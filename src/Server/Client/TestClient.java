package Server.Client;

import java.util.Vector;

import Server.Common.Command;

public class TestClient extends TCPClient {

    public TestClient() {
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
        for (int i = 0; i < 1000; i++ ) {
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
            addCustomerId.add("AddCustomer");
            addCustomerId.add(String.valueOf(i));
            addCustomerId.add(String.valueOf(i));
            try {
                execute(Command.AddFlight, addFlight);
                execute(Command.AddCars, addCars);
                execute(Command.AddRooms, addRooms);
                execute(Command.AddCustomer, addCustomer);
                execute(Command.AddCustomerID, addCustomerId);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}