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
        Command cmd = ((RequestData)req.getData()).getCommand();
        Integer xId = req.getData().getXId();

        System.out.println(req.toString());

        Boolean resStatus = execute(xId, cmd, arguments);

        Response response = new Response();
        response.addCurrentTimeStamp()
            .addStatus(resStatus)
            .addMessage("");

        System.out.println(response.toString());

        return response;
    }

    public Boolean execute(Integer xId, Command cmd, Map<String, Object> arguments) throws IOException {
        switch (cmd) {
            case Help: {
                break;
            }

            case AddFlight: {

                int flightNum = ((Integer)arguments.get("flightNum")).intValue();
                int flightSeats = ((Integer)arguments.get("flightSeats")).intValue();
                int flightPrice = ((Integer)arguments.get("flightPrice")).intValue();

                try {
                    Thread.sleep(3000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }

                if (resourceManager.addFlight(xId, flightNum, flightSeats, flightPrice)) {
                    System.out.println("Flight added");
                    return new Boolean(true);
                } else {
                    System.out.println("Flight could not be added");
                    return new Boolean(false);
                }
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