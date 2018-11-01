package Server.Middleware;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

import Server.Sockets.RequestHandler;
import Server.Network.*;

public class MiddlewareRequestHandler implements RequestHandler {
    final Socket customerClient;
    final Socket flightClient;
    final Socket carClient;
    final Socket roomClient;
    final BufferedReader customerIn;
    final PrintWriter customerOut;
    final BufferedReader flightIn;
    final PrintWriter flightOut;
    final PrintWriter carOut;
    final BufferedReader carIn;
    final PrintWriter roomOut;
    final BufferedReader roomIn;

    public MiddlewareRequestHandler(Socket customerClient, Socket flightClient, Socket carClient, Socket roomClient) throws IOException {
        this.customerClient = customerClient;
        this.customerIn = new BufferedReader(new InputStreamReader(customerClient.getInputStream()));
        this.customerOut = new PrintWriter(customerClient.getOutputStream(), true);
        this.flightClient = flightClient;
        this.flightIn = new BufferedReader(new InputStreamReader(flightClient.getInputStream()));
        this.flightOut = new PrintWriter(flightClient.getOutputStream(), true);
        this.carClient = carClient;
        this.carIn = new BufferedReader(new InputStreamReader(carClient.getInputStream()));
        this.carOut = new PrintWriter(carClient.getOutputStream(), true);
        this.roomClient = roomClient;
        this.roomIn = new BufferedReader(new InputStreamReader(roomClient.getInputStream()));
        this.roomOut = new PrintWriter(roomClient.getOutputStream(), true);
    }

    public Response handle(Request req) throws IOException, ClassNotFoundException{
        System.out.println("REQUEST: " + req.getMessage());
        Response response = new Response("HI");
        return response;
    }
}