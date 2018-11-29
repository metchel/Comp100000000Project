
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
import java.util.HashMap;
import java.net.InetAddress;

public class MiddlewareResourceManager {
    private final InetAddress inetAddress;
    private final int port;
    private final String name;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Map<Integer, Boolean> crashMap;

    private boolean FAILURE_DETECTED = false;

    public MiddlewareResourceManager(String name, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException{
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
        this.socket = new Socket(inetAddress, port);
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());
        this.crashMap = new HashMap<Integer, Boolean>();
    }

    public void retryConnection(long wait) {
        while (FAILURE_DETECTED) {
            try {
                synchronized(this) {
                    Thread.sleep(wait);
                    Trace.info("Trying to reset connection.");
                    this.socket = new Socket(inetAddress, port);
                    this.oos = new ObjectOutputStream(this.socket.getOutputStream());
                    this.ois = new ObjectInputStream(this.socket.getInputStream());
                    Trace.info("Connection Reset");
                    FAILURE_DETECTED = false;
                }
            } catch (Exception e) {
                continue;
            }
        }
    } 

    public synchronized void send(Request request) throws IOException {
        try {
            this.oos.writeObject(request);
        } catch(Exception e) {
            Trace.warn("Broken Socket");
        }
    }

    public void forceFailureDetection() {
        FAILURE_DETECTED = true;
        Thread t = new Thread(() -> {
            retryConnection(1000);
        });
        t.start();
    }

    public synchronized Response receive() throws IOException, ClassNotFoundException {
        try {
            long start = System.currentTimeMillis();
            long time = 5000;
            while (System.currentTimeMillis() < start + time) {
                return (Response)this.ois.readObject();
            }

            Trace.warn("Timeout waiting for response from RM.");
            Response res = new Response();
            res.addCurrentTimeStamp().addStatus(false).addMessage("Failure");
            return res;
        } catch(Exception e) {
            FAILURE_DETECTED = true;
            Thread t = new Thread(() -> {
                retryConnection(1000);
            });
            t.start();
            Trace.warn("Detected Failure");
            Response res = new Response();
            res.addCurrentTimeStamp()
                .addStatus(false)
                .addMessage("Failure");
            return res;
        }
    }

    public void setCrash(int mode) {
        this.crashMap.put(mode, true);
    }

    public void resetCrashes() {
        for (Integer mode: crashMap.keySet()) {
            this.crashMap.put(mode, false);
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

    public void shutdown() throws IOException {
        this.send(generateRequest(-1, Command.Shutdown, null));
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