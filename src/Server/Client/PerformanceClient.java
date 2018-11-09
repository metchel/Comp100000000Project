package Server.Client;

import java.util.Vector;

import Server.Common.Command;

public class PerformanceClient extends TCPClient {


    public PerformanceClient() {
        super();

    }
    public static void main(String[] args) {

        try {
            TestClient client = new TestClient();
            client.connectServer();
            client.start();
        }

        catch (Exception e) {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void start() {

        /**
         * Add some stuff
         */
        File file = new File("results.csv");
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile);

        Vector<String> start = new Vector<String>();


        for (int i = 0; i < 1000; i++ ) {
            String[][] resultArray = new String[10][3];
            Vector<String> addFlight = new Vector<String>();
            addFlight.add("AddFlight");
            addFlight.add(String.valueOf(i));
            addFlight.add(String.valueOf(i));
            addFlight.add(String.valueOf(i));
            addFlight.add(String.valueOf(i));

            Vector<String> addCars = new Vector<String>();
            addCars.add("AddCars");
            addCars.add(String.valueOf(i));
            addCars.add(String.valueOf(i));
            addCars.add(String.valueOf(i));
            addCars.add(String.valueOf(i));

            Vector<String> addRooms = new Vector<String>();
            addRooms.add("AddRooms");
            addRooms.add(String.valueOf(i));
            addRooms.add(String.valueOf(i));
            addRooms.add(String.valueOf(i));
            addRooms.add(String.valueOf(i));

            Vector<String> addCustomer = new Vector<String>();
            addCustomer.add("AddCustomer");
            addCustomer.add(String.valueOf(i));

            Vector<String> addCustomerId = new Vector<String>();
            addCustomerId.add("AddCustomerID");
            addCustomerId.add(String.valueOf(i));
            addCustomerId.add(String.valueOf(i));

            Vector<String> commit = new Vector<String>();
            commit.add(String.valueOf(i));

            Vector<String> abort = new Vector<String>();
            abort.add(String.valueOf(i));

            try {
                int i = 0;


                long startTime = System.currentTimeMillis();
                execute(Command.Start, start);
                long finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.AddFlight, addFlight);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.AddCars, addCars);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.AddRooms, addRooms);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.AddCustomer, addCustomer);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.AddCustomerID, addCustomerId);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

                startTime = System.currentTimeMillis();
                execute(Command.Commit, commit);
                finishTime = System.currentTimeMillis();
                resultArray[i] = {Long.toString(finishTime - startTime)};
                i++;

            } catch(Exception e) {
                writer.close();
                e.printStackTrace();
            }
        }

        for (String[] resultAr : resultArray){
            writer.writeNext(resultAr);
        }

        writer.close();
    }

    @Override
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


                break;
            }
            case Quit:
                checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);
        }
    }

}