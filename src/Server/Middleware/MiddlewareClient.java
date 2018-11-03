package Server.Middleware;

import Server.Network.Request;
import Server.Network.Response;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public class MiddlewareClient {
    private InetAddress inetAddress;
    private int port;
    final String name;
    final Socket socket;
    final ObjectOutputStream oos;
    final ObjectInputStream ois;

    public MiddlewareClient(String name, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException{
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
        this.socket = new Socket(inetAddress, port);
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());
    }

    public synchronized void send(Request request) throws IOException {
        this.oos.writeObject(request);
    }

    public synchronized Response receive() throws IOException, ClassNotFoundException {
        return (Response)this.ois.readObject();
    }

    public void closeConnection() throws IOException {
        this.socket.close();
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
}