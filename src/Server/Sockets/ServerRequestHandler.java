package Server.Sockets;

import Server.Interface.IResourceManager;
import Server.Common.Command;
import Server.Network.*;
import Server.ResourceManager.TransactionResourceManager;
import Server.Common.RMHashMap;
import Server.Common.Trace;

import java.io.IOException;
import java.util.Vector;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Date;

public class ServerRequestHandler implements RequestHandler {
    private final TransactionResourceManager resourceManager;


    public ServerRequestHandler(TransactionResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public synchronized Response handle(Request req) throws IOException, ClassNotFoundException {
        Trace.info(req.xIdToString());
        Boolean resStatus = null;
        String message = null;
        RequestData data = req.getData();
        Integer xId = data.getXId();

        if (req instanceof CanCommitRequest) {
            Trace.info("CanCommitRequest Received.");
            Map cm = resourceManager.getCrashMap();
            System.out.println("Cm:"+cm.toString());

            if ((Boolean) resourceManager.getCrashMap().get(1)) {
                System.exit(1);
            }
            resStatus = resourceManager.prepare(xId);
            if ((Boolean) resourceManager.getCrashMap().get(2)) {
                System.exit(1);
            }

            if (resStatus) {
                message = "Successfully prepared transaction " + xId.toString();
            } else {
                message = "Failed to prepare transaction " + xId.toString();
            }

            Response res = new Response();

            if ((Boolean) cm.get(3)) {
                return res.addCurrentTimeStamp()
                        .addStatus(resStatus)
                        .addMessage("3");
            } else {
                return res.addCurrentTimeStamp()
                        .addStatus(resStatus)
                        .addMessage(message);
            }

        }

        if (req instanceof DoCommitRequest) {
            Trace.info("DoCommitRequest Received.");
            System.out.println("Cm:"+resourceManager.getCrashMap().toString());

            if ((Boolean) resourceManager.getCrashMap().get(4)) {
                System.exit(1);
            }

            System.out.println("NPE on get crashmap");
            resStatus = resourceManager.commit(xId);
            if (resStatus) {
                message = "Successfully commited transaction " + xId.toString();
            } else {
                message = "Failed to commit transaction " + xId.toString();
            }

            Response res = new Response();
            return res.addCurrentTimeStamp()
                .addStatus(resStatus)
                .addMessage(message);
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
                if ((Boolean) resourceManager.getCrashMap().get(4)){
                    System.exit(1);
                }
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



    public int toInt(String string) throws NumberFormatException {
        return (new Integer(string)).intValue();
    }

    public boolean toBoolean(String string) {
        return (new Boolean(string)).booleanValue();
    }
}