package Middleware;

import Server.Common.ResourceManager;

public class Middleware implements IMiddleware {

    final MiddlewareResourceManager resourceManager;

    public Middleware(int port, MiddlewareResourceManager manager) {
        resourceManager = manager;
    }

    // IMiddleware implements Runnable, must implement that here
    public void run() {

    }

    // TODO
    private synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}