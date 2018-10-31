package Server.Sockets;

import Server.Interface.IResourceManager;

import java.io.BufferedReader;
import java.net.Socket;

public abstract class RequestHandler {

    private IResourceManager resourceManager;

    public RequestHandler(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public abstract String handle(String req);
}