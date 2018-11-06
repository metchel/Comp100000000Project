package Server.Middleware;

import Server.Network.Request;
import Server.Network.RequestData;
import Server.Network.Response;
import Server.ResourceManager.SocketResourceManager;
import Server.Common.Command;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;

import Server.Sockets.RequestHandler;

public class MiddlewareRequestHandler implements RequestHandler {
    private final MiddlewareClient flightClient;
    private final MiddlewareClient carClient;
    private final MiddlewareClient roomClient;

    private final SocketResourceManager customerResourceManager;
    private final MiddlewareCoordinator coordinator;

    public MiddlewareRequestHandler(SocketResourceManager customerResourceManager, MiddlewareCoordinator coordinator, MiddlewareClient flightClient, MiddlewareClient carClient, MiddlewareClient roomClient) throws IOException, ClassNotFoundException {
        this.customerResourceManager = customerResourceManager;
        this.coordinator = coordinator;
        this.flightClient = flightClient;
        this.carClient = carClient;
        this.roomClient = roomClient;
    }

    public Response handle(Request request) throws IOException, ClassNotFoundException {
        final RequestData data = (RequestData) request.getData();
        final Command command = data.getCommand();
        Response response = new Response();

        switch (command) {
            case Help: {
                break;
            }
            /**
             * Read only operations
             */
            case QueryFlight: {
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCars: {
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRooms: {
                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case QueryCustomer: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                Boolean resStatus = new Boolean(false);
                String message = "Customer does not exist.";
                if (info != null && info != "") {
                    resStatus = new Boolean(true);
                    message = info;
                }
                response.addCurrentTimeStamp()
                    .addStatus(resStatus)
                    .addMessage(message);
                break;
            }
            case QueryFlightPrice: {
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case QueryCarsPrice: {
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case QueryRoomsPrice: {
                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }

            /**
             * Write only operations
             */
            case AddFlight: {
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }

            case AddCars: {
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }

            case AddRooms: {
                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }


            case AddCustomer: {
                Integer xId = data.getXId();
                int newId = this.customerResourceManager.newCustomer(xId.intValue());
                Integer newIdInteger = new Integer(newId);

                Request clone = new Request();
                RequestData informClients = new RequestData();
                informClients.addXId(xId)
                    .addCommand(Command.AddCustomerID)
                    .addArgument("cId", newId);
                clone.addCurrentTimeStamp()
                    .addData(informClients);
                this.flightClient.send(clone);
                Response flightResponse = this.flightClient.receive();
                this.carClient.send(clone);
                Response carResponse = this.carClient.receive();
                this.roomClient.send(clone);
                Response roomResponse = this.roomClient.receive();
                boolean informClientSuccess = flightResponse.getStatus().booleanValue() 
                    && carResponse.getStatus().booleanValue()
                    && roomResponse.getStatus().booleanValue();
                response.addCurrentTimeStamp()
                    .addStatus(new Boolean(true))
                    .addMessage(newIdInteger.toString());
                break;
            }
            case AddCustomerID: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                boolean resStatus = this.customerResourceManager.newCustomer(xId, cId);
                Boolean resStatusBoolean = new Boolean(resStatus);

                Request clone = new Request();
                clone.addCurrentTimeStamp()
                    .addData(data);
                this.flightClient.send(clone);
                Response flightResponse = this.flightClient.receive();
                this.carClient.send(clone);
                Response carResponse = this.carClient.receive();
                this.roomClient.send(clone);
                Response roomResponse = this.roomClient.receive();
                boolean informClientSuccess = resStatus 
                    && flightResponse.getStatus().booleanValue() 
                    && carResponse.getStatus().booleanValue()
                    && roomResponse.getStatus().booleanValue();
                response.addCurrentTimeStamp()
                    .addStatus(resStatusBoolean)
                    .addMessage(resStatusBoolean.toString());
                break;
            }
            case DeleteFlight: {
                this.flightClient.send(request);
                response = this.flightClient.receive();
                break;
            }
            case DeleteCars: {
                this.carClient.send(request);
                response = this.carClient.receive();
                break;
            }
            case DeleteRooms: {
                this.roomClient.send(request);
                response = this.roomClient.receive();
                break;
            }
            case DeleteCustomer: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                boolean resStatus = this.customerResourceManager.deleteCustomer(xId, cId);
                Boolean resStatusBoolean = new Boolean(resStatus);
                response.addCurrentTimeStamp()
                    .addStatus(resStatusBoolean)
                    .addMessage(resStatusBoolean.toString());
                break;
            }

            /**
             * Read and Write operations
             */
            case ReserveFlight: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.flightClient.send(request);
                    response = this.flightClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }

            case ReserveCar: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.carClient.send(request);
                    response = this.carClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case ReserveRoom: {
                Integer xId = data.getXId();
                Integer cId = (Integer)data.getCommandArgs().get("cId");
                String info = this.customerResourceManager.queryCustomerInfo(xId, cId);
                if (info != null && info != "") {
                    this.roomClient.send(request);
                    response = this.roomClient.receive();
                } else {
                    response.addCurrentTimeStamp()
                        .addStatus(new Boolean(false))
                        .addMessage("Customer does not exist.");
                }
                break;
            }
            case Bundle: {
                // to do
                break;
            }
        }

        return response;
    }
}