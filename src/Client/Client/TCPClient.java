package Client;

import Server.Interface.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.io.*;

public class TCPClient extends Client
{
    private static String s_serverHost = "localhost";
    private static int s_serverPort = 1099;
    private static String s_serverName = "Server";

    private static String s_rmiPrefix = "group30";

    public static void main(String args[])
    {

    }

    public TCPClient()
    {
        super();
    }

}

