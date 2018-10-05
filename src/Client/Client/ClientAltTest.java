package Client;

import Server.Interface.*;

import java.util.*;
import java.io.*;
import java.net.*;


public abstract class ClientAltTest
{
    //IResourceManager m_resourceManager = null;
    Socket server = null;
    BufferedReader in;
    PrintWriter out;

    public ClientAltTest()
    {
        super();
    }

    public abstract void connectServer() throws UnknownHostException, IOException;

    public void start()
    {
        // Prepare for reading commands
        System.out.println();
        System.out.println("Location \"help\" for list of supported commands");

        //BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            // Read the next command
            String command = "";
            Vector<String> arguments = new Vector<String>();
            Vector<String> argumentsTwo = new Vector<String>();
            String[][] testone_commands = {{"AddCustomerID","10","111"},{"AddFlight","10","737","1000","499"},
                    {"AddRooms","10","Montreal","500","99"},{"AddCars","10","Montreal","500","49"},{"Bundle","10","111","737","Montreal","true","true"},
                    {"QueryCustomer","10","111"}};

            String[][] testtwo_commands = {{"QueryRooms","10","Montreal"}};

            String[][] testthree_commands = {{"DeleteFlight","10","737"},{"Bundle","10","111","737","Montreal","false","false"}};

            System.out.print((char)27 + "[32;1m\n>] " + (char)27 + "[0m");
                //command = stdin.readLine().trim();

            Vector<String> test_oneresults = new Vector<String>();
            Vector<String> t1 = new Vector<String>();
                for (int i = 0; i < testone_commands.length; i++){
                    commands = testone_commands[i];
                    for (String arg : commands){

                        t1.add(arg);
                    }
                    Command cmd = Command.fromString((String)t1.elementAt(0));
                    try {
                        test_oneresults.add(execute(cmd, t1));
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }


            if (test_oneresults.get(5) != ("Bill for customer 69,\n" +
                "1 room-mass $342,\n" +
                "1 flight-999 $123,\n" +
                "1 car-mass $123,")){
                System.out.println("Did not pass test one");
            }
            else {
                System.out.println("Passed test one.");
            }


            Vector<String> test_tworesults = new Vector<String>();
            Vector<String> t2 = new Vector<String>();


            for (int i = 0; i < testone_commands.length; i++){
                    commands = testtwo_commands[i];
                    for (String arg : commands) {

                        t2.add(arg);
                    }
                    Command cmd = Command.fromString((String) t2.elementAt(0));
                    try {
                        test_tworesults.add(execute(cmd, t2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            if (test_tworesults.get(0) != "499"){
                System.out.println("Did not pass test two");
            }
            else {
                System.out.println("Passed test two.");
            }

            Vector<String> test_threeresults = new Vector<String>();
            Vector<String> t3 = new Vector<String>();
            for (int i = 0; i < testthree_commands.length; i++){
                commands = testthree_commands[i];
                    for (String arg : commands){

                        t3.add(arg);

                    }
                    Command cmd = Command.fromString((String)t3.elementAt(0));
                    try {
                        test_threeresults.add(execute(cmd, t3));
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

            if (test_threeresults.get(1) != "false"){
                System.out.println("Did not pass test three");
            }
            else {
                System.out.println("Passed test three.");
            }

        }
    }

    public String execute(Command cmd, Vector<String> arguments) throws IOException, NumberFormatException
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
                out.println(packet+"\n");
                String response = in.readLine();
                return response;


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
                out.println(packet+"\n");
                String response = in.readLine();
                return response;

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
                return response;

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
                return response;

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
                return response;

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
                return response;

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
                return response;

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
                return response;

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
                return response;

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
                return response;
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
                return response;
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
                return response;
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
                System.out.println(keyword+","+test);
                //System.out.print(bill);
                return response;
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
                return response;
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
                return response;
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
                return response;
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
                return response;
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
                return response;
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
                return response;
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
                return response;
            }
            case Quit:
                checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);
        }
        return "";
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

