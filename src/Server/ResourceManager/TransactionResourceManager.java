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

    public synchronized boolean start(int xId) {
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

    public synchronized boolean commit(int xId) {
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
                    txOps.push(new Operation(OperationType.ADD, 
                        Flight.getKey(flightNum), 
                        null));

                    Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
			        writeData(xid, newObj.getKey(), newObj);
			        Trace.info("RM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
                } else {
                    txOps.push(new Operation(OperationType.UPDATE, 
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