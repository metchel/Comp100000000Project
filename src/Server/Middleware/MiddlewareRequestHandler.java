package Server.Middleware;

import Server.Network.Request;
import Server.Network.Response;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

import Server.Sockets.RequestHandler;

public class MiddlewareRequestHandler implements RequestHandler {
    final Socket flightClient;
    final Socket carClient;
    final Socket roomClient;
    final ObjectOutputStream flightOos;
    final ObjectInputStream flightOis;
    final ObjectOutputStream carOos;
    final ObjectInputStream carOis;
    final ObjectOutputStream roomOos;
    final ObjectInputStream roomOis;

    public MiddlewareRequestHandler(Socket flightClient, Socket carClient, Socket roomClient) throws IOException {
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;

        this.flightOos = new ObjectOutputStream(flightClient.getOutputStream());
        this.flightOis = new ObjectInputStream(flightClient.getInputStream());
        this.carOos = new ObjectOutputStream(carClient.getOutputStream());
        this.carOis = new ObjectInputStream(carClient.getInputStream());
        this.roomOos = new ObjectOutputStream(roomClient.getOutputStream());
        this.roomOis = new ObjectInputStream(roomClient.getInputStream());

    }

    public Response handle(Request req) throws IOException, ClassNotFoundException {
        System.out.println("REQUEST: " + req.toString());
        this.flightOos.writeObject(req);
        Response response = (Response) this.flightOis.readObject();
        System.out.println("RESPONSE: " + response.toString());
        return response;
    }
}