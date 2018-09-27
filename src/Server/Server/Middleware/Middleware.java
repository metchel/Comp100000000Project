package Middleware;

import Server.Interface.*;

public class Middleware implements IResourceManager {

    final MiddlewareResourceManager resourceManager;

    public Middleware(int port, MiddlewareResourceManager manager) {
        resourceManager = manager;
    }

    // IMiddleware implements Runnable, must implement that here
    public void run() {

    }

    // TODO
    public synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}