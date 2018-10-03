package Client;

import Server.Interface.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import java.util.*;
import java.io.*;

public class TCPClient extends ClientAlt
{
    private static String serverHost = "localhost";
    private static int serverPort = 1236;
    //private static String s_serverName = "Server";

    //private static String s_rmiPrefix = "group30";

    public static void main(String args[])
    {

        if (args.length > 0)
        {
            serverHost = args[0];
        }
        if (args.length > 1)
        {
            serverPort = toInt(args[1]);
        }
        if (args.length > 2)
        {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
            System.exit(1);
        }
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.println(serverHost+":"+(Integer.toString(serverPort)));
        try {
            TCPClient client = new TCPClient();
            client.connectServer();
            client.start();
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

    }


    public void connectServer() throws UnknownHostException, IOException
    {
        connectServer(serverHost, serverPort);
    }

    public void connectServer(String serverHost, int serverPort) throws UnknownHostException, IOException
    {
        try {
            System.out.println(serverHost+" "+serverPort);
            server = new Socket(serverHost, serverPort);
            System.out.println("Connected to " + server.getInetAddress());
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out = new PrintWriter(server.getOutputStream(),true);
    }

    public TCPClient()
    {
        super();
    }

}

