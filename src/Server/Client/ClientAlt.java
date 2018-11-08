package Server.Client;

import Server.Interface.*;
import Server.Network.*;
import Server.Common.Command;

import java.util.*;
import java.io.*;
import java.net.*;


public abstract class ClientAlt
{
    //IResourceManager m_resourceManager = null;
    Socket server = null;
    PrintWriter out = null;
    BufferedReader in = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    public ClientAlt()
    {
        super();
    }

    public abstract void connectServer() throws UnknownHostException, IOException;

    public void start()
    {
        // Prepare for reading commands
        System.out.println();
        System.out.println("Location \"help\" for list of supported commands");

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            // Read the next command
            String command = "";
            Vector<String> arguments = new Vector<String>();
            try {
                System.out.print((char)27 + "[32;1m\n>] " + (char)27 + "[0m");
                command = stdin.readLine().trim();
            }
            catch (IOException io) {
                System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0m" + io.getLocalizedMessage());
                io.printStackTrace();
                System.exit(1);
            }

            try {
                arguments = parse(command);
                Command cmd = Command.fromString((String)arguments.elementAt(0));
                try {
                    execute(cmd, arguments);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
            catch (IllegalArgumentException e) {
                System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0m" + e.getLocalizedMessage());
            }

            catch (Exception e) {
                System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mUncaught exception");
                e.printStackTrace();
            }
        }
    }

    public void execute(Command cmd, Vector<String> arguments) throws IOException, NumberFormatException, ClassNotFoundException
    {
        switch (cmd)
        {
            case Help:
            {
                if (arguments.size() == 1) {
                    System.out.println(Command.description());
                } else if (arguments.size() == 2) {
                    Command l_cmd = Command.fromString((String)arguments.elementAt(1));
                    System.out.println(l_cmd.toString());
                } else {
                    System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
                }
                break;
            }

            case Start: {
                checkArgumentsCount(1, arguments.size());

                System.out.println("Starting transaction.");

                RequestData data = new RequestData();
                data.addXId(-1)
                    .addCommand(cmd);
                
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Transaction started with xid " + response.getMessage());
                } else {
                    System.out.println("Transaction could not be started.");
                }

                break;
            }

            case Commit: {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Commit request for transaction " + arguments.elementAt(1));

                String id = arguments.elementAt(1);
                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd);
                
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Transaction committed." + response.getMessage());
                } else {
                    System.out.println("Transaction could not commit.");
                }

                break;
            }

            case Abort: {checkArgumentsCount(2, arguments.size());

                System.out.println("Abort request for transaction " + arguments.elementAt(1));

                String id = arguments.elementAt(1);
                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd);
                
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Transaction aborted." + response.getMessage());
                } else {
                    System.out.println("Transaction could not abort. Shutting down.");
                }

                break;
            }
            case AddFlight: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));
                System.out.println("-Flight Seats: " + arguments.elementAt(3));
                System.out.println("-Flight Price: " + arguments.elementAt(4));

                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);
                String flightSeats = arguments.elementAt(3);
                String flightPrice = arguments.elementAt(4);
                
                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("flightNum", Integer.parseInt(flightNum))
                    .addArgument("flightSeats", Integer.parseInt(flightSeats))
                    .addArgument("flightPrice", Integer.parseInt(flightPrice));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Flight added");
                } else {
                    System.out.println("Flight could not be added");
                }

                break;
            }
            case AddCars: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));
                System.out.println("-Number of Cars: " + arguments.elementAt(3));
                System.out.println("-Car Price: " + arguments.elementAt(4));

                String id = arguments.elementAt(1);
                String carLoc = arguments.elementAt(2);
                String numCars = arguments.elementAt(3);
                String carPrice = arguments.elementAt(4);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("carLoc", carLoc)
                    .addArgument("numCars", Integer.parseInt(numCars))
                    .addArgument("carPrice", Integer.parseInt(carPrice));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Car added");
                } else {
                    System.out.println("Car could not be added");
                }
                break;
            }
            case AddRooms: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));
                System.out.println("-Number of Rooms: " + arguments.elementAt(3));
                System.out.println("-Room Price: " + arguments.elementAt(4));

                String id = arguments.elementAt(1);
                String roomLoc = arguments.elementAt(2);
                String numRooms = arguments.elementAt(3);
                String roomPrice = arguments.elementAt(4);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("roomLoc", roomLoc)
                    .addArgument("numRooms", Integer.parseInt(numRooms))
                    .addArgument("roomPrice", Integer.parseInt(roomPrice));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Room added");
                } else {
                    System.out.println("Room could not be added");
                }
                break;
            }
            case AddCustomer: {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

                String id = arguments.elementAt(1);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                break;
            }
            case AddCustomerID: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Add customer ID: " + cId);
                } else {
                    System.out.println("Customer could not be added");
                }

                break;
            }
            case DeleteFlight: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("flightNum", Integer.parseInt(flightNum));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()){
                    System.out.println("Flight Deleted");
                }else{
                    System.out.println("Flight could not be deleted");
                }
                break;
            }
            case DeleteCars: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String carLoc = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("carLoc", carLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()){
                    System.out.println("Cars Deleted");
                }else{
                    System.out.println("Cars could not be deleted");
                }
                break;
            }
            case DeleteRooms: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String roomLoc = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("roomLoc", roomLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()){
                    System.out.println("Rooms Deleted");
                }else{
                    System.out.println("Rooms could not be deleted");
                }
                break;
            }
            case DeleteCustomer: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()){
                    System.out.println("Customer Deleted");
                }else{
                    System.out.println("Customer could not be deleted");
                }
                break;
            }
            case QueryFlight: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("flightNum", Integer.parseInt(flightNum));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Number of seats available: " + response.toString());
                break;
            }
            case QueryCars: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String carLoc = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("carLoc", carLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Number of cars at this location: " + response.toString());
                break;
            }
            case QueryRooms: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String roomLoc = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("roomLoc", roomLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Number of rooms at this location: " + response.toString());
                break;
            }
            case QueryCustomer: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                break;
            }
            case QueryFlightPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("flightNum", Integer.parseInt(flightNum));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Price of a seat: " + response.toString());
                break;
            }
            case QueryCarsPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));
                String id = arguments.elementAt(1);
                String carLoc = arguments.elementAt(2);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("carLoc", carLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Price of cars at this location: " + response.toString());
                break;
            }
            case QueryRoomsPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));

                String id = arguments.elementAt(1);
                String roomLoc = arguments.elementAt(2);
                
                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("roomLoc", roomLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());
                System.out.println("Price of rooms at this location: " + response.toString());
                break;
            }
            case ReserveFlight: {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));
                System.out.println("-Flight Number: " + arguments.elementAt(3));

                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);
                String flightNum = arguments.elementAt(3);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId))
                    .addArgument("flightNum", Integer.parseInt(flightNum));
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Flight Reserved");
                }else{
                    System.out.println("Flight could not be reserved");
                }
                break;
            }
            case ReserveCar: {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));
                System.out.println("-Car Location: " + arguments.elementAt(3));
                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);
                String carLoc = arguments.elementAt(3);


                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId))
                    .addArgument("carLoc", carLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Car Reserved");
                }else{
                    System.out.println("Car could not be reserved");
                }
                break;
            }
            case ReserveRoom: {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));
                System.out.println("-Room Location: " + arguments.elementAt(3));

                String id = arguments.elementAt(1);
                String cId = arguments.elementAt(2);
                String roomLoc = arguments.elementAt(3);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(cId))
                    .addArgument("roomLoc", roomLoc);
        
                Request req = new Request();
                req.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(req);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Room Reserved");
                }else{
                    System.out.println("Room could not be reserved");
                }
                break;
            }
            case Bundle: {
                if (arguments.size() < 7) {
                    System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
                    break;
                }

                System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));
                for (int i = 0; i < arguments.size() - 6; ++i)
                {
                    System.out.println("-Flight Number: " + arguments.elementAt(3+i));
                }
                System.out.println("-Car Location: " + arguments.elementAt(arguments.size()-2));
                System.out.println("-Room Location: " + arguments.elementAt(arguments.size()-1));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);

                ArrayList<Integer> flightNumList = new ArrayList<Integer>();
                for (int i = 0; i < arguments.size() - 6; ++i)
                {
                    flightNumList.add(Integer.parseInt(arguments.elementAt(3+i)));
                }
                String location = arguments.elementAt(arguments.size()-3);
                String car = arguments.elementAt(arguments.size()-2);
                String room = arguments.elementAt(arguments.size()-1);

                RequestData data = new RequestData();
                data.addXId(Integer.parseInt(id))
                    .addCommand(cmd)
                    .addArgument("cId", Integer.parseInt(customerID))
                    .addArgument("flightNumList", flightNumList)
                    .addArgument("location", location)
                    .addArgument("car", car)
                    .addArgument("room", room);

                Request request = new Request();
                request.addCurrentTimeStamp()
                    .addData(data);

                oos.writeObject(request);
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.toString());

                if (response.getStatus()) {
                    System.out.println("Bundle Reserved");
                }else{
                    System.out.println("Bundle could not be reserved");
                }
                break;
            }
            case Quit:
                checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);
        }
    }

    public static Vector<String> parse(String command)
    {
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

    public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException
    {
        if (expected != actual)
        {
            throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received " + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
        }
    }

    public static int toInt(String string) throws NumberFormatException
    {
        return (new Integer(string)).intValue();
    }

    public static boolean toBoolean(String string)// throws Exception
    {
        return (new Boolean(string)).booleanValue();
    }
}

