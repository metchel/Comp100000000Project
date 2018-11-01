package Server.Middleware;

import Server.Common.Trace;
import Server.Common.ResourceManager;
import Server.Common.Constants;
import Server.Sockets.ClientWorker;
import Server.ResourceManager.SocketResourceManager;
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
    private ResourceManagerServer customerServer;
    private Socket customerClient;
    private ArrayList<ResourceManagerServer> itemServers;

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

        final Socket customerClient;
        final Socket flightClient;
        final Socket carClient;
        final Socket roomClient;

        final ServerSocket serverSocket;
        
        try {
            ResourceManagerServer customerServer = new ResourceManagerServer(InetAddress.getByName(inetCustomer), portCustomer, Constants.CUSTOMER);
            ResourceManagerServer flightServer = new ResourceManagerServer(InetAddress.getByName(inetFlights), portFlights, Constants.FLIGHT);
            ResourceManagerServer carServer = new ResourceManagerServer(InetAddress.getByName(inetCars), portCars, Constants.CAR);
            ResourceManagerServer roomServer = new ResourceManagerServer(InetAddress.getByName(inetRooms), portRooms, Constants.ROOM);

            customerClient = customerServer.connect();
            flightClient = flightServer.connect();
            carClient = carServer.connect();
            roomClient = roomServer.connect();

            BufferedReader flightIn = new BufferedReader(new InputStreamReader(flightClient.getInputStream()));
            PrintWriter flightOut = new PrintWriter(flightClient.getOutputStream(), true);

            Builder builder = new Builder();
            Middleware middleware = builder
                .atInetAddress(InetAddress.getByName(inetMiddleware))
                .atPort(portMiddleware)
                .withCustomerServer(customerServer)
                .withItemServer(flightServer)
                .withItemServer(carServer)
                .withItemServer(roomServer)
                .build();

            serverSocket = new ServerSocket(middleware.getPort());

            while(true) {
                ClientWorker worker;
                try {
                    Socket client = serverSocket.accept();
                    RequestHandler handler = new MiddlewareRequestHandler(customerClient, flightClient, carClient, roomClient);
                    worker = new ClientWorker(client, handler);
                    PrintWriter writer = new PrintWriter(customerClient.getOutputStream());
                    Thread t = new Thread(worker);
                    t.start();
                } catch(IOException e) {
                    System.out.println("Middlware::main failed accepting clients");
                    System.exit(-1);
                }
            }
        } catch(IOException e) {
            System.out.println("Middleware::main failed somewhere");
            System.exit(-1);
        }
    }

    private static class Builder {
        private InetAddress inetAddress;
        private int port;
        private ResourceManagerServer customerServer;
        private ArrayList<ResourceManagerServer> itemServers = new ArrayList<>();

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
}