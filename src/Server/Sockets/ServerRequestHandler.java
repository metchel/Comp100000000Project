package Server.Sockets;

import Server.Interface.IResourceManager;
import Server.Common.Command;
import Server.Network.*;
import Server.ResourceManager.ItemResourceManager;
import Server.Common.RMHashMap;
import Server.Common.Trace;
import Server.Middleware.MiddlewareResourceManager;


import java.io.IOException;
import java.util.Vector;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Date;

public class ServerRequestHandler implements RequestHandler {
    private final ItemResourceManager resourceManager;
    private final CoordinatorStub coordinator;
    private final Map<String, Integer> participants;

    public ServerRequestHandler(ItemResourceManager resourceManager, CoordinatorStub coordinator) {
        this.resourceManager = resourceManager;
        this.coordinator = coordinator;
        this.participants = new HashMap<String, Integer>();
    }

    public synchronized Response handle(Request req) throws IOException, ClassNotFoundException {
        if (req instanceof InformGroupRequest) {
            Trace.info("INFORM GROUP RECEIEVED");
            InformGroupRequest groupReq = (InformGroupRequest)req;
            participants.put(groupReq.getAddress(), groupReq.getPort());
            Trace.info("RECIEVED INFORMATION ABOUT PARTICIPANTS, in case of termination protocol." + this.participants.toString());
            Response resp = new Response();
            return resp.addCurrentTimeStamp().addStatus(true).addMessage("Informed");
        }
        Trace.info(req.xIdToString());
        Boolean resStatus = null;
        String message = null;
        RequestData data = req.getData();
        Integer xId = data.getXId();

        if (req instanceof CanCommitRequest) {
            Trace.info("CanCommitRequest Received.");
            Map cm = resourceManager.getCrashMap();
            System.out.println("Cm:"+cm.toString());

            if ((Boolean) cm.get(1)) {
                System.exit(1);
            }
            resStatus = resourceManager.prepare(xId);
            if ((Boolean) cm.get(2)) {
                Trace.info("Crash Mode 2");
                System.exit(1);
            }

            /**
             * The uncertainty period
             */
            Thread t = new Thread(() -> {
                long now = System.currentTimeMillis();
                while (System.currentTimeMillis() < now + 5000) {
                    if (this.resourceManager.getStatus(xId) == "COMMITTED" 
                    || this.resourceManager.getStatus(xId).equals("ABORTED")) {
                        // decision reached, no timeout.
                        return;
                    }
                }
                // timeout. detect Middleware failure
                Trace.info("BLOCK RM, DETECTED COORDINATOR FAILIURE. NO DECISION RECIEVED.");
                // run termination protocol
            });
            t.start();
            if ((Boolean) cm.get(3)) {
                if (resStatus) {
                    return new VoteResponse(xId.intValue(), "YES").addMessage("CRASH 3");
                } else {
                    return new VoteResponse(xId.intValue(), "NO").addMessage("CRASH 3");
                }
            }
            if (resStatus) {
                return new VoteResponse(xId.intValue(), "YES");
            } else {
                return new VoteResponse(xId.intValue(), "NO");
            }
        }

        if (req instanceof DoCommitRequest) {
            Trace.info("DoCommitRequest Received.");
            System.out.println("Cm:"+resourceManager.getCrashMap().toString());

            if ((Boolean) resourceManager.getCrashMap().get(4)) {
                System.exit(1);
            }

            resStatus = resourceManager.commit(xId);
            if (resStatus) {
                message = "Successfully commited transaction " + xId.toString();
            } else {
                message = "Failed to commit transaction " + xId.toString();
            }

            if (resStatus) {
                return new CommitSuccessResponse(xId.intValue(), true);
            } else {
                return new CommitSuccessResponse(xId.intValue(), false);
            }
        }

        Map<String, Object> arguments = (data.getCommandArgs());
        Command cmd = data.getCommand();

        Trace.info(req.toString());

        Object result = execute(xId, cmd, arguments);
        if (result instanceof Integer) {
            if(((Integer)result).intValue() == -1) {
                resStatus = new Boolean(false);
            } else {
                resStatus = new Boolean(true);
            }
            message = result.toString();
        }
        if (result instanceof Boolean) {
            resStatus = (Boolean)result;
            message = result.toString();
        }
        if (result instanceof RMHashMap) {
            resStatus = new Boolean(true);
            message = "Reservation booked.";
        }
        
        if (result == null) {
            resStatus = new Boolean(false);
            message = "Reservation failed";
        }
        Response response = new Response();
        response.addCurrentTimeStamp()
            .addStatus(resStatus)
            .addMessage(message);
        
        if (result instanceof RMHashMap) {
            response.addReservationData(result);
        }

        return response;
    }

    public synchronized Object execute(Integer xId, Command cmd, Map<String, Object> arguments) throws IOException {
        switch (cmd) {
            case Help: {
                return new Boolean(false);
            }

            case Start: {
                return new Boolean(resourceManager.start(xId.intValue()));
            }

            case Commit: {
                return new Boolean(resourceManager.commit(xId.intValue()));
            }

            case Abort: {
                return new Boolean(resourceManager.abort(xId.intValue()));
            }

            case CrashFlightRM: {
                Integer mode = (Integer)arguments.get("mode");
                System.out.println("Been told to Crash mode "+mode);
                return new Boolean(resourceManager.forceCrash(mode));

            }
            case CrashCarRM: {
                Integer mode = (Integer)arguments.get("mode");
                System.out.println("Been told to Crash mode "+mode);
                return new Boolean(resourceManager.forceCrash(mode));
            }

            case CrashRoomRM: {
                Integer mode = (Integer)arguments.get("mode");
                System.out.println("Been told to Crash mode "+mode);
                return new Boolean(resourceManager.forceCrash(mode));
            }

            case Shutdown: {
                System.out.println("Gracefully shutting down...");
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(1);
            }

            case AddFlight: {

                Integer flightNum = (Integer)arguments.get("flightNum");
                Integer flightSeats = (Integer)arguments.get("flightSeats");
                Integer flightPrice = (Integer)arguments.get("flightPrice");

                return new Boolean(resourceManager.addFlight(xId.intValue(), flightNum.intValue(), flightSeats.intValue(), flightPrice.intValue()));
            }

            case AddCars: {
                String carLoc = (String)arguments.get("carLoc");
                Integer numCars = (Integer)arguments.get("numCars");
                Integer carPrice = (Integer)arguments.get("carPrice");

                return new Boolean(resourceManager.addCars(xId.intValue(), carLoc, numCars.intValue(), carPrice.intValue()));
            }

            case AddRooms: {
                String roomLoc = (String)arguments.get("roomLoc");
                Integer numRooms = (Integer)arguments.get("numRooms");
                Integer roomPrice = (Integer)arguments.get("roomPrice");

                return new Boolean(resourceManager.addRooms(xId.intValue(), roomLoc, numRooms.intValue(), roomPrice.intValue()));
            }

            /**
             * This should never be called to the server directly.
             * Called at the Middleware:
             *  - Middleware generates new Id.
             *  - Middleware calls forwards newId through AddCustomerId request.
             *  - data replication of customers at all ResourceManagers.
             */
            case AddCustomer: {
                return new Boolean(false);
            }

            case AddCustomerID: {
                Integer cId = (Integer)arguments.get("cId");
                return new Boolean(resourceManager.newCustomer(xId.intValue(), cId.intValue()));
            }

            case DeleteFlight: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Boolean(resourceManager.deleteFlight(xId.intValue(), flightNum.intValue()));
            }

            case DeleteCars: {
                String carLoc = (String)arguments.get("carLoc");
                return new Boolean(resourceManager.deleteCars(xId.intValue(), carLoc));
            }

            case DeleteRooms: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Boolean(resourceManager.deleteRooms(xId.intValue(), roomLoc));
            }

            case QueryFlight: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Integer(resourceManager.queryFlight(xId.intValue(), flightNum.intValue()));
            }

            case QueryCars: {
                String carLoc = (String)arguments.get("carLoc");
                return new Integer(resourceManager.queryCars(xId.intValue(), carLoc));
            }

            case QueryRooms: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Integer(resourceManager.queryRooms(xId.intValue(), roomLoc));
            }

            case QueryFlightPrice: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Integer(resourceManager.queryFlightPrice(xId.intValue(), flightNum.intValue()));
            }

            case QueryCarsPrice: {
                String carLoc = (String)arguments.get("carLoc");
                return new Integer(resourceManager.queryCarsPrice(xId.intValue(), carLoc));
            }

            case QueryRoomsPrice: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Integer(resourceManager.queryRoomsPrice(xId.intValue(), roomLoc));
            }

            case QueryCustomer: {
                Integer cId = (Integer)arguments.get("cId");
                return new String(resourceManager.queryCustomerInfo(xId.intValue(), cId.intValue()));
            }

            case DeleteCustomer: {
                Integer cId = (Integer)arguments.get("cId");
                return new Boolean(resourceManager.deleteCustomer(xId.intValue(), cId.intValue()));
            }

            case ReserveFlight: {
                Integer cId = (Integer)arguments.get("cId");
                Integer flightNum = (Integer)arguments.get("flightNum");
                return resourceManager.reserveFlight(xId.intValue(), cId.intValue(), flightNum.intValue());
            }

            case ReserveCar: {
                Integer cId = (Integer)arguments.get("cId");
                String carLoc = (String)arguments.get("carLoc");
                return resourceManager.reserveCar(xId.intValue(), cId.intValue(), carLoc);
            }

            case ReserveRoom: {
                Integer cId = (Integer)arguments.get("cId");
                String roomLoc = (String)arguments.get("roomLoc");
                return resourceManager.reserveRoom(xId.intValue(), cId.intValue(), roomLoc);
            }

            case Bundle: {
                Integer cId = (Integer)arguments.get("cId");
                resourceManager.undoLastReservation(xId, cId);
                return new Boolean(true);
            }

            case Quit: {
                return new Boolean(false);
            }
        }

        return new Boolean(false);
    }

    public void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException {
        if (expected != actual) {
            throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received " + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
        }
    }

    public void runTerminationProtocol() {

    }


    public int toInt(String string) throws NumberFormatException {
        return (new Integer(string)).intValue();
    }

    public boolean toBoolean(String string) {
        return (new Boolean(string)).booleanValue();
    }
}