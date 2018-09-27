package Middleware;

import Server.Interface.*;

public class Middleware implements IResourceManager {

    IResourceManager flight_resourceManager = null;
    IResourceManager car_resourceManager = null;
    IResourceManager hotel_resourceManager = null;

    public Middleware(int port, MiddlewareResourceManager manager) {
        resourceManager = manager;
    }

    // IMiddleware implements Runnable, must implement that here
    public void run() {

    }

    // TODO
    public synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}