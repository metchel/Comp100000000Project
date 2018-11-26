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
    private ArrayList<MiddlewareResourceManager> rms;

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
            final MiddlewareResourceManager flightRM = new MiddlewareResourceManager(Constants.FLIGHT, InetAddress.getByName(inetFlights), portFlights);
            final MiddlewareResourceManager carRM = new MiddlewareResourceManager(Constants.CAR, InetAddress.getByName(inetCars), portCars);
            final MiddlewareResourceManager roomRM = new MiddlewareResourceManager(Constants.ROOM, InetAddress.getByName(inetRooms), portRooms);
            final MiddlewareCoordinator coordinator = new MiddlewareCoordinator(flightRM, carRM, roomRM);
            middleware = new Middleware.Builder()
                .atInetAddress(InetAddress.getByName(inetMiddleware))
                .atPort(portMiddleware)
                .withResourceManager(customerResourceManager)
                .withCoordinator(coordinator)
                .withClient(flightRM)
                .withClient(carRM)
                .withClient(roomRM)
                .build();

            serverSocket = new ServerSocket(middleware.getPort());
            Socket client = new Socket();
            RequestHandler handler = new MiddlewareRequestHandler(client, customerResourceManager, coordinator, flightRM, carRM, roomRM);
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
        private ArrayList<MiddlewareResourceManager> clients = new ArrayList<MiddlewareResourceManager    >();

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

        public Builder withClient(MiddlewareResourceManager client) {
            this.clients.add(client);
            return this;
        }

        public Middleware build() throws IOException {
            Middleware middleware = new Middleware();
            middleware.inetAddress = this.inetAddress;
            middleware.customerResourceManager = this.customerResourceManager;
            middleware.coordinator = this.coordinator;
            middleware.port = this.port;
            middleware.rms = this.clients;

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