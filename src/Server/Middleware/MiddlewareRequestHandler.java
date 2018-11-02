package Server.Middleware;

import Server.Network.Request;
import Server.Network.Response;

import java.io.IOException;
import java.net.Socket;

import Server.Sockets.RequestHandler;

public class MiddlewareRequestHandler implements RequestHandler {
    final MiddlewareClient flightClient;
    final MiddlewareClient carClient;
    final MiddlewareClient roomClient;

    public MiddlewareRequestHandler(MiddlewareClient flightClient, MiddlewareClient carClient, MiddlewareClient roomClient) throws IOException, ClassNotFoundException {
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;
    }

    public Response handle(Request req) throws IOException, ClassNotFoundException {
        System.out.println("REQUEST: " + req.toString());
        this.flightClient.send(req);
        this.carClient.send(req);
        this.roomClient.send(req);
        Response response = (Response) this.flightClient.receive();
        Response response2 = (Response) this.carClient.receive();
        Response response3 = (Response) this.roomClient.receive();
        System.out.println("RESPONSE: " + response.toString());
        return response;
    }


}