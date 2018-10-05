package Server.Middleware;

import Server.Interface.*;
import Server.Common.*;
import java.rmi.NotBoundException;
import java.util.*;
import java.net.*;
import java.io.*;


public class TCPMiddlewareManager  {

    private static String[] serverNames = new String[4];
    private static String[] rmNames = new String[]{"Flights","Cars","Rooms","Customers"};
    private static int serverPort = 1608;
    Socket carRM;
    Socket flightRM;
    Socket hotelRM;
    Socket customerRM;
    static BufferedReader carIn;
    static BufferedReader flightIn;
    static BufferedReader hotelIn;
    static BufferedReader customerIn;
    static PrintWriter carOut;
    static PrintWriter flightOut;
    static PrintWriter hotelOut;
    static PrintWriter customerOut;
    static ServerSocket server;



    public static void main(String args[])
    {

        for (int i = 0; i < 4; i++){
            rmNames[i] = args[i];
        }

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            TCPMiddlewareManager mwm = new TCPMiddlewareManager();
            mwm.connectToRMs();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }



        try {
            server = new ServerSocket(serverPort);
            try {
                while (true) {
                    new ActiveConnection(server.accept()).start();
                    //Socket client = server.accept();

                    //BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                }
            } finally {
                server.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }


    }


    public void connectToRMs() throws UnknownHostException, IOException
    {
        try {
            carRM = new Socket(rmNames[0], serverPort);
            flightRM = new Socket(rmNames[1], serverPort);
            hotelRM = new Socket(rmNames[2], serverPort);
            customerRM = new Socket(rmNames[3], serverPort);
            System.out.println("Connected to " + carRM.getInetAddress()+","+flightRM.getInetAddress()+","+hotelRM.getInetAddress()+","+customerRM.getInetAddress());
        }catch(IOException e){
            e.printStackTrace();
            return;
        }

        carIn = new BufferedReader(new InputStreamReader(carRM.getInputStream()));
        flightIn = new BufferedReader(new InputStreamReader(flightRM.getInputStream()));
        hotelIn = new BufferedReader(new InputStreamReader(hotelRM.getInputStream()));
        customerIn = new BufferedReader(new InputStreamReader(customerRM.getInputStream()));

        carOut = new PrintWriter(carRM.getOutputStream(),true);
        flightOut = new PrintWriter(flightRM.getOutputStream(),true);
        hotelOut = new PrintWriter(hotelRM.getOutputStream(),true);
        customerOut = new PrintWriter(customerRM.getOutputStream(),true);
    }

    public TCPMiddlewareManager(){
        super();
    }

    private static class ActiveConnection extends Thread {
        Socket clientSocket;
        BufferedReader inC;
        PrintWriter outC;

        public ActiveConnection(Socket socket) {
            this.clientSocket = socket;
            System.out.println("Active Connection constructed");
            System.out.println("Client " + socket.getInetAddress() + "connected.");
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
                inC = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outC = new PrintWriter(clientSocket.getOutputStream(), true);
                Vector<String> arguments = new Vector<String>();
                while (true) {
                    String input = inC.readLine();
                    if (input.equals(".") || input.equals("")) {
                        continue;
                    }
                    if (input == null) {
                        break;
                    }
                    System.out.println("Middleware recieved: " + input);
                    arguments = parse(input);
                    Command cmd = Command.fromString((String) arguments.elementAt(0));
                    execute(cmd, arguments);
                }
            }catch (IOException e) {
                System.out.println("Error in TCPMiddlewareManager run()");
            }finally {
                try {
                    clientSocket.close();
                    inC.close();
                    outC.close();
                    carIn.close();
                    flightIn.close();
                    hotelIn.close();
                    customerIn.close();
                    carOut.close();
                    flightOut.close();
                    hotelOut.close();
                    customerOut.close();
                    server.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket");
                    try {
                        server.close();
                    }catch (IOException f) {
                        System.out.println("still Couldn't close a socket");
                    }

                }
                System.out.println("Connection with client closed");
            }
        }

        public void execute(Command cmd, Vector<String> arguments) throws IOException, NumberFormatException
        {
            switch (cmd)
            {
                case Help:
                {
                    break;
                }
                case AddFlight: {
                    checkArgumentsCount(5, arguments.size());

                    System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));
                    System.out.println("-Flight Seats: " + arguments.elementAt(3));
                    System.out.println("-Flight Price: " + arguments.elementAt(4));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);

                   /* String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String flightNum = arguments.elementAt(2);
                    String flightSeats = arguments.elementAt(3);
                    String flightPrice = arguments.elementAt(4);

                    String packet = commandName+","+id+","+flightNum+","+flightSeats+","+flightPrice;
                    */
                    flightOut.println(packet);
                    String response = flightIn.readLine();
                    outC.println(response);
                    break;
                }
                case AddCars: {
                    checkArgumentsCount(5, arguments.size());

                    System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));
                    System.out.println("-Number of Cars: " + arguments.elementAt(3));
                    System.out.println("-Car Price: " + arguments.elementAt(4));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
/*
                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);
                    String numCars = arguments.elementAt(3);
                    String price = arguments.elementAt(4);

                    String packet = commandName+","+id+","+location+","+numCars+","+price;
                    */
                    carOut.println(packet);
                    String response = carIn.readLine();
                    outC.println(response);
                    break;
                }
                case AddRooms: {
                    checkArgumentsCount(5, arguments.size());

                    System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));
                    System.out.println("-Number of Rooms: " + arguments.elementAt(3));
                    System.out.println("-Room Price: " + arguments.elementAt(4));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";
                    }
                    packet.substring(0, packet.length() - 1);
                    /*
                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);
                    String numRooms = arguments.elementAt(3);
                    String price = arguments.elementAt(4);

                    String packet = commandName+","+id+","+location+","+numRooms+","+price;
                    */
                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    outC.println(response);
                    break;
                }
                case AddCustomer: {
                    checkArgumentsCount(2, arguments.size());

                    System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

                    String commandName = "AddCustomerID";
                    String id = arguments.elementAt(1);
                    int newid = Integer.parseInt(String.valueOf(id) +
                            String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                            String.valueOf(Math.round(Math.random() * 100 + 1)));

                    String packet = commandName+","+id+","+Integer.toString(newid);

                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    System.out.println("Room response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    flightOut.println(packet);
                    response = flightIn.readLine();
                    System.out.println("Flight response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    carOut.println(packet);
                    response = carIn.readLine();
                    System.out.println("Car response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    customerOut.println(packet);
                    response = customerIn.readLine();
                    System.out.println("Customer response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    outC.println(response);
                    //System.out.println("Add customer ID: " + customer);
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

                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    System.out.println("Hotel response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    flightOut.println(packet);
                    response = flightIn.readLine();
                    System.out.println("Flight response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    carOut.println(packet);
                    response = carIn.readLine();
                    System.out.println("Car response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    customerOut.println(packet);
                    response = customerIn.readLine();
                    System.out.println("Customer response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }

                    outC.println(response);

                    break;
                }
                case DeleteFlight: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String flightNum = arguments.elementAt(2);

                    String packet = commandName+","+id+","+flightNum;
*/
                    flightOut.println(packet);
                    String response = flightIn.readLine();
                    outC.println(response);
                    break;
                }
                case DeleteCars: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*
                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);

                    String packet = commandName+","+id+","+location;
*/
                    carOut.println(packet);
                    String response = carIn.readLine();
                    outC.println(response);
                    break;
                }
                case DeleteRooms: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*
                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);

                    String packet = commandName+","+id+","+location;
*/
                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    outC.println(response);
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

                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    System.out.println("Room response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }
                    flightOut.println(packet);
                    response = flightIn.readLine();
                    System.out.println("Flight response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }
                    carOut.println(packet);
                    response = flightIn.readLine();
                    System.out.println("Car response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }
                    customerOut.println(packet);
                    response = customerIn.readLine();
                    System.out.println("Customer response"+response);
                    if (response.equals("false")){
                        outC.println(response);
                        break;
                    }
                    outC.println(response);
                    break;
                }
                case QueryFlight: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String flightNum = arguments.elementAt(2);
                    String packet = commandName+","+id+","+flightNum;
*/

                    // System.out.println("Number of seats available: " + seats);
                    flightOut.println(packet);
                    String response = flightIn.readLine();
                    outC.println(response);
                    break;
                }
                case QueryCars: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);

                    String packet = commandName+","+id+","+location;
*/
                    carOut.println(packet);
                    String response = carIn.readLine();
                    outC.println(response);
                    System.out.println("Number of cars at this location: " + response);
                    break;
                }
                case QueryRooms: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);

                    String packet = commandName+","+id+","+location;
                    */
                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    outC.println(response);
                    //System.out.println("Number of rooms at this location: " + numRoom);
                    break;
                }
                case QueryCustomer: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));

                    StringBuffer stringBuff = new StringBuffer();
                    String line = null;

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*


                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String customerID = arguments.elementAt(2);
                    String packet = commandName+","+id+","+customerID;
                    */

                    hotelOut.println(packet);
                    while((line = hotelIn.readLine()) != null){
                        System.out.println(line);
                        stringBuff.append(line);
                        if(!hotelIn.ready()){
                            break;
                        }
                    }
                    //stringBuff.append(System.lineSeparator());
                    //String respH = stringBuff.toString();
                    //System.out.println("respH"+respH);

                    flightOut.println(packet);
                    while((line = flightIn.readLine()) != null){
                        stringBuff.append(line);
                        if(!flightIn.ready()){
                            break;
                        }
                    }
                    //stringBuff.append(System.lineSeparator());
                    //String respF = stringBuff.toString();
                    //System.out.println("respF"+respF);

                    carOut.println(packet);
                    while((line = carIn.readLine()) != null){
                        stringBuff.append(line);
                        if(!carIn.ready()){
                            break;
                        }
                    }
                    //stringBuff.append(System.lineSeparator());
                    //line = stringBuff.toString();
                    String resp = stringBuff.toString();
                   // System.out.println(resp);


                    outC.println(resp);
                    break;
                }
                case QueryFlightPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Flight Number: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String flightNum = arguments.elementAt(2);
                    String packet = commandName+","+id+","+flightNum;
*/
                    flightOut.println(packet);
                    String response = flightIn.readLine();
                    outC.println(response);
                    //System.out.println("Price of a seat: " + price);
                    break;
                }
                case QueryCarsPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Car Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";
                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);
                    String packet = commandName+","+id+","+location;
*/
                    carOut.println(packet);
                    String response = carIn.readLine();
                    outC.println(response);
                    // System.out.println("Price of cars at this location: " + price);
                    break;
                }
                case QueryRoomsPrice: {
                    checkArgumentsCount(3, arguments.size());

                    System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Room Location: " + arguments.elementAt(2));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String location = arguments.elementAt(2);
                    String packet = commandName+","+id+","+location;
*/
                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    outC.println(response);
                    //System.out.println("Price of rooms at this location: " + price);
                    break;
                }
                case ReserveFlight: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Flight Number: " + arguments.elementAt(3));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String customerID = arguments.elementAt(2);
                    String flightNum = arguments.elementAt(3);

                    String packet = commandName+","+id+","+customerID+","+flightNum;
*/
                    flightOut.println(packet);
                    String response = flightIn.readLine();
                    outC.println(response);
                    break;
                }
                case ReserveCar: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Car Location: " + arguments.elementAt(3));

                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*

                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String customerID = arguments.elementAt(2);
                    String location = arguments.elementAt(3);

                    String packet = commandName+","+id+","+customerID+","+location;
*/
                    carOut.println(packet);
                    String response = carIn.readLine();
                    outC.println(response);

                    break;
                }
                case ReserveRoom: {
                    checkArgumentsCount(4, arguments.size());

                    System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));
                    System.out.println("-Room Location: " + arguments.elementAt(3));
                    String packetArray [] = arguments.toArray(new String[arguments.size()]);
                    String packet = "";
                    for (String argum : packetArray) {
                        packet = packet + argum + ",";

                    }
                    packet.substring(0, packet.length() - 1);
                    /*
                    String commandName = arguments.elementAt(0);
                    String id = arguments.elementAt(1);
                    String customerID = arguments.elementAt(2);
                    String location = arguments.elementAt(3);

                    String packet = commandName+","+id+","+customerID+","+location;
                    */
                    hotelOut.println(packet);
                    String response = hotelIn.readLine();
                    outC.println(response);
                    break;
                }
                case Bundle: {
                    if (arguments.size() < 7) {
                        System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
                        break;
                    }

                    System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
                    System.out.println("-Customer ID: " + arguments.elementAt(2));

                    String id = arguments.elementAt(1);
                    String fCom = "ReserveFlight,";
                    String customerID = arguments.elementAt(2);
                    String packet = id+","+customerID+",";
                    Boolean fSuc = true;

                    for (int i = 0; i < arguments.size() - 6; ++i)
                    {
                        System.out.println("-Flight Number: " + arguments.elementAt(3+i));
                        flightOut.println(fCom+packet+arguments.elementAt(3+i));
                        if((flightIn.readLine()).equals("false")) {
                            outC.println("false");
                            fSuc = false;
                            break;
                        }
                    }
                    if (fSuc == false) {
                        break;
                    }

                    String location = arguments.elementAt(arguments.size()-3);
                    String car = arguments.elementAt(arguments.size()-2);
                    String room = arguments.elementAt(arguments.size()-1);

                    System.out.println("Renting a car at Location: "+location+" "+car);
                    System.out.println("Renting a room at Location: "+location+" "+room);


                    if (car.equals("true")){
                        carOut.println("ReserveCar,"+packet+location);
                        if((carIn.readLine()).equals("false")) {
                            outC.println("false");
                            fSuc = false;
                            break;
                        }
                    }
                    if (room.equals("true")){
                        hotelOut.println("ReserveRoom,"+packet+location);
                        if((hotelIn.readLine()).equals("false")) {
                            outC.println("false");
                            fSuc = false;
                            break;
                        }
                    }
                    if (fSuc == false) {
                        break;
                    }
                    outC.println("true");

                    //packet = packet + location + car + room;
                   // customerOut.println(packet+"\n");
                    //String response = customerIn.readLine();
                   // System.out.println(response);
                    break;
                }
                case Quit:
                    break;
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