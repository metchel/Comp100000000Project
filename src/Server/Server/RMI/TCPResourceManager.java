package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TCPResourceManager extends ResourceManager
{
    private static String s_serverName = "Server";

    //private static String s_rmiPrefix = "group30";

    public static void main(String args[])
    {

    }

    public TCPResourceManager(String name)
    {
        super(name);
    }
}
