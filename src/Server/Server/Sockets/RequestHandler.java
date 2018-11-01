package Server.Sockets;

import java.io.IOException;
import java.net.Socket;

public interface RequestHandler {

    public String handle(String req) throws IOException;
}