package Server.Middleware;

import Server.Interface.*;
import Server.Common.*;
import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MiddlewareManager extends Middleware {

    private static String s_serverName = "Middleware";

    private static String s_rmiPrefix = "group30";

    //private static String[] serverNames = new String[]{"lab2-15.cs.mcgill.ca","lab2-17.cs.mcgill.ca","lab2-19.cs.mcgill.ca","lab2-21.cs.mcgill.ca"};

    private static String[] name = new String[]{"Flights","Cars","Rooms","Customers"};

    private static String s_serverHost = "localhost";
    private static int s_serverPort = 1099;


    public static void main(String args[])
    {
        /*if (args.length > 0)
        {
            s_serverName = args[0];
        }*/

        // Create the RMI server entry
        try {
            // Create a new Server object
            MiddlewareManager middleware = new MiddlewareManager(s_serverName);

            // Dynamically generate the stub (client proxy)
            IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(middleware, 0);

            // Bind the remote object's stub in the registry
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(1099);
            }
            final Registry registry = l_registry;
            registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        registry.unbind(s_rmiPrefix + s_serverName);
                        System.out.println("'" + s_serverName + "' Middleware resource manager unbound");
                    }
                    catch(Exception e) {
                        System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("'" + s_serverName + "' Middleware resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }


        // Need to reference the RMIRegister to reference the backend objects
        try{
            boolean first = true;
            while(true){
                try{
                    Registry registryOne = LocateRegistry.getRegistry(args[0],s_serverPort);
                    flight_resourceManager = (IResourceManager)registryOne.lookup(s_rmiPrefix + name[0]);
                    Registry registryTwo = LocateRegistry.getRegistry(args[1],s_serverPort);
                    car_resourceManager = (IResourceManager)registryTwo.lookup(s_rmiPrefix + name[1]);
                    Registry registryThree = LocateRegistry.getRegistry(args[2],s_serverPort);
                    hotel_resourceManager = (IResourceManager)registryThree.lookup(s_rmiPrefix + name[2]);
                    Registry registryFour = LocateRegistry.getRegistry(args[3], s_serverPort);
                    customer_resourceManager = (IResourceManager)registryFour.lookup(s_rmiPrefix + name[3]);
                    System.out.println("Connected");
                    break;
                }
                catch (NotBoundException|RemoteException e) {
                    if (first) {
                        e.printStackTrace();
                        System.out.println("Waiting for server");
                        first = false;
                    }
                }
                Thread.sleep(500);
            }
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }



    }

    public MiddlewareManager(String name){
        super(name);
    }



}