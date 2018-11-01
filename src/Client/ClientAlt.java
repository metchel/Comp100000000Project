package Client;

import Server.Interface.*;
import Server.Network.*;

import java.util.*;
import java.io.*;
import java.net.*;


public abstract class ClientAlt
{
    //IResourceManager m_resourceManager = null;
    Socket server = null;
    BufferedReader in;
    PrintWriter out;
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
                /*catch (ConnectException e) {
                    connectServer();
                    execute(cmd, arguments);
                }*/
            }
            catch (IllegalArgumentException e) {
                System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0m" + e.getLocalizedMessage());
            }
            /*catch (ConnectIOException e) {
                System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mConnection to server lost");
            }*/
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
            case AddFlight: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));
                System.out.println("-Flight Seats: " + arguments.elementAt(3));
                System.out.println("-Flight Price: " + arguments.elementAt(4));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);
                String flightSeats = arguments.elementAt(3);
                String flightPrice = arguments.elementAt(4);
                String packet = commandName+","+id+","+flightNum+","+flightSeats+","+flightPrice;

                Request req = new Request(packet);

                oos.writeObject(req);
                //RequestBuilder rmb = new RequestBuilder();   
                //RequestMessage rm = rmb.withCommand(commandName).inXId(id).withArgument(flightNum).withArgument(flightSeats).withArgument(flightPrice).build();

                //String packet = rm.toString();
                
                Response response = (Response) ois.readObject();

                System.out.println("RESPONSE: " + response.getMessage());
                //System.out.println(response);
                if (response.getMessage().equals("true")){
                    System.out.println("Flight added");
                }else{
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);
                String numCars = arguments.elementAt(3);
                String price = arguments.elementAt(4);

                String packet = commandName+","+id+","+location+","+numCars+","+price;
                Request req = new Request(packet);

                oos.writeObject(req);
                //out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
                    System.out.println("Cars added");
                }else{
                    System.out.println("Cars could not be added");
                }
                break;
            }
            case AddRooms: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));
                System.out.println("-Number of Rooms: " + arguments.elementAt(3));
                System.out.println("-Room Price: " + arguments.elementAt(4));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);
                String numRooms = arguments.elementAt(3);
                String price = arguments.elementAt(4);

                String packet = commandName+","+id+","+location+","+numRooms+","+price;

                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
                    System.out.println("Rooms added");
                }else{
                    System.out.println("Rooms could not be added");
                }
                break;
            }
            case AddCustomer: {
                checkArgumentsCount(2, arguments.size());

                System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);

                String packet = commandName+","+id;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Add customer ID: " + id);
                break;
            }
            case AddCustomerID: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);

                String packet = commandName+","+id+","+customerID;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
                    System.out.println("Add customer ID: " + customerID);
                }else{
                    System.out.println("Customer could not be added");
                }

                break;
            }
            case DeleteFlight: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);

                String packet = commandName+","+id+","+flightNum;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);

                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
                    System.out.println("Cars Deleted");
                }else{
                    System.out.println("Cars could not be deleted");
                }
                break;
            }
            case DeleteRooms: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);

                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);

                String packet = commandName+","+id+","+customerID;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);
                String packet = commandName+","+id+","+flightNum;


                out.println(packet+"\n");
                String response = in.readLine();
                System.out.println("Number of seats available: "+response);
                //System.out.println(response);
                break;
            }
            case QueryCars: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);

                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Number of cars at this location: "+response);
               // System.out.println("Number of cars at this location: " + numCars);
                break;
            }
            case QueryRooms: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);

                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Number of rooms at this location: "+response);
                //System.out.println("Number of rooms at this location: " + numRoom);
                break;
            }
            case QueryCustomer: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);
                String packet = commandName+","+id+","+customerID;
                out.println(packet+"\n");
                String response = in.readLine();
                String keyword = "Bill for customer "+customerID;
                String test = response.replaceAll(keyword,"\n");
                System.out.println(keyword+"\n"+test);
                //System.out.print(bill);
                break;
            }
            case QueryFlightPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String flightNum = arguments.elementAt(2);
                String packet = commandName+","+id+","+flightNum;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Price of a seat: " + response);
                break;
            }
            case QueryCarsPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);
                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Price of cars at this location: " + response);
                break;
            }
            case QueryRoomsPrice: {
                checkArgumentsCount(3, arguments.size());

                System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Room Location: " + arguments.elementAt(2));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String location = arguments.elementAt(2);
                String packet = commandName+","+id+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                System.out.println("Price of rooms at this location: " + response);
                break;
            }
            case ReserveFlight: {
                checkArgumentsCount(4, arguments.size());

                System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Customer ID: " + arguments.elementAt(2));
                System.out.println("-Flight Number: " + arguments.elementAt(3));

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);
                String flightNum = arguments.elementAt(3);

                String packet = commandName+","+id+","+customerID+","+flightNum;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);
                String location = arguments.elementAt(3);

                String packet = commandName+","+id+","+customerID+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

                String commandName = arguments.elementAt(0);
                String id = arguments.elementAt(1);
                String customerID = arguments.elementAt(2);
                String location = arguments.elementAt(3);

                String packet = commandName+","+id+","+customerID+","+location;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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
                String packet = commandName+","+id+","+customerID;


                for (int i = 0; i < arguments.size() - 6; ++i)
                {
                    packet = packet+","+arguments.elementAt(3+i);
                }
                String location = arguments.elementAt(arguments.size()-3);
                String car = arguments.elementAt(arguments.size()-2);
                String room = arguments.elementAt(arguments.size()-1);

                packet = packet+","+location+","+car+","+room;
                out.println(packet+"\n");
                String response = in.readLine();
                //System.out.println(response);
                if (response.equals("true")){
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

