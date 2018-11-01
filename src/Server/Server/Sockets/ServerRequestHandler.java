package Server.Sockets;

import Server.ResourceManager.SocketResourceManager;
import Server.Interface.IResourceManager;
import Server.Common.Command;

import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;

public class ServerRequestHandler implements RequestHandler {
    private SocketResourceManager resourceManager;

    public ServerRequestHandler(SocketResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Response handle(Request req) throws IOException, ClassNotFoundException {
        Vector<String> arguments = this.parse(req.getMessage());
        Command COMMAND = Command.fromString(arguments.get(0));
        String res = Boolean.toString(execute(COMMAND, arguments));

        Response response = new Response(res);
        System.out.println("RESPONSE: " + response.getMessage());

        return response;
    }

    public Vector<String> parse(String command) {
        Vector<String> arguments = new Vector<String>();
        StringTokenizer tokenizer = new StringTokenizer(command,",");
        String argument = "";
        while (tokenizer.hasMoreTokens())
        {
            argument = tokenizer.nextToken();
            argument = argument.trim();
            arguments.add(argument);
        }
        return arguments;
    }

    public boolean execute(Command cmd, Vector<String> arguments) throws IOException {
        switch (cmd) {
            case Help: {
                break;
            }
            case AddFlight: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));
                System.out.println("-Flight Seats: " + arguments.elementAt(3));
                System.out.println("-Flight Price: " + arguments.elementAt(4));

                int id = toInt(arguments.elementAt(1));
                int flightNum = toInt(arguments.elementAt(2));
                int flightSeats = toInt(arguments.elementAt(3));
                int flightPrice = toInt(arguments.elementAt(4));

                if (resourceManager.addFlight(id, flightNum, flightSeats, flightPrice)) {
                    System.out.println("Flight added");
                    return true;
                } else {
                    System.out.println("Flight could not be added");
                    return false;
                }
            }
            case AddCars: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));
                System.out.println("-Number of Cars: " + arguments.elementAt(3));
                System.out.println("-Car Price: " + arguments.elementAt(4));

                int id = toInt(arguments.elementAt(1));
                String location = arguments.elementAt(2);
                int numCars = toInt(arguments.elementAt(3));
                int price = toInt(arguments.elementAt(4));

                if (resourceManager.addCars(id, location, numCars, price)) {
                    System.out.println("Cars added");
                    return true;
                } else {
                    System.out.println("Cars could not be added");
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