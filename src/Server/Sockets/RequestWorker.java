package Server.Sockets;

import Server.Common.Trace;
import Server.Network.Request;
import Server.Network.Response;
import Server.Sockets.RequestHandler;

public class RequestWorker implements Runnable {
    private Response response;
    private final Request request;
    private final RequestHandler handler;

    volatile boolean active;

    public void run() {
        Trace.info("RequestWorker is working...");
        while(active) {
            try {
                synchronized(this.request) {
                    this.response = this.handler.handle(this.request);
                    active = false;
                }
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