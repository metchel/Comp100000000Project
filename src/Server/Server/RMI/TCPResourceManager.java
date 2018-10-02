package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPResourceManager extends ResourceManager {
    private static String serverName = "Server";
    private static int serverPort = 1099

    //private static String s_rmiPrefix = "group30";

    public static void main(String args[]) {
        ServerSocket server = new ServerSocket(serverPort);
        try {
            while (true) {
                new ActiveConnection(server.accept()).start();
            }
        } finally {
            server.close();
        }
    }

    public TCPResourceManager(String name) {
        super(name);
    }

    private static class ActiveConnection extends Thread {
        Socket clientSocket;

        public ActiveConnection(Socket socket) {
            this.clientSocket = socket;
            System.out.println("Active Connection constructor!");
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                while(true){
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
                    System.out.println("Server recieved: "+input);
                }
            }catch (IOException e) {
                System.out.println("Error in TCPResourceManager run()")
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket");
                }
                System.out.println("Connection with client closed");
            }

        }
    }
}