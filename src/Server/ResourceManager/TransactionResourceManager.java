package Server.ResourceManager;

import Server.Common.*;
import Server.LockManager.LockManager;
import Server.LockManager.DeadlockException;
import Server.LockManager.TransactionLockObject.LockType;
import Server.Middleware.TransactionManager;
import Server.Transactions.Operation;
import Server.Transactions.Operation.OperationType;

import java.io.IOException;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

public class TransactionResourceManager extends SocketResourceManager {
    private final LockManager lockManager;
    private Map<Integer, Stack<Operation>> txMap;
    
    public TransactionResourceManager(String name) {
        super(name);
        this.lockManager = new LockManager();
        this.txMap = new HashMap<Integer, Stack<Operation>>();
    }

    public boolean start(int xId) {
        try {
            Stack txOps = this.txMap.get(xId);
            if (txOps != null) {
                return false;
            } 
            this.txMap.put(xId, new Stack<Operation>());
            return true;
        } catch (Exception e) {
            Trace.info("Could not enlist transaction " + xId);
            return false;
        }
    }

    public boolean commit(int xId) {
        try {
            Trace.info("Committing transaction " + xId);
            Stack txOps = this.txMap.get(xId);
            if (txOps == null) {
                Trace.info("No operations for transaction " + xId);
                return false;
            }

            this.txMap.remove(xId);
            return lockManager.UnlockAll(xId);
        } catch(Exception e) {
            Trace.info("Could not commit transaction " + xId);
            return false;
        }
    }

    public boolean abort(int xId){
        try {
            System.out.println("Aborting transaction " + xId);
            Stack txOps = txMap.get(xId);

            if(txOps == null) {
                return false;
            }

            while(!txOps.isEmpty()) {
                Operation op = (Operation)txOps.pop();
                undo(xId, op);
            }

            txMap.remove(xId);
            lockManager.UnlockAll(xId);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean shutdown() {
        return false;
    }

    public void undo(int xId, Operation operation) {
        OperationType type = operation.getOperationType();

        switch(type) {

            case AddFlight: {
                RMItem value = operation.getValue();
                if (value == null) {
                    removeData(xId, operation.getKey());
                } else {
                    writeData(xId, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddCars: {
                RMItem value = operation.getValue();
                if (value == null) {
                    removeData(xId, operation.getKey());
                } else {
                    writeData(xId, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddRooms: {
                RMItem value = operation.getValue();
                if (value == null) {
                    removeData(xId, operation.getKey());
                } else {
                    writeData(xId, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddCustomer: {
                RMItem value = operation.getValue();
                removeData(xId, operation.getKey());
                break;
            }

            case AddCustomerID: {
                RMItem value = operation.getValue();
                if (value == null) {
                    removeData(xId, operation.getKey());
                } else {
                    writeData(xId, operation.getKey(), operation.getValue());
                }
                break;
            }

            case DeleteFlight: {
                break;
            }

            case DeleteCars: {
                break;
            }

            case DeleteRooms: {
                break;
            }


            case DeleteCustomer: {
                break;
            }

            case ReserveFlight: {
                break;
            }

            case ReserveCar: {
                break;
            }

            case ReserveRoom: {
                break;
            }   
            
            case QueryFlight: {
                break;
            }

            case QueryCars: {
                break;
            }

            case QueryRooms: {
                break;
            }

            case QueryCustomer: {
                break;
            }

            case QueryFlightPrice: {
                break;
            }

            case QueryCarPrice: {
                break;
            }
            
            case QueryRoomPrice: {
                break;
            }
        }
    }

    @Override
    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws IOException {
        Trace.info("RM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
        try {
            Stack txOps = this.txMap.get(xid);
            // transaction hasn't started... 
            if (txOps == null) {
                return false;
            }

            if (lockManager.Lock(xid, Integer.toString(flightNum), LockType.LOCK_WRITE)) {
                Flight curObj = (Flight)readData(xid, Flight.getKey(flightNum));
                if (curObj == null) {
                    txOps.push(new Operation(OperationType.AddFlight, 
                        Flight.getKey(flightNum), 
                        null));

                    Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
			        writeData(xid, newObj.getKey(), newObj);
			        Trace.info("RM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
                } else {
                    txOps.push(new Operation(OperationType.AddFlight, 
                        Flight.getKey(flightNum), 
                        new Flight(flightNum, curObj.getCount(), curObj.getPrice())));
                    curObj.setCount(curObj.getCount() + flightSeats);
                    if (flightPrice > 0)
                    {
                        curObj.setPrice(flightPrice);
                    }
                    writeData(xid, curObj.getKey(), curObj);
                    Trace.info("RM::addFlight(" + xid + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addCars(int xid, String location, int numCars, int carPrice) {
        Trace.info("RM::addCars(" + xid + ", " + location + ", "
                + numCars + ", $" + carPrice + ") called.");
       try{
            Stack txOps = this.txMap.get(xid);
            if(txOps == null)
                return false;

            if(lockManager.Lock(xid, location, LockType.LOCK_WRITE)){
                Car curObj = (Car) readData(xid, Car.getKey(location));
                if (curObj == null) {

                    txOps.push(new Operation(OperationType.AddCars, 
                        Car.getKey(location), 
                        null));

                    Car newObj = new Car(location, numCars, carPrice);
                    writeData(xid, newObj.getKey(), newObj);
                    Trace.info("RM::addCars(" + xid + ", " + location + ", "
                            + numCars + ", $" + carPrice + ") OK.");
                } else {

                    txOps.push(new Operation(OperationType.AddCars, 
                        Car.getKey(location),
                        new Car(location, curObj.getCount(), curObj.getPrice())));

                    curObj.setCount(curObj.getCount() + numCars);
                    if (carPrice > 0) {
                        curObj.setPrice(carPrice);
                    }
                    writeData(xid, curObj.getKey(), curObj);
                    Trace.info("RM::addCars(" + xid + ", " + location + ", "
                            + numCars + ", $" + carPrice + ") OK: "
                            + "cars = " + curObj.getCount() + ", price = $" + carPrice);
                }
                return true;
            }
            else{
                return false;
            }
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public boolean addRooms(int xid, String location, int numRooms, int roomPrice) {
        Trace.info("RM::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + roomPrice + ") called.");

        try{
            Stack txOps = txMap.get(xid);
            if(txOps == null)
                return false;

            if(lockManager.Lock(xid, location, LockType.LOCK_WRITE)){
                Room curObj = (Room) readData(xid, Room.getKey(location));
                if (curObj == null) {

                    txOps.push(new Operation(OperationType.AddRooms, 
                        Room.getKey(location), 
                        null));

                    Room newObj = new Room(location, numRooms, roomPrice);
                    writeData(xid, newObj.getKey(), newObj);
                    Trace.info("RM::addRooms(" + xid + ", " + location + ", "
                            + numRooms + ", $" + roomPrice + ") OK.");
                } else {

                    txOps.push(new Operation(OperationType.AddRooms, 
                        Room.getKey(location),
                        new Room(location, numRooms, roomPrice)));

                    curObj.setCount(curObj.getCount() + numRooms);
                    if (roomPrice > 0) {
                        curObj.setPrice(roomPrice);
                    }
                    writeData(xid, curObj.getKey(), curObj);
                    Trace.info("RM::addRooms(" + xid + ", " + location + ", "
                            + numRooms + ", $" + roomPrice + ") OK: "
                            + "rooms = " + curObj.getCount() + ", price = $" + roomPrice);
                }
                return true;
            } else{
                return false;
            }
        } catch(Exception e) {
            return false;
        }
    }

    @Override 
    public int queryFlight(int xid, int flightNum) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            if(lockManager.Lock(xid, Integer.toString(flightNum), LockType.LOCK_READ)) {
                return queryNum(xid, Flight.getKey(flightNum));
            } else {
                return -1;
            }
        } catch(Exception e) {
            return -1;
        }
    }

    @Override 
    public int queryCars(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            if(lockManager.Lock(xid, location, LockType.LOCK_READ)) {
                return queryNum(xid, Car.getKey(location));
            } else {
                return -1;
            }
        } catch(Exception e) {
            return -1;
        }
    }

    @Override 
    public int queryRooms(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            if(lockManager.Lock(xid, location, LockType.LOCK_READ)) {
                return queryNum(xid, Room.getKey(location));
            } else {
                return -1;
            }
        } catch(Exception e) {
            return -1;
        }
    }

    @Override
    public int newCustomer(int xid) throws IOException {
        Trace.info("RM::newCustomer(" + xid + ") called");
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                System.out.println("null!");
                return -1;
            }

            int cid = Integer.parseInt(String.valueOf(xid) +
            String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
            String.valueOf(Math.round(Math.random() * 100 + 1)));

            Customer customer = new Customer(cid);

            lockManager.Lock(xid, customer.getKey(), LockType.LOCK_WRITE);
            txOps.push(new Operation(OperationType.AddCustomer,
                customer.getKey(),
                customer));
            writeData(xid, customer.getKey(), customer);
            Trace.info("RM::newCustomer(" + xid + ") OK: " + cid);
            return cid;
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override 
    public boolean newCustomer(int xid, int cid) throws IOException {
        Trace.info("INFO: RM::newCustomer(" + xid + ", " + cid + ") called.");
        try {
            Stack txOps = txMap.get(xid);
            if(txOps == null)
                return false;

            Customer customer = (Customer) readData(xid, Customer.getKey(cid));

            if (customer == null) {
                if(lockManager.Lock(xid, Customer.getKey(cid), LockType.LOCK_WRITE)){
                    customer = new Customer(cid);
                    txOps.push(new Operation(OperationType.AddCustomerID, 
                        customer.getKey(), 
                        null));
                    writeData(xid, customer.getKey(), customer);
                    Trace.info("INFO: RM::newCustomer(" + xid + ", " + cid + ") OK.");
                    return true;
                } else {
                    return false;
                }
            } else {
                Trace.info("INFO: RM::newCustomer(" + xid + ", " +
                        cid + ") failed: customer already exists.");
                return false;
            }
        } catch(Exception e) {
            return false;
        }
    }
}