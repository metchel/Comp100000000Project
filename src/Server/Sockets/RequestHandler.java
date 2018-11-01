package Server.Sockets;

import Server.Network.Request;
import Server.Network.Response;

import java.io.IOException;
import java.net.Socket;

public interface RequestHandler {

    public Response handle(Request req) throws IOException, ClassNotFoundException;
}