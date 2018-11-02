package Server.Middleware;

import Server.Network.Request;
import Server.Network.RequestData;
import Server.Network.Response;
import Server.ResourceManager.SocketResourceManager;
import Server.Common.Command;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;

import Server.Sockets.RequestHandler;

public class MiddlewareRequestHandler implements RequestHandler {
    final MiddlewareClient flightClient;
    final MiddlewareClient carClient;
    final MiddlewareClient roomClient;

    final SocketResourceManager customerResourceManager;
    final Map<String, Queue<Request>> transactionMap;

    public MiddlewareRequestHandler(MiddlewareClient flightClient, MiddlewareClient carClient, MiddlewareClient roomClient) throws IOException, ClassNotFoundException {
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;
        this.customerResourceManager = new SocketResourceManager("Customers");
        this.transactionMap = new HashMap<String, Queue<Request>>();
        this.transactionMap.put("FLIGHTS", new LinkedList<Request>());
        this.transactionMap.put("CARS", new LinkedList<Request>());
        this.transactionMap.put("ROOMS", new LinkedList<Request>());
        this.transactionMap.put("CUSTOMERS", new LinkedList<Request>());
    }

    public Response handle(Request req) throws IOException, ClassNotFoundException {
        System.out.println("REQUEST: " + req.toString());
        requestMapper(req);

        Response response = null;
        while(!this.transactionMap.get("FLIGHTS").isEmpty()) {
            Request request = this.transactionMap.get("FLIGHTS").remove();
            this.flightClient.send(request);
            response = this.flightClient.receive();
        }

        while(!this.transactionMap.get("CARS").isEmpty()) {
            Request request = this.transactionMap.get("CARS").remove();
            this.carClient.send(request);
            response = this.carClient.receive();
        }

        while(!this.transactionMap.get("ROOMS").isEmpty()) {
            Request request = this.transactionMap.get("ROOMS").remove();
            this.roomClient.send(request);
            response = roomClient.receive();
        }
        System.out.println("RESPONSE: " + response.toString());
        return response;
    }

    public void requestMapper(Request request) {
        RequestData data = (RequestData) request.getData();
        Command command = data.getCommand();

        switch (command)
        {
            case Help:
            {
                break;
            }
            case AddFlight: {
                this.transactionMap.get("FLIGHTS").add(request);
                break;
            }

            case AddCars: {
                this.transactionMap.get("CARS").add(request);
                break;
            }

            case AddRooms: {
                this.transactionMap.get("ROOMS").add(request);
                break;
            }

            case AddCustomer: {
                this.transactionMap.get("CUSTOMERS").add(request);
                break;
            }
            case AddCustomerID: {
                this.transactionMap.get("CUSTOMERS").add(request);
                break;
            }
            case DeleteFlight: {
                this.transactionMap.get("FLIGHTS").add(request);
                break;
            }
            case DeleteCars: {
                this.transactionMap.get("CARS").add(request);
                break;
            }
            case DeleteRooms: {
                this.transactionMap.get("ROOMS").add(request);
                break;
            }
            case DeleteCustomer: {
                this.transactionMap.get("CUSTOMERS").add(request);
                break;
            }
            case QueryFlight: {
                this.transactionMap.get("FLIGHTS").add(request);
                break;
            }
            case QueryCars: {
                this.transactionMap.get("CARS").add(request);
                break;
            }
            case QueryRooms: {
                this.transactionMap.get("CARS").add(request);
                break;
            }
            case QueryCustomer: {
                this.transactionMap.get("CUSTOMERS").add(request);
                break;
            }
            case QueryFlightPrice: {
                this.transactionMap.get("FLIGHTS").add(request);
                break;
            }
            case QueryCarsPrice: {
                this.transactionMap.get("CARS").add(request);
                break;
            }
            case QueryRoomsPrice: {
                this.transactionMap.get("ROOMS").add(request);
                break;
            }
            case ReserveFlight: {
                this.transactionMap.get("FLIGHTS").add(request);
                break;
            }
            case ReserveCar: {
                this.transactionMap.get("CARS").add(request);
                break;
            }
            case ReserveRoom: {
                this.transactionMap.get("ROOMS").add(request);
                break;
            }
            case Bundle: {
                this.transactionMap.get("CUSTOMERS").add(request);
                this.transactionMap.get("FLIGHTS").add(request);
                this.transactionMap.get("CARS").add(request);
                this.transactionMap.get("ROOMS").add(request);
                break;
            }
        }
    }
}