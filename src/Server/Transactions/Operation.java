package Server.Transactions;

import Server.Common.Command;
import Server.Common.RMItem;

public class Operation {
    private final OperationType type;
    private final String key;
    private final RMItem value;

    public Operation(OperationType type, String key, RMItem value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public OperationType getOperationType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }

    public RMItem getValue() {
        return this.value;
    }

    public enum OperationType {
        AddFlight,
        AddCars,
        AddRooms,
        AddCustomer,
        AddCustomerID,
        DeleteFlight,
        DeleteCars,
        DeleteRooms,
        DeleteCustomer,
        QueryFlight,
        QueryCars,
        QueryRooms,
        QueryCustomer,
        QueryFlightPrice,
        QueryCarPrice,
        QueryRoomPrice,
        ReserveFlight,
        ReserveCar,
        ReserveRoom;
    }
}