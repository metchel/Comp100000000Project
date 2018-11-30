package Server.Sockets;

import Server.Common.Trace;
import Server.Common.ResourceManager;
import Server.ResourceManager.TransactionResourceManager;
import Server.ResourceManager.ItemResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;

public class Server {

    private InetAddress inetAddress;
    private int port;
    private TransactionResourceManager resourceManager;
    private CoordinatorStub coordinatorStub;

    private Server() {}
    public static void main (String[] args) {
        
        if (args.length != 5) {
            System.out.println("Server::main not enough arguments.");
            System.exit(-1);
        }

        String inetAddress = args[0];
        int port = Integer.parseInt(args[1]);
        String rmName = args[2];
        String coordinatorAddress = args[3];
        int coordinatorPort = Integer.parseInt(args[4]);

        Server server = null;
        ServerSocket serverSocket = null;

        try {
            CoordinatorStub coordinator = new CoordinatorStub(InetAddress.getByName(coordinatorAddress), coordinatorPort);
            server = new Server.Builder()
                .atInetAddress(InetAddress.getByName(inetAddress))
                .atPort(port)
                .withResourceManager(new ItemResourceManager(rmName, coordinator))
                .withCoordinator(coordinator)
                .build();
            serverSocket = new ServerSocket(server.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestHandler handler = new ServerRequestHandler((ItemResourceManager)server.getResourceManager(), server.getCoordinator());

        while(true) {
            try {
                Socket client = serverSocket.accept();
                ClientWorker worker = new ClientWorker(client, handler);
                Thread t = new Thread(worker);
                t.start();
            } catch(IOException e) {
                System.out.println("Server::main failed accepting on port");
                System.exit(-1);
            }
        }
    }

    private static class Builder {
        private InetAddress inetAddress;
        private int port;
        private TransactionResourceManager resourceManager;
        private CoordinatorStub coordinator;

        public Builder atInetAddress(InetAddress inetAddress) throws UnknownHostException {
            this.inetAddress = inetAddress;
            return this;
        }

        public Builder atPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withResourceManager(TransactionResourceManager resourceManager) {
            this.resourceManager = resourceManager;
            return this;
        }

        public Builder withCoordinator(CoordinatorStub coord) {
            this.coordinator = coord;
            return this;
        }

        public Server build() throws IOException {
            Server server = new Server();
            server.inetAddress = this.inetAddress;
            server.port = this.port;
            server.resourceManager = this.resourceManager;
            server.coordinatorStub = this.coordinator;
            return server;
        }
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getInetAddres() {
        return this.inetAddress;
    }

    public TransactionResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public CoordinatorStub getCoordinator() {
        return this.coordinatorStub;
    }
}