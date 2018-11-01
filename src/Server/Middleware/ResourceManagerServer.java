package Server.Middleware;

import Server.Sockets.RequestHandler;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

public class ResourceManagerServer {
    private InetAddress inetAddress;
    private int port;
    private String name;

    public ResourceManagerServer(InetAddress inetAddress, int port, String name) {
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public int getPort() {
        return this.port;
    }
    
    public String getName() {
        return this.name;
    }

    public Socket connect() throws IOException {
        return new Socket(this.inetAddress, this.port);
    }
}