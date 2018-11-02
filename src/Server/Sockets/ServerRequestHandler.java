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
        Map<String, Object> arguments = req.getData().getCommandArgs();
        Command cmd = req.getData().getCommand();
        Integer xId = req.getData().getXId();

        boolean resStatus = execute(cmd, arguments);

        Response response = new Response();
        response.addCurrentTimeStamp()
            .addStatus(resStatus)
            .addMessage("IDK");

        System.out.println("RESPONSE: " + response.toString());

        return response;
    }

    public boolean execute(Command cmd, Map<String, Object> arguments) throws IOException {
        switch (cmd) {
            case Help: {
                break;
            }

            case AddFlight: {
                checkArgumentsCount(5, arguments.keySet().size());

                System.out.println("Adding a new flight [xid=" + arguments.get("xId").toString() + "]");
                System.out.println("-Flight Number: " + arguments.get("flightNum").toString());
                System.out.println("-Flight Seats: " + arguments.get("flightSeats").toString());
                System.out.println("-Flight Price: " + arguments.get("flightPrice").toString());

                int id = ((Integer)arguments.get("xId")).intValue();
                int flightNum = ((Integer)arguments.get("flightNum")).intValue();
                int flightSeats = ((Integer)arguments.get("flightSeats")).intValue();
                int flightPrice = ((Integer)arguments.get("flightPrice")).intValue();

                if (resourceManager.addFlight(id, flightNum, flightSeats, flightPrice)) {
                    System.out.println("Flight added");
                    return true;
                } else {
                    System.out.println("Flight could not be added");
                    return false;
                }
            }
        }

        return false;
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