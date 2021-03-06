package Server.Middleware;

import Server.Network.Request;
import Server.Network.RequestData;
import Server.Network.Response;
import Server.Network.CommitSuccessResponse;
import Server.Network.AskDecisionRequest;
import Server.ResourceManager.SocketResourceManager;
import Server.ResourceManager.CustomerResourceManager;
import Server.Common.Command;
import Server.Common.Constants;
import Server.Common.Customer;
import Server.Common.RMHashMap;
import Server.Common.Trace;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.ArrayList;
import java.util.Stack;

import Server.Sockets.RequestHandler;
import Server.Transactions.ReserveOperation;
import Server.Transactions.Operation.OperationType;

public class MiddlewareRequestHandler implements RequestHandler {
    private final Socket client;
    private final MiddlewareResourceManager flightClient;
    private final MiddlewareResourceManager carClient;
    private final MiddlewareResourceManager roomClient;

    private final CustomerResourceManager customerResourceManager;
    private final MiddlewareCoordinator coordinator;

    private static final String CUSTOMER = Constants.CUSTOMER;
    private static final String FLIGHT = Constants.FLIGHT;
    private static final String ROOM = Constants.ROOM;
    private static final String CAR = Constants.CAR;

    public MiddlewareRequestHandler(Socket client,
    CustomerResourceManager customerResourceManager, 
    MiddlewareCoordinator coordinator, 
    MiddlewareResourceManager flightClient,
    MiddlewareResourceManager carClient,
    MiddlewareResourceManager roomClient) throws IOException, ClassNotFoundException {
        this.client = client;
        this.customerResourceManager = customerResourceManager;
        this.coordinator = coordinator;
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;
    }

    public synchronized Response handle(Request request) throws IOException, ClassNotFoundException {
        final RequestData data = (RequestData) request.getData();
        final int xid = request.getData().getXId();

        if (request instanceof AskDecisionRequest) {
            Trace.info("Decision has been asked for...");
            Transaction.Status decision = this.coordinator.getStatus(xid);
            if (decision.equals(Transaction.Status.ABORTED)) {
                return new CommitSuccessResponse(xid, false);
            }
            if (decision.equals(Transaction.Status.COMMITTED)) {
                return new CommitSuccessResponse(xid, true);
            }
            
        }

        final Command command = data.getCommand();
        Response response = new Response();

        MiddlewareResourceManager[] clients = {flightClient, carClient, roomClient};

        Trace.info(request.toString());

        switch (command) {
            case Help: {
                break;
            }
            /**
             * Transaction related operations
             */
            case Start: {
                int nextTransactionId = coordinator.start();

                this.customerResourceManager.start(nextTransactionId);
                boolean success = true;
                for (MiddlewareResourceManager client: clients) {
                    success = success && client.start(nextTransactionId);
                }
                if (success) {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(true))
                        .addMessage(Integer.toString(nextTransactionId));
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Could not start transaction.");
                }
                break;
            }
            case Commit: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }

                if (!this.coordinator.hasStarted(xId)) {
                    Trace.info("Transaction hasn't started.");
                    break;
                }


                if (this.coordinator.commit(xId)) {
                    response.addCurrentTimeStamp()
                        .addStatus(true)
                        .addMessage("Transaction " + xId + " committed.");
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(false)
                        .addMessage("Transaction " + xId + " not committed.");
                }
                break;
            }
            case Abort: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                if (this.coordinator.abort(xId)) {
                    response.addCurrentTimeStamp()
                        .addStatus(true)
                        .addMessage("Transaction " + xId + " aborted.");
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(false)
                        .addMessage("Transaction " + xId + " not aborted.");
                }
                break;
            }

            case Shutdown: {
                this.flightClient.send(request);
                this.carClient.send(request);
                this.roomClient.send(request);
                
                System.out.println("Gracefully shutting down...");
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
            /**
             * Crash operations
             */
            case CrashMiddleware: {
                int tmp = (Integer) data.getCommandArgs().get("mode");
                Trace.info("CrashMidd:" + tmp);
                Trace.info("Telling the MW Coordinator to crash");
                this.coordinator.forceCrash(tmp);
                response.addCurrentTimeStamp()
                        .addStatus(true)
                        .addMessage("Middleware crash mode set");
                break;
            }
            case CrashFlightRM: {
                this.flightClient.send(request);
                //Response res = this.roomClient.receive();
                response = this.flightClient.receive();
                Trace.info("Telling the Flight RM to crash");
                break;
            }
            case CrashCarRM: {
                this.carClient.send(request);
                //Response res = this.roomClient.receive();
                response = this.carClient.receive();
                Trace.info("Telling the Room RM to crash");
                break;
            }

            case CrashRoomRM: {
                this.roomClient.send(request);
                //Response res = this.roomClient.receive();
                response = this.roomClient.receive();
                Trace.info("Telling the Hotel RM to crash");
                break;
            }

            case ResetCrash: {
                this.roomClient.send(request);
                this.carClient.send(request);
                this.flightClient.send(request);
                Trace.info("Resetting Crashes");
                this.coordinator.resetCrashes();
                response.addCurrentTimeStamp()
                        .addStatus(true)
                        .addMessage("Reset the crashes");
                break;
            }

            /**
             * Read only operations
             */
            case QueryFlight: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, FLIGHT);
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCars: {
                Integer xId = data.getXId();
                this.coordinator.updateTransactionTtl(xId);
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.addOperation(xId, CAR);
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRooms: {
                Integer xId = data.getXId();
                this.coordinator.updateTransactionTtl(xId);
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.addOperation(xId, ROOM);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case QueryCustomer: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                Boolean resStatus = new Boolean(false);
                String message = "Customer does not exist.";
                if (info != null && info != "") {
                    resStatus = new Boolean(true);
                    message = info;
                }
                response.addCurrentTimeStamp()
                    .addStatus(resStatus)
                    .addMessage(message);
                break;
            }
            case QueryFlightPrice: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCarsPrice: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRoomsPrice: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CAR);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }

            /**
             * Write only operations
             */
            case AddFlight: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }

            case AddCars: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }

            case AddRooms: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, ROOM);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }


            case AddCustomer: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                int newId = this.customerResourceManager.newCustomer(xId.intValue());
                
                if (newId != -1) { 
                    Integer newIdInteger = new Integer(newId);
                    Request clone = new Request();
                    RequestData informClients = new RequestData();
                    informClients.addXId(xId)
                        .addCommand(Command.AddCustomerID)
                        .addArgument("cId", newId);
                    clone.addCurrentTimeStamp()
                        .addData(informClients);
                    this.flightClient.send(clone);
                    Response flightResponse = this.flightClient.receive();
                    this.carClient.send(clone);
                    Response carResponse = this.carClient.receive();
                    this.roomClient.send(clone);
                    Response roomResponse = this.roomClient.receive();
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(true))
                        .addMessage(newIdInteger.toString());
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage(new Integer(-1).toString());
                }
                break;
            }
            case AddCustomerID: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                boolean resStatus = this.customerResourceManager.newCustomer(xId, cId);
                Boolean resStatusBoolean = new Boolean(resStatus);

                if (resStatus) {
                    Request clone = new Request();
                    clone.addCurrentTimeStamp()
                        .addData(data);
                    this.flightClient.send(clone);
                    Response flightResponse = this.flightClient.receive();
                    this.carClient.send(clone);
                    Response carResponse = this.carClient.receive();
                    this.roomClient.send(clone);
                    Response roomResponse = this.roomClient.receive();
                    boolean informClientSuccess = resStatus 
                        && flightResponse.getStatus().booleanValue() 
                        && carResponse.getStatus().booleanValue()
                        && roomResponse.getStatus().booleanValue(); 
                    
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(informClientSuccess))
                        .addMessage(Boolean.toString(informClientSuccess));
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer could not be added.");
                }
                break;
            }
            case DeleteFlight: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case DeleteCars: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case DeleteRooms: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, ROOM);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case DeleteCustomer: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                boolean resStatus = this.customerResourceManager.deleteCustomer(xId, cId);

                this.flightClient.send(request);
                Response flightResponse = this.flightClient.receive();
                this.carClient.send(request);
                Response carResponse = this.carClient.receive();
                this.roomClient.send(request);
                Response roomResponse = this.roomClient.receive();

                Boolean resStatusBoolean = new Boolean(resStatus);
                response.addCurrentTimeStamp()
                    .addStatus(resStatusBoolean)
                    .addMessage(resStatusBoolean.toString());
                break;
            }

            /**
             * Read and Write operations
             */
            case ReserveFlight: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                if (this.customerResourceManager.lockCustomer(xId.intValue(), cId.intValue())) {
                    this.customerResourceManager.addReserveFlightOp(xId.intValue(), cId.intValue());
                    this.flightClient.send(request);
                    response = this.flightClient.receive();
                    final RMHashMap reservationData = response.getReservationData();
                    this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }

            case ReserveCar: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, CAR);
                
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                if (this.customerResourceManager.lockCustomer(xId.intValue(), cId.intValue())) {
                    this.customerResourceManager.addReserveCarOp(xId.intValue(), cId.intValue());
                    this.carClient.send(request);
                    response = this.carClient.receive();
                    final RMHashMap reservationData = response.getReservationData();
                    this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case ReserveRoom: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                if (this.customerResourceManager.lockCustomer(xId.intValue(), cId.intValue())) {
                    this.customerResourceManager.addReserveRoomOp(xId.intValue(), cId.intValue());
                    this.roomClient.send(request);
                    response = this.roomClient.receive();
                    final RMHashMap reservationData = response.getReservationData();
                    this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case Bundle: {
                Integer xId = data.getXId();
                if (!this.coordinator.exists(xId)) {
                    Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    response.addStatus(new Boolean(false));
                    response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
                    break;
                }
                this.coordinator.updateTransactionTtl(xId);
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                ArrayList<Integer> flightNumList = (ArrayList)data.getCommandArgs().get("flightNumList");
                String location = (String)data.getCommandArgs().get("location");
                String car = (String)data.getCommandArgs().get("car");
                String room = (String)data.getCommandArgs().get("room");

                boolean successResponse = true;

                final ArrayList<Response> flightReserveResponses = new ArrayList<Response>();
                for (Integer flightNum: flightNumList) {
                    this.customerResourceManager.addReserveFlightOp(xId.intValue(), cId.intValue());
                    Request req = generateReservationRequest(xId, Command.ReserveFlight, cId, "flightNum", flightNum);
                    this.flightClient.send(req);
                    Response flightResponse = this.flightClient.receive();

                    if (flightResponse.getStatus().booleanValue()) {
                        final RMHashMap reservationData = flightResponse.getReservationData();
                        this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                    } else {
                        successResponse = false;
                    }
                }

                // reserve car
                if (successResponse && car.equals("1")) {
                    this.customerResourceManager.addReserveCarOp(xId.intValue(), cId.intValue());
                    final Request carReservation = generateReservationRequest(xId, Command.ReserveCar, cId, "carLoc", location);
                    this.carClient.send(carReservation);
                    final Response carResponse = this.carClient.receive();

                    if (carResponse.getStatus().booleanValue()) {
                        final RMHashMap reservationData = carResponse.getReservationData();
                        this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                    } else {
                        successResponse = false;
                    }
                }

                //reserve room
                if (successResponse && room.equals("1")) {
                    this.customerResourceManager.addReserveCarOp(xId.intValue(), cId.intValue());

                    final Request roomReservation = generateReservationRequest(xId, Command.ReserveRoom, cId, "roomLoc", location);
                    this.roomClient.send(roomReservation);
                    final Response roomResponse = this.roomClient.receive();

                    if (roomResponse.getStatus().booleanValue()) {
                        final RMHashMap reservationData = roomResponse.getReservationData();
                        this.customerResourceManager.updateReservationData(xId, cId, reservationData);
                    } else {
                        successResponse = false;
                    }
                }

                if(successResponse) {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(true))
                        .addMessage("Bundle successfully reserved.");
                } else {

                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Bundle reservation failed.");
                }
                break;
            }
        }

        return response;
    }

    public Request generateReservationRequest(Integer xId, Command command, Integer cId, String key, Object value) {
        Request reservation = new Request();
        RequestData data = new RequestData();
        data.addXId(xId.intValue())
            .addCommand(command)
            .addArgument("cId", cId)
            .addArgument(key, value);
        reservation.addCurrentTimeStamp()
            .addData(data);

        return reservation;
    }

    public Response forceAbort(Integer xId) throws IOException, ClassNotFoundException {

        Response response = new Response();
        final RequestData data = new RequestData();
        data.addXId(xId)
            .addCommand(Command.Abort);
        final Request request = new Request();
        request.addData(data);

        if (!this.coordinator.exists(xId)) {
            Trace.info("Transaction " + xId + " doesn't exist or has already commited/aborted.");
            response.addStatus(new Boolean(false));
            response.addMessage("Transaction " + xId + " doesn't exist or has already commited/aborted.");
            return response;
        }
        this.coordinator.abort(xId.intValue());
        Set<String> servers = this.coordinator.getTransactionRms(xId);
        boolean abortSuccess = true;
        for (String server: servers) {
            if (server.equals(FLIGHT)) {
                this.flightClient.send(request);
                Response flightResponse = this.flightClient.receive();
                abortSuccess = abortSuccess && flightResponse.getStatus().booleanValue();
                continue;
            } else if(server.equals(CAR)) {
                this.carClient.send(request);
                Response carResponse = this.carClient.receive();
                abortSuccess = abortSuccess && carResponse.getStatus().booleanValue();
                continue;
            } else if (server.equals(ROOM)) {
                this.roomClient.send(request);
                Response roomResponse = this.roomClient.receive();
                abortSuccess = abortSuccess && roomResponse.getStatus().booleanValue();
                continue;
            } else if (server.equals(CUSTOMER)) {
                boolean customerResponse = this.customerResourceManager.abort(xId);
                abortSuccess = abortSuccess && customerResponse;
            } else {
                System.out.println("Something has gone terribly wrong.");
            }
        }
        if (abortSuccess) {
            this.coordinator.abort(xId);
            response.addCurrentTimeStamp()
                .addStatus(true)
                .addMessage("Transaction " + xId + " aborted.");
        } else {
            response.addCurrentTimeStamp()
                .addStatus(false)
                .addMessage("Transaction " + xId + " not aborted.");
        }
        return response;
    }
}