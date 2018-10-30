package Server.Middleware;

import Server.Common.Trace;
import Server.Common.ResourceManager;
import Server.Sockets.ClientWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Middleware {
    private InetAddress inetAddress;
    private int port;
    private ResourceManagerServer customerServer;
    private Socket customerClient;
    private ArrayList<ResourceManagerServer> itemServers;
    private ArrayList<Socket> itemClients;
    private static ServerSocket serverSocket;
    final static String CUSTOMER = "CUSTOMER";
    final static String FLIGHT = "FLIGHT";
    final static String CAR = "CAR";
    final static String ROOM = "ROOM";

    private Middleware() {}
    public static void main(String[] args) {
        if (args.length != 10) {
            System.out.println("Middleware::main not enough arguments.");
            System.exit(-1);
        }

        /**
         * Middleware host and port
         */
        String inetMiddleware = args[0];
        int portMiddleware = Integer.parseInt(args[1]);

        /**
         * Customer server host and port
         */
        String inetCustomer = args[2];
        int portCustomer = Integer.parseInt(args[3]);

        /**
         * Item servers hosts and ports
         */
        String inetFlights = args[4];
        int portFlights = Integer.parseInt(args[5]);
        String inetCars = args[6];
        int portCars = Integer.parseInt(args[7]);
        String inetRooms = args[8];
        int portRooms = Integer.parseInt(args[9]);
        
        try {
            ResourceManagerServer customerServer = new ResourceManagerServer(InetAddress.getByName(inetCustomer), portCustomer, CUSTOMER);
            ResourceManagerServer flightServer = new ResourceManagerServer(InetAddress.getByName(inetFlights), portFlights, FLIGHT);
            ResourceManagerServer carServer = new ResourceManagerServer(InetAddress.getByName(inetCars), portCars, CAR);
            ResourceManagerServer roomServer = new ResourceManagerServer(InetAddress.getByName(inetRooms), portRooms, ROOM);

            Builder builder = new Builder();
            Middleware middleware = builder
                .atInetAddress(InetAddress.getByName(inetMiddleware))
                .atPort(portMiddleware)
                .withCustomerServer(customerServer)
                .withItemServer(flightServer)
                .withItemServer(carServer)
                .withItemServer(roomServer)
                .build();
            middleware.connectToCustomerServer();
            middleware.connectToItemServers();
            serverSocket = new ServerSocket(middleware.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            ClientWorker worker;

            try {
                Socket client = serverSocket.accept();
                worker = new ClientWorker(client);
                Thread t = new Thread(worker);
                t.start();
            } catch(IOException e) {
                System.out.println("Middlware::main failed accepting clients");
                System.exit(-1);
            }
        }
    }

    public static class Builder {
        private InetAddress inetAddress;
        private int port;
        private ResourceManagerServer customerServer;
        private ArrayList<ResourceManagerServer> itemServers;

        public Builder atInetAddress(InetAddress inetAddress) throws UnknownHostException{
            this.inetAddress = inetAddress;
            return this;
        }

        public Builder atPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withCustomerServer(ResourceManagerServer customerServer) {
            this.customerServer = customerServer;
            return this;
        }

        public Builder withItemServer(ResourceManagerServer itemServer) {
            this.itemServers.add(itemServer);
            return this;
        }

        public Middleware build() throws IOException {
            Middleware middleware = new Middleware();
            middleware.inetAddress = this.inetAddress;
            middleware.port = this.port;
            middleware.customerServer = this.customerServer;
            middleware.itemServers = this.itemServers;

            return middleware;
        }
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getInetAddres() {
        return this.inetAddress;
    }

    public void connectToCustomerServer() throws IOException {
        this.customerClient = new Socket(this.customerServer.getInetAddress(), this.customerServer.getPort());
    }

    public void connectToItemServers() throws IOException {
        for (ResourceManagerServer itemServer: this.itemServers) {
            this.itemClients.add(new Socket(itemServer.getInetAddress(), itemServer.getPort()));
        }
    }
}