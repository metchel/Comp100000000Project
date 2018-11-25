package Server.Middleware;

import Server.Common.Trace;
import Server.Common.ResourceManager;
import Server.Common.Constants;
import Server.Sockets.ClientWorker;
import Server.ResourceManager.TransactionResourceManager;
import Server.Sockets.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Middleware {
    private InetAddress inetAddress;
    private int port;
    private TransactionResourceManager customerResourceManager;
    private MiddlewareCoordinator coordinator;
    private ArrayList<MiddlewareClient> clients;

    private Middleware() {}
    public static void main(String[] args) {
        if (args.length != 8) {
            System.out.println("Middleware::main not enough arguments.");
            System.exit(-1);
        }

        String inetMiddleware = args[0];
        int portMiddleware = Integer.parseInt(args[1]);

        String inetFlights = args[2];
        int portFlights = Integer.parseInt(args[3]);
        String inetCars = args[4];
        int portCars = Integer.parseInt(args[5]);
        String inetRooms = args[6];
        int portRooms = Integer.parseInt(args[7]);

        final Middleware middleware;
        final ServerSocket serverSocket;
        
        try {
            final TransactionResourceManager customerResourceManager = new TransactionResourceManager(Constants.CUSTOMER);
            final MiddlewareClient flightClient = new MiddlewareClient(Constants.FLIGHT, InetAddress.getByName(inetFlights), portFlights);
            final MiddlewareClient carClient = new MiddlewareClient(Constants.CAR, InetAddress.getByName(inetCars), portCars);
            final MiddlewareClient roomClient = new MiddlewareClient(Constants.ROOM, InetAddress.getByName(inetRooms), portRooms);
            final MiddlewareCoordinator coordinator = new MiddlewareCoordinator(flightClient, carClient, roomClient);
            middleware = new Middleware.Builder()
                .atInetAddress(InetAddress.getByName(inetMiddleware))
                .atPort(portMiddleware)
                .withResourceManager(customerResourceManager)
                .withCoordinator(coordinator)
                .withClient(flightClient)
                .withClient(carClient)
                .withClient(roomClient)
                .build();

            serverSocket = new ServerSocket(middleware.getPort());
            RequestHandler handler = new MiddlewareRequestHandler(customerResourceManager, coordinator, flightClient, carClient, roomClient);
            Socket client = new Socket();
            while(true) {
                try {
                    client = serverSocket.accept();
                    ClientWorker worker = new ClientWorker(client, handler);
                    Thread t = new Thread(worker);
                    t.start();
                } catch(IOException e) {
                    System.out.println("Middlware::main failed somewhere");
                    System.exit(-1);
                }
            }
        } catch(Exception e) {
            System.out.println("Middleware::main failed somewhere");
            System.exit(-1);
        }
    }

    private static class Builder {
        private InetAddress inetAddress;
        private int port;
        private TransactionResourceManager customerResourceManager;
        private MiddlewareCoordinator coordinator;
        private ArrayList<MiddlewareClient> clients = new ArrayList<MiddlewareClient    >();

        public Builder atInetAddress(InetAddress inetAddress) throws UnknownHostException{
            this.inetAddress = inetAddress;
            return this;
        }

        public Builder atPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withResourceManager(TransactionResourceManager customerResourceManager) {
            this.customerResourceManager = customerResourceManager;
            return this;
        }

        public Builder withCoordinator(MiddlewareCoordinator coordinator) {
            this.coordinator = coordinator;
            return this;
        }

        public Builder withClient(MiddlewareClient client) {
            this.clients.add(client);
            return this;
        }

        public Middleware build() throws IOException {
            Middleware middleware = new Middleware();
            middleware.inetAddress = this.inetAddress;
            middleware.customerResourceManager = this.customerResourceManager;
            middleware.coordinator = this.coordinator;
            middleware.port = this.port;
            middleware.clients = this.clients;

            return middleware;
        }
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public TransactionResourceManager getCustomerResourceManager() {
        return this.customerResourceManager;
    }

    public MiddlewareCoordinator getCoordinator() {
        return this.coordinator;
    }
}