package Server.ResourceManager;

import Server.Common.*;
import Server.LockManager.LockManager;
import Server.LockManager.DeadlockException;
import Server.LockManager.TransactionLockObject.LockType;
import Server.Transactions.Operation;
import Server.Transactions.Operation.OperationType;

import java.io.IOException;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

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
        } catch (Exception e) {
            Trace.info("Could not enlist transaction " + xId);
            return false;
        }
        return true;
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
                break;
            }

            case AddCustomerID: {
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
}