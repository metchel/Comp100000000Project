package Server.Client;

import java.util.Vector;
import java.util.Timer;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import java.net.UnknownHostException;

import Server.Common.Command;

public class TestClient extends TCPClient implements Runnable{

    private static volatile int transactionCount;
    private static String serverHost;
    private static int serverPort;
    private static volatile int numClients = 1;
    private final FileWriter writer;
    private final File file = new File("result.csv");

    public TestClient(String host, int port) throws IOException {
        super();
        serverHost = host;
        serverPort = port;
        this.writer = new FileWriter(file.getAbsoluteFile(), true);
    }

    public static void main(String[] a) {
        try {
            TestClient test = new TestClient(a[0], Integer.parseInt(a[1]));
            Thread t0 = new Thread(test);
            t0.start();
        } catch(IOException e) {
            System.exit(-1);
        }
    }

    @Override
    public void run() {

        synchronized(this) {
            try {
                numClients++;
                this.connectServer();
            } catch (Exception e) {
                System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
                e.printStackTrace();
                System.exit(-1);
            }

            long startTime = System.currentTimeMillis();
            for (int i = 1; i < 1000; i++ ) {
                Vector<String> start = new Vector<String>();
                start.add("Start");

                Vector<String> addFlight = new Vector<String>();
                addFlight.add("AddFlight");
                addFlight.add(String.valueOf(transactionCount));
                addFlight.add(String.valueOf(transactionCount));
                addFlight.add(String.valueOf(transactionCount));
                addFlight.add(String.valueOf(transactionCount));

                Vector<String> addCars = new Vector<String>();
                addCars.add("AddCars");
                addCars.add(String.valueOf(transactionCount));
                addCars.add(String.valueOf(transactionCount));
                addCars.add(String.valueOf(transactionCount));
                addCars.add(String.valueOf(transactionCount));

                Vector<String> addRooms = new Vector<String>();
                addRooms.add("AddRooms");
                addRooms.add(String.valueOf(transactionCount));
                addRooms.add(String.valueOf(transactionCount));
                addRooms.add(String.valueOf(transactionCount));
                addRooms.add(String.valueOf(transactionCount));

                Vector<String> addCustomerId = new Vector<String>();
                addCustomerId.add("AddCustomerID");
                addCustomerId.add(String.valueOf(transactionCount));
                addCustomerId.add(String.valueOf(transactionCount));

                Vector<String> reserveFlight = new Vector<String>();
                reserveFlight.add("ReserveFlight");
                reserveFlight.add(String.valueOf(transactionCount));
                reserveFlight.add(String.valueOf(transactionCount));
                reserveFlight.add(String.valueOf(transactionCount));

                Vector<String> reserveCar = new Vector<String>();
                reserveCar.add("ReserveCar");
                reserveCar.add(String.valueOf(transactionCount));
                reserveCar.add(String.valueOf(transactionCount));
                reserveCar.add(String.valueOf(transactionCount));

                Vector<String> reserveRoom = new Vector<String>();
                reserveRoom.add("ReserveRoom");
                reserveRoom.add(String.valueOf(transactionCount));
                reserveRoom.add(String.valueOf(transactionCount));
                reserveRoom.add(String.valueOf(transactionCount));

                Vector<String> commit = new Vector<String>();
                commit.add("Commit");
                commit.add(String.valueOf(transactionCount));

                try {
                    execute(Command.Start, start);
                    writeResponseTime(Command.AddFlight, addFlight);
                    writeResponseTime(Command.AddCars, addCars);
                    writeResponseTime(Command.AddRooms, addRooms);
                    writeResponseTime(Command.AddCustomerID, addCustomerId);
                    writeResponseTime(Command.ReserveFlight, reserveFlight);
                    writeResponseTime(Command.ReserveCar, reserveCar);
                    writeResponseTime(Command.ReserveRoom, reserveRoom);
                    execute(Command.Commit, commit);
                    transactionCount++;
                } catch(Exception e) {
                    transactionCount++;
                    continue;
                }
            }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        }
    }

    @Override
    public void connectServer() throws UnknownHostException, IOException {
        connectServer(serverHost, serverPort);
    }

    public void writeResponseTime(Command c, Vector<String> args) throws IOException {
        long startT = System.currentTimeMillis();

        try {
            execute(c, args);
        } catch(Exception e) {
            e.printStackTrace();
        }
        long endT = System.currentTimeMillis();
        long responseT = endT - startT;

        synchronized(this.writer) {
            writer.write(c.toString() + ", " + numClients + ", " + responseT);
            writer.write("\n");
        }

        delayHalfSec();
    }

    public void delayHalfSec() {
        try {
            Thread.sleep(333);
        } catch(InterruptedException e) {
            return;
        }
    }
}