package Server.Sockets;

import Server.Common.Trace;
import Server.Network.Request;
import Server.Network.Response;
import Server.Sockets.RequestHandler;

public class RequestWorker implements Runnable {
    private Response response;
    private final Request request;
    private final RequestHandler handler;

    volatile Boolean active;

    public void run() {
        Trace.info("RequestWorker is working...");
        synchronized(this.active) {
            while(this.active.booleanValue()) {
                try {
                    this.response = this.handler.handle(this.request);
                } catch(Exception e) {
                    e.printStackTrace();
                    this.response = null;
                    this.active = new Boolean(false);
                }
                this.active = new Boolean(false);
            }

        }
    }

    public RequestWorker(Request request, RequestHandler handler) {
        active = new Boolean(true);
        this.request = request;
        this.handler = handler;
    }

    public synchronized Response getResponse() {
        return this.response;
    }
}