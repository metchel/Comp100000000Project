package Middleware;

import Server.Interface.*;
import java.util.*;
import java.rmi.RemoteException;
import java.io.*;


public abstract class Middleware implements IResourceManager {

    static IResourceManager flight_resourceManager = null;
    static IResourceManager car_resourceManager = null;
    static IResourceManager hotel_resourceManager = null;
    protected String m_name = "";
    public Middleware(String p_name) {m_name = p_name; }

    // IMiddleware implements Runnable, must implement that here
    public void run() {

    }

    // TODO
    public synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}