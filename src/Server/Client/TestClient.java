package Server.Client;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

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

    boolean TEST_RESPONSE_TIME = true;
    boolean TEST_THROUGHPUT = false;

    private static int transactionCount = 1;
    private static String serverHost;
    private static int serverPort;
    private static int numClients = 0;
    final FileWriter writer;
    final static File file = new File("result.csv");

    public TestClient(String host, int port) throws IOException {
        super();
        serverHost = host;
        serverPort = port;
        this.writer = new FileWriter(file.getAbsoluteFile(), true);
    }

    public static void main(String[] a) {
        try {
            Thread[] threads = new Thread[1];
            for (int i = 0; i < threads.length; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                TestClient test = new TestClient(a[0], Integer.parseInt(a[1]));
                threads[i] = new Thread(test);
                threads[i].start();
            }

            for (int i = 0; i < threads.length; i++) {

                try {
                    threads[i].join();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
            for (int i = 1; i < 10; i++ ) {
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

                if (TEST_RESPONSE_TIME) {
                    try {
                        execute(Command.Start, start);
                        delay();
                        execute(Command.AddFlight, addFlight);
                        delay();
                        execute(Command.AddCars, addCars);
                        delay();
                        execute(Command.AddRooms, addRooms);
                        delay();
                        execute(Command.AddCustomerID, addCustomerId);
                        delay();
                        execute(Command.ReserveFlight, reserveFlight);
                        delay();
                        execute(Command.ReserveCar, reserveCar);
                        delay();
                        execute(Command.ReserveRoom, reserveRoom);
                        delay();
                        execute(Command.Commit, commit);
                        transactionCount++;
                    } catch(Exception e) {
                        transactionCount++;
                        continue;
                    }
                }

                if (TEST_THROUGHPUT) {

                }
            }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        }
        numClients--;
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
            writer.write(endT + ", " + c.toString() + ", " + numClients + ", " + responseT);
            writer.write("\n");
        }

        delay();
    }

    public void delay() {
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            return;
        }
    }
}