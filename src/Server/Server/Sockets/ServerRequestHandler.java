package Server.Sockets;

import Server.Interface.IResourceManager;

public class ServerRequestHandler extends RequestHandler {

    public ServerRequestHandler(IResourceManager resourceManager) {
        super(resourceManager);
    }

    public String handle(String req) {
        System.out.println(req);

        String response = "yay";

        return response;
    }
}