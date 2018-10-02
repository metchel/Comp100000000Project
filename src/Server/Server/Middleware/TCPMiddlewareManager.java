package Server.Middleware;

import Server.Interface.*;
import Server.Common.*;
import java.rmi.NotBoundException;
import java.util.*;
import java.net.*;

public class TCPMiddlewareManager extends Middleware {

    private static String s_serverName = "Middleware";

    //private static String s_rmiPrefix = "group30";

    //private static String[] serverNames = new String[]{"lab2-15.cs.mcgill.ca","lab2-17.cs.mcgill.ca","lab2-19.cs.mcgill.ca","lab2-21.cs.mcgill.ca"};

    private static String[] rmNames = new String[]{"Flights","Cars","Rooms","Customers"};

    private static String s_serverHost = "localhost";
    private static int serverPort = 1099;


    public static void main(String args[])
    {
        ServerSocket server = new ServerSocket(port);
        while(true){
            Socket client = server.accept();
            System.out.println("Client "+client.getInetAddress()+"connected.");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        }


    }

    public TCPMiddlewareManager(String name){
        super(name);
    }



}