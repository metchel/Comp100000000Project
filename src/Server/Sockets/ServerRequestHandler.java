package Server.Sockets;

import Server.ResourceManager.SocketResourceManager;
import Server.Interface.IResourceManager;
import Server.Common.Command;
import Server.Network.*;

import java.io.IOException;
import java.util.Vector;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Date;

public class ServerRequestHandler implements RequestHandler {
    private SocketResourceManager resourceManager;

    public ServerRequestHandler(SocketResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Response handle(Request req) throws IOException, ClassNotFoundException {
        Map<String, Object> arguments = ((RequestData)req.getData()).getCommandArgs();
        RequestData data = req.getData();
        Command cmd = data.getCommand();
        Integer xId = data.getXId();

        System.out.println(req.toString());

        Object result = execute(xId, cmd, arguments);
        Boolean resStatus = null;
        String message = null;
        if (result instanceof Integer) {
            resStatus = new Boolean(true);
            message = result.toString();
        }
        if (result instanceof Boolean) {
            resStatus = (Boolean)result;
            message = result.toString();
        }

        Response response = new Response();
        response.addCurrentTimeStamp()
            .addStatus(resStatus)
            .addMessage(message);

        System.out.println(response.toString());

        return response;
    }

    public Object execute(Integer xId, Command cmd, Map<String, Object> arguments) throws IOException {
        switch (cmd) {
            case Help: {
                return new Boolean(false);
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

                return new Boolean(resourceManager.addRooms(xId, roomLoc, numRooms.intValue(), roomPrice.intValue()));
            }

            case AddCustomer: {
                return new Integer(resourceManager.newCustomer(xId));
            }

            case AddCustomerID: {
                Integer cId = (Integer)arguments.get("cId");
                return new Boolean(resourceManager.newCustomer(xId, cId.intValue()));
            }

            case DeleteFlight: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Boolean(resourceManager.deleteFlight(xId, flightNum.intValue()));
            }

            case DeleteCars: {
                String carLoc = (String)arguments.get("carLoc");
                return new Boolean(resourceManager.deleteCars(xId, carLoc));
            }

            case DeleteRooms: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Boolean(resourceManager.deleteRooms(xId, roomLoc));
            }

            case QueryFlight: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Integer(resourceManager.queryFlight(xId, flightNum.intValue()));
            }

            case QueryCars: {
                String carLoc = (String)arguments.get("carLoc");
                return new Integer(resourceManager.queryCars(xId, carLoc));
            }

            case QueryRooms: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Integer(resourceManager.queryRooms(xId, roomLoc));
            }

            case QueryFlightPrice: {
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Integer(resourceManager.queryFlightPrice(xId, flightNum.intValue()));
            }

            case QueryCarsPrice: {
                String carLoc = (String)arguments.get("carLoc");
                return new Integer(resourceManager.queryCarsPrice(xId, carLoc));
            }

            case QueryRoomsPrice: {
                String roomLoc = (String)arguments.get("roomLoc");
                return new Integer(resourceManager.queryRoomsPrice(xId, roomLoc));
            }

            case QueryCustomer: {
                return new Boolean(false);
            }

            case DeleteCustomer: {
                return new Boolean(false);
            }

            case ReserveFlight: {
                Integer cId = (Integer)arguments.get("cId");
                Integer flightNum = (Integer)arguments.get("flightNum");
                return new Boolean(resourceManager.reserveFlight(xId, cId, flightNum));
            }

            case ReserveCar: {
                Integer cId = (Integer)arguments.get("cId");
                String carLoc = (String)arguments.get("carLoc");
                return new Boolean(resourceManager.reserveCar(xId, cId, carLoc));
            }

            case ReserveRoom: {
                Integer cId = (Integer)arguments.get("cId");
                String roomLoc = (String)arguments.get("roomLoc");
                return new Boolean(resourceManager.reserveRoom(xId, cId, roomLoc));
            }

            case Bundle: {
                return new Boolean(false);
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