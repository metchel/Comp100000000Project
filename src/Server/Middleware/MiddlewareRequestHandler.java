package Server.Middleware;

import Server.Network.Request;
import Server.Network.RequestData;
import Server.Network.Response;
import Server.ResourceManager.SocketResourceManager;
import Server.ResourceManager.TransactionResourceManager;
import Server.Common.Command;
import Server.Common.Constants;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.ArrayList;

import Server.Sockets.RequestHandler;

public class MiddlewareRequestHandler implements RequestHandler {
    private final MiddlewareClient flightClient;
    private final MiddlewareClient carClient;
    private final MiddlewareClient roomClient;

    private final TransactionResourceManager customerResourceManager;
    private final MiddlewareCoordinator coordinator;

    private static final String CUSTOMER = Constants.CUSTOMER;
    private static final String FLIGHT = Constants.FLIGHT;
    private static final String ROOM = Constants.ROOM;
    private static final String CAR = Constants.CAR;

    public MiddlewareRequestHandler(TransactionResourceManager customerResourceManager, MiddlewareCoordinator coordinator, MiddlewareClient flightClient, MiddlewareClient carClient, MiddlewareClient roomClient) throws IOException, ClassNotFoundException {
        this.customerResourceManager = customerResourceManager;
        this.coordinator = coordinator;
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;
    }

    public synchronized Response handle(Request request) throws IOException, ClassNotFoundException {
        final RequestData data = (RequestData) request.getData();
        final Command command = data.getCommand();
        Response response = new Response();

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

                Request clone = new Request();
                RequestData informClients = new RequestData();
                informClients.addXId(new Integer(nextTransactionId))
                    .addCommand(Command.Start);
                clone.addData(informClients);
                this.flightClient.send(clone);
                Response flightResponse = this.flightClient.receive();
                this.carClient.send(clone);
                Response carResponse = this.carClient.receive();
                this.roomClient.send(clone);
                Response roomResponse = this.roomClient.receive();

                response.addCurrentTimeStamp()
                    .addStatus(new Boolean(true))
                    .addMessage(Integer.toString(nextTransactionId));
                break;
            }
            case Commit: {
                Integer xId = data.getXId();
                Set<String> servers = this.coordinator.getTransactionRms(xId);
                boolean commitSuccess = true;
                for (String server: servers) {
                    if (server.equals(FLIGHT)) {
                        this.flightClient.send(request);
                        Response flightResponse = this.flightClient.receive();
                        commitSuccess = commitSuccess && flightResponse.getStatus().booleanValue();
                        continue;
                    } else if(server.equals(CAR)) {
                        this.carClient.send(request);
                        Response carResponse = this.carClient.receive();
                        commitSuccess = commitSuccess && carResponse.getStatus().booleanValue();
                        continue;
                    } else if (server.equals(ROOM)) {
                        this.roomClient.send(request);
                        Response roomResponse = this.roomClient.receive();
                        commitSuccess = commitSuccess && roomResponse.getStatus().booleanValue();
                        continue;
                    } else if (server.equals(CUSTOMER)) {
                        boolean customerSuccess = this.customerResourceManager.commit(xId);
                        commitSuccess = commitSuccess && customerSuccess;
                    }
                }

                if (commitSuccess) {
                    this.coordinator.commit(xId.intValue());
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
                break;
            }
            /**
             * Read only operations
             */
            case QueryFlight: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, FLIGHT);
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCars: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CAR);
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRooms: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, ROOM);
                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case QueryCustomer: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CUSTOMER);

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
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCarsPrice: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRoomsPrice: {
                Integer xId = data.getXId();
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
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }

            case AddCars: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }

            case AddRooms: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, ROOM);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }


            case AddCustomer: {
                Integer xId = data.getXId();
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
                this.coordinator.addOperation(xId, CUSTOMER);
                this.coordinator.addOperation(xId, FLIGHT);
                this.coordinator.addOperation(xId, CAR);
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                boolean resStatus = this.customerResourceManager.newCustomer(xId, cId);
                Boolean resStatusBoolean = new Boolean(resStatus);

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
                break;
            }
            case DeleteFlight: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, FLIGHT);

                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case DeleteCars: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CAR);

                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case DeleteRooms: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, ROOM);

                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case DeleteCustomer: {
                Integer xId = data.getXId();
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
                this.coordinator.addOperation(xId, FLIGHT);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.flightClient.send(request);
                    response = this.flightClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }

            case ReserveCar: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, CAR);
                
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.carClient.send(request);
                    response = this.carClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case ReserveRoom: {
                Integer xId = data.getXId();
                this.coordinator.addOperation(xId, ROOM);

                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.roomClient.send(request);
                    response = this.roomClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case Bundle: {
                Integer xId = data.getXId();
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
                // reserve flights
                final ArrayList<Response> flightReserveResponses = new ArrayList<Response>();
                for (Integer flightNum: flightNumList) {
                    Request req = generateReservationRequest(xId, Command.ReserveFlight, cId, "flightNum", flightNum);
                    this.flightClient.send(req);
                    Response reserveResponse = this.flightClient.receive();
                    flightReserveResponses.add(response);
                    successResponse = successResponse && reserveResponse.getStatus().booleanValue();
                }

                // reserve car
                if (car.equals("1")) {
                    final Request carReservation = generateReservationRequest(xId, Command.ReserveCar, cId, "location", location);
                    this.carClient.send(carReservation);
                    final Response carResponse = this.carClient.receive();
                    successResponse = successResponse && carResponse.getStatus().booleanValue();
                }

                //reserve room
                if (room.equals("1")) {
                    final Request roomReservation = generateReservationRequest(xId, Command.ReserveRoom, cId, "location", location);
                    this.roomClient.send(roomReservation);
                    final Response roomResponse = this.roomClient.receive();
                    successResponse = successResponse && roomResponse.getStatus().booleanValue();
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
}