package Server.ResourceManager;

import Server.Common.*;
import Server.LockManager.LockManager;
import Server.LockManager.DeadlockException;
import Server.LockManager.TransactionLockObject.LockType;
import Server.Middleware.TransactionManager;
import Server.Transactions.Operation;
import Server.Transactions.Operation.OperationType;
import Server.Transactions.ReserveOperation;

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

    public synchronized boolean start(int xId) {
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

    public synchronized boolean commit(int xId) {
        try {
            Trace.info("Committing transaction " + xId);
            Stack txOps = this.txMap.get(xId);
            if (txOps == null) {
                Trace.info("Could not commit " + xId);
                return false;
            }

            this.txMap.remove(xId);
            return lockManager.UnlockAll(xId);
        } catch(Exception e) {
            Trace.info("Could not commit transaction " + xId);
            return false;
        }
    }

    public synchronized boolean abort(int xId){
        try {
            System.out.println("Aborting transaction " + xId);
            Stack txOps = txMap.get(xId);

            if(txOps == null) {
                Trace.warn("null operations!");
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
            Trace.warn("Exception during abort!");
            return false;
        }
    }

    public boolean shutdown() {
        return false;
    }

    public void undo(int xid, Operation operation) {
        OperationType type = operation.getOperationType();

        switch(type) {

            case AddFlight: {
                Trace.info("RM::undo AddFlight");
                RMItem value = operation.getValue();
                if (value == null) {
                    deleteItem(xid, operation.getKey());
                } else {
                    writeData(xid, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddCars: {
                Trace.info("RM::undo AddCars");
                RMItem value = operation.getValue();
                if (value == null) {
                    deleteItem(xid, operation.getKey());
                } else {
                    writeData(xid, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddRooms: {
                Trace.info("RM::undo AddRooms");
                RMItem value = operation.getValue();
                if (value == null) {
                    deleteItem(xid, operation.getKey());
                } else {
                    writeData(xid, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddCustomer: {
                Trace.info("RM::undo AddCustomer");
                RMItem value = operation.getValue();
                if (value == null) {
                    deleteItem(xid, operation.getKey());
                } else {
                    writeData(xid, operation.getKey(), operation.getValue());
                }
                break;
            }

            case AddCustomerID: {
                Trace.info("RM::undo AddCustomerID");
                RMItem value = operation.getValue();
                if (value == null) {
                    deleteItem(xid, operation.getKey());
                } else {
                    writeData(xid, operation.getKey(), operation.getValue());
                }
                break;
            }

            case DeleteFlight: {
                Trace.info("RM::undo DeleteFlight");
                writeData(xid, operation.getKey(), operation.getValue());
                break;
            }

            case DeleteCars: {
                Trace.info("RM::undo DeleteCars");
                writeData(xid, operation.getKey(), operation.getValue());
                break;
            }

            case DeleteRooms: {
                Trace.info("RM::undo DeleteRooms");
                writeData(xid, operation.getKey(), operation.getValue());
                break;
            }

            case DeleteCustomer: {
                Trace.info("RM::undo DeleteCustomer");
                writeData(xid, operation.getKey(), operation.getValue());
                break;
            }

            case ReserveFlight: {
                Trace.info("RM::undo ReserveFlight");
                ReserveOperation reserveOp = (ReserveOperation) operation;
                System.out.println(reserveOp.getCustomer().getBill());
                writeData(xid, reserveOp.getCustomerId(), reserveOp.getCustomer());
                writeData(xid, reserveOp.getKey(), reserveOp.getValue());
                break;
            }

            case ReserveCar: {
                Trace.info("RM::undo ReserveCar");
                ReserveOperation reserveOp = (ReserveOperation) operation;
                writeData(xid, reserveOp.getCustomerId(), reserveOp.getCustomer());
                writeData(xid, reserveOp.getKey(), reserveOp.getValue());
                break;
            }

            case ReserveRoom: {
                Trace.info("RM::undo ReserveRoom");
                ReserveOperation reserveOp = (ReserveOperation) operation;
                writeData(xid, reserveOp.getCustomerId(), reserveOp.getCustomer());
                writeData(xid, reserveOp.getKey(), reserveOp.getValue());
                break;
            }   
            
            /**
             * Read only
             */
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
            } else {
                return false;
            }
        } catch(Exception e) {
            return false;
        }
    }

    @Override 
    public boolean deleteFlight(int xid, int flightNum) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return false;
            }

            lockManager.Lock(xid, Integer.toString(flightNum), LockType.LOCK_WRITE);
            txOps.push(new Operation(OperationType.DeleteFlight, 
                Flight.getKey(flightNum),
                readData(xid, Flight.getKey(flightNum))));
            
            return deleteItem(xid, Flight.getKey(flightNum));
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override 
    public boolean deleteCars(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return false;
            }

            lockManager.Lock(xid,location, LockType.LOCK_WRITE);
            txOps.push(new Operation(OperationType.DeleteCars, 
                Car.getKey(location),
                readData(xid, Car.getKey(location))));
            
            return deleteItem(xid, Car.getKey(location));
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Override 
    public boolean deleteRooms(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return false;
            }

            lockManager.Lock(xid,location, LockType.LOCK_WRITE);
            txOps.push(new Operation(OperationType.DeleteRooms, 
                Room.getKey(location),
                readData(xid, Room.getKey(location))));
            
            return deleteItem(xid, Room.getKey(location));
        } catch(Exception e) {
            e.printStackTrace();
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
    public int queryFlightPrice(int xid, int flightNum) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            lockManager.Lock(xid, Integer.toString(flightNum), LockType.LOCK_READ);
            return queryPrice(xid, Flight.getKey(flightNum));
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            lockManager.Lock(xid, location, LockType.LOCK_READ);
            return queryPrice(xid, Car.getKey(location));
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int xid, String location) throws IOException {
        try {
            Stack txOps = this.txMap.get(xid);
            if (txOps == null) {
                return -1;
            }

            lockManager.Lock(xid, location, LockType.LOCK_READ);
            return queryPrice(xid, Room.getKey(location));
        } catch(Exception e) {
            e.printStackTrace();
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

    @Override 
    public boolean deleteCustomer(int xid, int cid) throws IOException {
        try {
            Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") called.");
            Customer customer = (Customer) readData(xid, Customer.getKey(cid));

            Stack txOps = txMap.get(xid);
            if (txOps == null) {
                return false;
            }

            if (customer == null) {
                Trace.warn("RM::deleteCustomer(" + xid + ", "
                + cid + ") failed: customer doesn't exist.");
                return false;
            } else {
                lockManager.Lock(xid, Customer.getKey(cid), LockType.LOCK_WRITE);
                txOps.push(new Operation(OperationType.DeleteCustomer,
                    customer.getKey(),
                    customer));
                RMHashMap reservations = customer.getReservations();
                for (String reservedKey : reservations.keySet()) {        
                    ReservedItem reserveditem = customer.getReservedItem(reservedKey);
                    Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times");
                    ReservableItem item  = (ReservableItem)readData(xid, reserveditem.getKey());
                    Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") has reserved " + reserveditem.getKey() + " which is reserved " +  item.getReserved() +  " times and is still available " + item.getCount() + " times");
                    item.setReserved(item.getReserved() - reserveditem.getCount());
                    item.setCount(item.getCount() + reserveditem.getCount());
                    writeData(xid, item.getKey(), item);
                }
                removeData(xid, customer.getKey());
                Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") OK.");
                return true;
            }

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String queryCustomerInfo(int xid, int cid) {
        try {
            Trace.info("RM::queryCustomerInfo(" + xid + ", " + cid + ") called.");

            Stack txOps = txMap.get(xid);
            if (txOps == null) {
                return "";
            }

            Customer customer = (Customer) readData(xid, Customer.getKey(cid));

            if (customer == null) {
                Trace.warn("RM::queryCustomerInfo(" + xid + ", "
                + cid + ") failed: customer doesn't exist.");
                return "";
            } else {
                if (lockManager.Lock(xid, customer.getKey(), LockType.LOCK_READ)) {
                    Trace.info("RM::queryCustomerInfo(" + xid + ", " + cid + "): \n");
                    return customer.getBill();
                } else {
                    return "";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public RMHashMap reserveFlight(int xid, int cid, int flightNum) {
        boolean success;
        try{
            Stack txOps = txMap.get(xid);
            if (txOps == null) {
                success = false;
            }
            Customer customer = (Customer) readData(xid, Customer.getKey(cid));
            Flight curObj = (Flight)readData(xid, Flight.getKey(flightNum));

            if(lockManager.Lock(xid, Flight.getKey(flightNum), LockType.LOCK_WRITE)){
                txOps.push(new ReserveOperation(OperationType.ReserveFlight, 
                    Customer.getKey(cid), 
                    customer,
                    Flight.getKey(flightNum),
                    curObj));
                success = reserveItem(xid, cid,
                        Flight.getKey(flightNum), String.valueOf(flightNum));
            } else {
                success = false;
            }

            if (success) {
                Customer updatedCustomer = (Customer) readData(xid, Customer.getKey(cid));
                return updatedCustomer.getReservations();
            } else {
                return null;
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RMHashMap reserveCar(int xid, int cid, String location) {
        boolean success;
        try{
            Stack txOps = txMap.get(xid);
            if (txOps == null) {
                success =  false;
            }
            Customer customer = (Customer) readData(xid, Customer.getKey(cid));
            Car curObj = (Car)readData(xid, Car.getKey(location));

            if(lockManager.Lock(xid, Car.getKey(location), LockType.LOCK_WRITE)){
                txOps.push(new ReserveOperation(OperationType.ReserveCar, 
                    Customer.getKey(cid), 
                    customer,
                    Car.getKey(location),
                    curObj));
                success = reserveItem(xid, cid,
                        Car.getKey(location), location);
            } else {
                success = false;
            }

            if (success) {
                Customer updatedCustomer = (Customer) readData(xid, Customer.getKey(cid));
                return updatedCustomer.getReservations();
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RMHashMap reserveRoom(int xid, int cid, String location) {
        boolean success;
        try{
            Stack txOps = txMap.get(xid);
            if (txOps == null) {
                success = false;
            }
            Customer customer = (Customer) readData(xid, Customer.getKey(cid));
            Room curObj = (Room)readData(xid, Room.getKey(location));

            if(lockManager.Lock(xid, Room.getKey(location), LockType.LOCK_WRITE)){
                txOps.push(new ReserveOperation(OperationType.ReserveRoom, 
                    Customer.getKey(cid), 
                    customer,
                    Room.getKey(location),
                    curObj));
                success = reserveItem(xid, cid,
                        Room.getKey(location), location);
            } else {
                success =  false;
            }

            if (success) {
                Customer updatedCustomer = (Customer) readData(xid, Customer.getKey(cid));
                return updatedCustomer.getReservations();
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateReservationData(Integer xid, Integer cid, RMHashMap data) {
        if (data == null) {
            return;
        } else {
            Customer customer = (Customer) readData(xid.intValue(), Customer.getKey(cid.intValue()));
            customer.mergeReservationData(data);

            this.m_data.put(customer.getKey(cid.intValue()), customer);
        }
    }

    public void addReserveFlightOp(int xid, int cid) {
        Stack txOps = txMap.get(xid);
        Customer customer = (Customer) readData(xid, Customer.getKey(cid));
        txOps.push(new ReserveOperation(OperationType.ReserveFlight, 
                    Customer.getKey(cid), 
                    customer,
                    null,
                    null));
    }

    public void addReserveCarOp(int xid, int cid) {
        Stack txOps = txMap.get(xid);
        Customer customer = (Customer) readData(xid, Customer.getKey(cid));
        txOps.push(new ReserveOperation(OperationType.ReserveCar, 
                    Customer.getKey(cid), 
                    customer,
                    null,
                    null));
    }

    public void addReserveRoomOp(int xid, int cid) {
        Stack txOps = txMap.get(xid);
        Customer customer = (Customer) readData(xid, Customer.getKey(cid));
        txOps.push(new ReserveOperation(OperationType.ReserveRoom, 
                    Customer.getKey(cid), 
                    customer,
                    null,
                    null));
    }
}