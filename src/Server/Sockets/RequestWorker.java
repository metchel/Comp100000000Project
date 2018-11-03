package Server.Sockets;

import Server.Common.Trace;
import Server.Network.Request;
import Server.Network.Response;
import Server.Sockets.RequestHandler;

public class RequestWorker implements Runnable {
    private volatile Response response;
    private Request request;
    private RequestHandler handler;

    boolean active;

    public void run() {
        Trace.info("RequestWorker is working...");
        while(active) {
            try {
                Thread.sleep(5000);
                this.response = this.handler.handle(this.request);
                active = false;
            } catch(Exception e) {
                e.printStackTrace();
                this.response = null;
                active = false;
            }
        }
    }

    public RequestWorker(Request request, RequestHandler handler) {
        active = true;
        this.request = request;
        this.handler = handler;
    }

    public synchronized Response getResponse() {
        return this.response;
    }
}