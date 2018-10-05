package Server.RMI;

import Server.Interface.*;
import Server.Common.*;
import java.rmi.RemoteException;
import java.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPResourceManager extends ResourceManager {
    private static String serverName = "Server";
    private static int serverPort = 1604;
    static ServerSocket server;

    //private static String s_rmiPrefix = "group30";

    public static void main(String args[]) {
        try {
            server = new ServerSocket(serverPort);
            try {
                while (true) {
                    new ActiveConnection(server.accept()).start();
                    System.out.println("Server is running and is accepting new connections.");
                }
            } finally {
                server.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public TCPResourceManager(String name) {
        super(name);
    }

    private static class ActiveConnection extends Thread {
        Socket clientSocket;
        TCPResourceManager rm = new TCPResourceManager(serverName);
        BufferedReader in;
        PrintWriter out;

        public ActiveConnection(Socket socket) {
            this.clientSocket = socket;
            System.out.println("Active Connection constructor!");
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

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                Vector<String> arguments = new Vector<String>();
                while(true){
                    String input = in.readLine();
                    if (input == null || input.equals(".") || input.equals("")) {
                        continue;
                    }
                    System.out.println("Server recieved: "+input);
                    arguments = parse(input);
                    Command cmd = Command.fromString((String)arguments.elementAt(0));
                    execute(cmd,arguments);
                }
            }catch (IOException e) {
                System.out.println("Error in TCPResourceManager run()");
            }finally {
                try {
                    clientSocket.close();
                    in.close();
                    out.close();
                    server.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket");
                }
                System.out.println("Connection with client closed");
            }

        }

        public void execute(Command cmd, Vector<String> arguments) throws RemoteException
        {
            switch (cmd)
            {
                case Help:
                {
                /*if (arguments.size() == 1) {
                    System.out.println(Command.description());
                } else if (arguments.size() == 2) {
                    Command l_cmd = Command.fromString((String)arguments.elementAt(1));
                    System.out.println(l_cmd.toString());
                } else {
                    System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
                }*/
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

                    if (rm.addFlight(id, flightNum, flightSeats, flightPrice)) {
                        System.out.println("Flight added");
                        out.println("true");
                    } else {
                        System.out.println("Flight could not be added");
                        out.println("false");
                    }
                    break;
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

                    if (rm.addCars(id, location, numCars, price)) {
                        System.out.println("Cars added");
                        out.println("true");
                    } else {
                        System.out.println("Cars could not be added");
                        out.println("false");
                    }
                    break;
                }
                case AddRooms: {
                    checkArgumentsCount(5, arguments.size());

                    System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));
                    System.out.println("-Number of Rooms: " + arguments.elementAt(3));
                    System.out.println("-Room Price: " + arguments.elementAt(4));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);
                    int numRooms = toInt(arguments.elementAt(3));
                    int price = toInt(arguments.elementAt(4));

                    if (rm.addRooms(id, location, numRooms, price)) {
                        System.out.println("Rooms added");
                        out.println("true");
                    } else {
                        System.out.println("Rooms could not be added");
                        out.println("false");
                    }
                    break;
                }
                case AddCustomer: {
                    checkArgumentsCount(2, arguments.size());

                    System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

                    int id = toInt(arguments.elementAt(1));
                   /* int customerID = Integer.parseInt(String.valueOf(id) +
                            String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                            String.valueOf(Math.round(Math.random() * 100 + 1)));*/

                    int customer = rm.newCustomer(id);
                    /*if (rm.newCustomer(id, customerID)) {
                        System.out.println("Add customer ID: " + customerID);
                        out.println("true");
                    } else {
                        System.out.println("Customer could not be added");
                        out.println("false");
                    }*/

                    System.out.println("Add customer ID: " + customer);
                    out.println("true");
                    break;
                }
                case AddCustomerID: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));

                    if (rm.newCustomer(id, customerID)) {
                        System.out.println("Add customer ID: " + customerID);
                        out.println("true");
                    } else {
                        System.out.println("Customer could not be added");
                        out.println("false");
                    }
                    break;
                }
                case DeleteFlight: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int flightNum = toInt(arguments.elementAt(2));

                    if (rm.deleteFlight(id, flightNum)) {
                        System.out.println("Flight Deleted");
                        out.println("true");
                    } else {
                        System.out.println("Flight could not be deleted");
                        out.println("false");
                    }
                    break;
                }
                case DeleteCars: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    if (rm.deleteCars(id, location)) {
                        System.out.println("Cars Deleted");
                        out.println("true");
                    } else {
                        System.out.println("Cars could not be deleted");
                        out.println("false");
                    }
                    break;
                }
                case DeleteRooms: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    if (rm.deleteRooms(id, location)) {
                        System.out.println("Rooms Deleted");
                        out.println("true");
                    } else {
                        System.out.println("Rooms could not be deleted");
                        out.println("false");
                    }
                    break;
                }
                case DeleteCustomer: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));

                    if (rm.deleteCustomer(id, customerID)) {
                        System.out.println("Customer Deleted");
                        out.println("true");
                    } else {
                        System.out.println("Customer could not be deleted");
                        out.println("false");
                    }
                    break;
                }
                case QueryFlight: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int flightNum = toInt(arguments.elementAt(2));

                    int seats = rm.queryFlight(id, flightNum);
                    System.out.println("Number of seats available: " + seats);
                    out.println(Integer.toString(seats));
                    break;
                }
                case QueryCars: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    int numCars = rm.queryCars(id, location);
                    System.out.println("Number of cars at this location: " + numCars);
                    out.println(Integer.toString(numCars));
                    break;
                }
                case QueryRooms: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    int numRoom = rm.queryRooms(id, location);
                    System.out.println("Number of rooms at this location: " + numRoom);
                    out.println(Integer.toString(numRoom));
                    break;
                }
                case QueryCustomer: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));

                    String bill = rm.queryCustomerInfo(id, customerID);
                    System.out.print(bill);
                    out.println(bill);
                    break;
                }
                case QueryFlightPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    int flightNum = toInt(arguments.elementAt(2));

                    int price = rm.queryFlightPrice(id, flightNum);
                    System.out.println("Price of a seat: " + price);
                    out.println(Integer.toString(price));
                    break;
                }
                case QueryCarsPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    int price = rm.queryCarsPrice(id, location);
                    System.out.println("Price of cars at this location: " + price);
                    out.println(Integer.toString(price));
                    break;
                }
                case QueryRoomsPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));

                    int id = toInt(arguments.elementAt(1));
                    String location = arguments.elementAt(2);

                    int price = rm.queryRoomsPrice(id, location);
                    System.out.println("Price of rooms at this location: " + price);
                    out.println(Integer.toString(price));
                    break;
                }
                case ReserveFlight: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Flight Number: " + arguments.elementAt(3));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));
                    int flightNum = toInt(arguments.elementAt(3));

                    if (rm.reserveFlight(id, customerID, flightNum)) {
                        System.out.println("Flight Reserved");
                        out.println("true");
                    } else {
                        System.out.println("Flight could not be reserved");
                        out.println("false");
                    }
                    break;
                }
                case ReserveCar: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Car Location: " + arguments.elementAt(3));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));
                    String location = arguments.elementAt(3);

                    if (rm.reserveCar(id, customerID, location)) {
                        System.out.println("Car Reserved");
                        out.println("true");
                    } else {
                        System.out.println("Car could not be reserved");
                        out.println("false");
                    }
                    break;
                }
                case ReserveRoom: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Room Location: " + arguments.elementAt(3));

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));
                    String location = arguments.elementAt(3);

                    if (rm.reserveRoom(id, customerID, location)) {
                        System.out.println("Room Reserved");
                        out.println("true");
                    } else {
                        System.out.println("Room could not be reserved");
                        out.println("false");
                    }
                    break;
                }
                case Bundle: {
                    System.out.println("Shouldnt be here");
                    /*
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

                    int id = toInt(arguments.elementAt(1));
                    int customerID = toInt(arguments.elementAt(2));
                    Vector<String> flightNumbers = new Vector<String>();

                    for (int i = 0; i < arguments.size() - 6; ++i)
                    {
                        flightNumbers.addElement(arguments.elementAt(3+i));
                    }


                    String location = arguments.elementAt(arguments.size()-3);
                    boolean car = toBoolean(arguments.elementAt(arguments.size()-2));
                    boolean room = toBoolean(arguments.elementAt(arguments.size()-1));

                    if (rm.bundle(id, customerID, flightNumbers, location, car, room)) {
                        System.out.println("Bundle Reserved");
                        out.println("true");
                    } else {
                        System.out.println("Bundle could not be reserved");
                        out.println("false");
                    }
                    */
                    break;

                }
                case Quit:
                    checkArgumentsCount(1, arguments.size());

                    System.out.println("Quitting client");
                    System.exit(0);
            }
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
}




