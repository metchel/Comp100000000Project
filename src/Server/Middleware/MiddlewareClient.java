package Server.Middleware;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public class MiddlewareClient {
    private InetAddress inetAddress;
    private int port;
    private String name;
    final Socket socket;
    final ObjectOutputStream oos;
    final ObjectInputStream ois;

    public MiddlewareClient(InetAddress inetAddress, int port) throws IOException, ClassNotFoundException{
        this.inetAddress = inetAddress;
        this.port = port;
        this.socket = new Socket(inetAddress, port);
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());
    }

    public void send(Object o) throws IOException {
        this.oos.writeObject(o);
    }

    public Object receive() throws IOException, ClassNotFoundException {
        return this.ois.readObject();
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