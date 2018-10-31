package Server.Middleware;

import Server.Sockets.RequestHandler;
import Server.Interface.IResourceManager;

public class MiddlewareRequestHandler extends RequestHandler {

    public MiddlewareRequestHandler(IResourceManager resourceManager) {
        super(resourceManager);
    }

    public String handle(String req) {
        String response = req;

        return response;
    }
}