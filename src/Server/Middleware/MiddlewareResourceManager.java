
package Server.Middleware;

import Server.Network.Request;
import Server.Network.RequestData;
import Server.Network.Response;
import Server.Common.Command;
import Server.Common.Trace;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.net.InetAddress;

public class MiddlewareResourceManager {
    private final InetAddress inetAddress;
    private final int port;
    private final String name;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public MiddlewareResourceManager(String name, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException{
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
        this.socket = new Socket(inetAddress, port);
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());
    }

    public void retryConnection(long interval) {
        boolean success = false;
        while (!success) {
            try {
                this.socket = new Socket(inetAddress, port);
                this.oos = new ObjectOutputStream(this.socket.getOutputStream());
                this.ois = new ObjectInputStream(this.socket.getInputStream());
                success = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(interval);
                } catch(InterruptedException ie) {
                    continue;
                }
                continue;
            }
        }
    } 

    public synchronized void send(Request request) throws IOException {
        try {
            this.oos.writeObject(request);
        } catch(Exception e) {
            Trace.warn("Broken Socket");
            retryConnection(1000);
            send(request);
        }
    }

    public synchronized Response receive() throws IOException, ClassNotFoundException {
        try {
            return (Response)this.ois.readObject();
        } catch(Exception e) {
            Trace.warn("Detected Failure");
            Response res = new Response();
            res.addCurrentTimeStamp().addStatus(false).addMessage("Failure");
            return res;
        }
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

    public boolean start(int transactionId) throws IOException, ClassNotFoundException {
        this.send(generateRequest(transactionId, Command.Start, null));
        Response res = this.receive();
        return res.getStatus().booleanValue();
    }

    public boolean commit (int transactionId) throws IOException, ClassNotFoundException {
        this.send(generateRequest(transactionId, Command.Commit, null));
        Response res = this.receive();
        return res.getStatus().booleanValue();
    }

    public boolean abort(int transactionId) throws IOException, ClassNotFoundException {
        this.send(generateRequest(transactionId, Command.Abort, null));
        Response res = this.receive();
        return res.getStatus().booleanValue();
    }

    public Response forward(Request req) throws IOException, ClassNotFoundException {
        this.send(req);
        return this.receive();
    }

    private Request generateRequest(int transactionId, Command command, Map<String, Object> arguments) {
        final Request req = new Request();
        final RequestData data = new RequestData();
        data.addXId(transactionId)
            .addCommand(command);
        
        if (arguments != null) {
            for (String key: arguments.keySet()) {
                data.addArgument(key, arguments.get(key));
            }
        }

        req.addCurrentTimeStamp()
            .addData(data);

        return req;
    }
}