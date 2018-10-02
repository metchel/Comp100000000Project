package Server.Middleware;

import Server.Interface.*;
import Server.Common.*;
import java.rmi.NotBoundException;
import java.util.*;
import java.net.*;
import java.io.*;


public class TCPMiddlewareManager extends MiddlewareAlt {

    private static String s_serverName = "Middleware";

    //private static String s_rmiPrefix = "group30";

    private static String[] serverNames = new String[4];

    private static String[] rmNames = new String[]{"Flights","Cars","Rooms","Customers"};

    private static String s_serverHost = "localhost";
    private static int serverPort = 1235;
    Socket carRM;
    Socket flightRM;
    Socket hotelRM;
    Socket customerRM;
    BufferedReader carIn;
    BufferedReader flightIn;
    BufferedReader hotelIn;
    BufferedReader cusstomerIn;
    PrintWriter carOut;
    PrintWriter flightOut;
    PrintWriter hotelOut;
    PrintWriter customerOut;



    public static void main(String args[])
    {

        for (int i = 0; i < 4; i++){
            rmNames[i] = args[i];
        }

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            TCPMiddlewareManager mwm = new TCPMiddlewareManager("Middleware");
            mwm.connectToRMs();
            //mwm.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }



        try {
            ServerSocket server = new ServerSocket(serverPort);
            while (true) {
                Socket client = server.accept();
                System.out.println("Client " + client.getInetAddress() + "connected.");
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            }
        }catch(IOException e){
            e.printStackTrace();
        }


    }



    
    public void connectToRMs(){
        try {
            carRM = new Socket(rmNames[0], serverPort);
            flightRM = new Socket(rmNames[1], serverPort);
            hotelRM = new Socket(rmNames[2], serverPort);
            customerRM = new Socket(rmNames[3], serverPort);
            System.out.println("Connected to " + carRM.getInetAddress()+","+flightRM.getInetAddress()+","+hotelRM.getInetAddress()+","+customerRM.getInetAddress());
        }catch(IOException e){
            e.printStackTrace();
            return;
        }

        carIn = new BufferedReader(new InputStreamReader(carRM.getInputStream()));
        flightIn = new BufferedReader(new InputStreamReader(flightRM.getInputStream()));
        hotelIn = new BufferedReader(new InputStreamReader(hotelRM.getInputStream()));
        customerIn = new BufferedReader(new InputStreamReader(customerRM.getInputStream()));

        carOut = new PrintWriter(carRM.getOutputStream(),true);
        flightOut = new PrintWriter(flightRM.getOutputStream(),true);
        hotelOut = new PrintWriter(hotelRM.getOutputStream(),true);
        customerOut = new PrintWriter(customerRM.getOutputStream(),true);
    }

    public TCPMiddlewareManager(String name){
        super(name);
    }



}