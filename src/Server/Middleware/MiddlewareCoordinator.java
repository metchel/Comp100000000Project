package Server.Middleware;

/*
Maintain a list of active transactions
– Keep track of which ResourceManagers are involved in a transaction (i.e. for each
operation of transaction T, the TM must be informed of all necessary RMs)
– Implement 1-phase commit: tell the appropriate ResourceManagers that they should
commit/abort the transaction
– Handle client disconnects by implementing a time-to-live mechanism. Every time an
operation involving a transaction is performed, the time is reset. If the time-to-live
expires then the transaction should be aborted
*/

import Server.Common.Command;
import Server.Common.Constants;
import Server.Common.Trace;
import Server.Network.CanCommitRequest;
import Server.Network.DoCommitRequest;
import Server.Network.Response;
import Server.Middleware.Transaction.Status;
import Server.Sockets.RequestHandler;
import Server.ResourceManager.TransactionResourceManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

public class MiddlewareCoordinator {
    private final ScheduledExecutorService ttlWorkerPool;
    private final Map<Integer, Transaction> transactionMap;
    private final Map<Integer, Status> transactionStatusMap;
    private final Map<Integer, Set<String>> rmMap;
    
    private final TransactionResourceManager customerRM;
    private final MiddlewareResourceManager flightRM;
    private final MiddlewareResourceManager carRM;
    private final MiddlewareResourceManager roomRM;

    private static final String CUSTOMER = Constants.CUSTOMER;
    private static final String FLIGHT = Constants.FLIGHT;
    private static final String ROOM = Constants.ROOM;
    private static final String CAR = Constants.CAR;
    private Map<Integer, Boolean> crashMap;

    public MiddlewareCoordinator(TransactionResourceManager customerRM,
                                MiddlewareResourceManager flightRM,
                                MiddlewareResourceManager carRM,
                                MiddlewareResourceManager roomRM) {
        this.transactionMap = new HashMap<Integer, Transaction>();
        this.transactionStatusMap = new HashMap<Integer, Status>();
        this.rmMap = new HashMap<Integer, Set<String>>();
        this.ttlWorkerPool = Executors.newScheduledThreadPool(1);

        this.customerRM = customerRM;
        this.flightRM = flightRM;
        this.carRM = carRM;
        this.roomRM = roomRM;
        this.crashMap = initCrashMap();
    }

    public int start() {
        Transaction t = new Transaction();
        t.start();
        int id = t.getId();
        this.transactionMap.put(id, t);
        this.transactionStatusMap.put(id, Status.STARTED);
        this.rmMap.put(id, new HashSet<String>());

        this.ttlWorkerPool.schedule(new TtlWorker(t), t.getTtl(), TimeUnit.MILLISECONDS);
        return id;
    }

    public void updateTransactionTtl(int transactionId) {
        Transaction t = this.transactionMap.get(transactionId);
        t.updateTtl();
        this.ttlWorkerPool.schedule(new TtlWorker(t), t.getTtl(), TimeUnit.MILLISECONDS);
    }

    public boolean hasCommited(Integer xid) {
        return this.transactionStatusMap.get(xid) == Status.COMMITTED;
    }

    public boolean hasAborted(Integer xid) {
        return this.transactionStatusMap.get(xid) == Status.ABORTED;
    }

    public boolean hasStarted(Integer xid) {
        return transactionStatusMap.get(xid) == Status.STARTED;
    }

    public boolean exists(Integer xid) {
        return hasStarted(xid);
    }

    public boolean commit(int xId) throws IOException, ClassNotFoundException {
        Transaction t = (Transaction) this.transactionMap.get(xId);
        if (t.voting()) {
            this.transactionStatusMap.put(xId, Status.VOTING);
        }

        HashMap<String, Boolean> voteMap = new HashMap<String, Boolean>();
        if (this.crashMap.get(1)){
            System.exit(1);
        }

        for (MiddlewareResourceManager rm : t.getClients()) {
            if (t.getClients().isEmpty()) {
                break;
            }
            rm.send(new CanCommitRequest(xId));
            Response res = rm.receive();
            Trace.info(res.toString());
            if (this.crashMap.get(3)){
                System.exit(1);
            }

            if (res.getMessage().equals("true")) {
                voteMap.put(rm.getName(), false);
                rm.forceFailureDetection();
            } else {
                voteMap.put(rm.getName(), res.getStatus());
            }
        }

        if (rmMap.get(xId).contains(CUSTOMER)) {
            voteMap.put(CUSTOMER, this.customerRM.prepare(xId));
        }

        if (this.crashMap.get(4)){
            System.exit(1);
        }

        Trace.info(voteMap.toString());

        boolean allPrepared = true;
        if (voteMap.containsValue(false)) {
            allPrepared = false;
        }

        HashMap<String, Boolean> commitMap = new HashMap<String, Boolean>();

        if (this.crashMap.get(5)){
            System.exit(1);
        }
        if (allPrepared) {
            this.transactionStatusMap.put(xId, Status.PREPARED);

            boolean allCommitted = false;
            for (MiddlewareResourceManager rm : t.getClients()) {
                rm.send(new DoCommitRequest(xId));
                Response res = rm.receive();
                if (this.crashMap.get(6)){
                    System.exit(1);
                }
                commitMap.put(rm.getName(), res.getStatus());
            }

            if (rmMap.get(xId).contains(CUSTOMER)) {
                commitMap.put(CUSTOMER, this.customerRM.commit(xId));
            }

            if (this.crashMap.get(7)){
                System.exit(1);
            }
            for (Boolean commitSuccess : commitMap.values()) {
                if (commitSuccess) {
                    allCommitted = true;
                } else {
                    allCommitted = false;
                    break;
                }
            }


            if (allCommitted) {
                this.transactionStatusMap.put(xId, Status.COMMITTED);
                return true;
            } else {
                for (String rm : commitMap.keySet()) {
                    if (commitMap.get(rm).equals(true)) {
                        boolean res;
                        if (rm.equals(CUSTOMER)) {
                            res = this.customerRM.abort(xId);
                        } else {
                            res = getRMFromString(rm).abort(xId);
                        }
                    }
                }
                return false;
            }
        } else {
            if (this.crashMap.get(5)){
                System.exit(1);
            }
            for (String rm : voteMap.keySet()) {
                if (voteMap.get(rm).equals(true)) {
                    boolean res;
                    if (rm.equals(CUSTOMER)) {
                        res = this.customerRM.abort(xId);
                    } else {
                        res = getRMFromString(rm).abort(xId);
                    }
                    if (this.crashMap.get(6)){
                        System.exit(1);
                    }
                }
            }
            this.transactionStatusMap.put(xId, Status.ABORTED);
            if (this.crashMap.get(7)){
                System.exit(1);
            }
            return false;
        }
    }

    private MiddlewareResourceManager getRMFromString(String rm) {
        switch(rm) {
            case FLIGHT:
                return this.flightRM;
            case CAR:
                return this.carRM;
            case ROOM:
                return this.roomRM;
        }

        return null;
    }

    public boolean abort(int transactionId) {
        Trace.info("ABORT " + transactionId);
        Transaction t = (Transaction) this.transactionMap.get(transactionId);
        boolean success = true;
        success = success && this.customerRM.abort(transactionId);
        for (MiddlewareResourceManager client : t.getClients()) {
            try {
                success = success && client.abort(t.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.transactionStatusMap.put(transactionId, Status.ABORTED);
        t.abort();

        return success;
    }

    public boolean forceCrash(int mode){
        Trace.info("Force MW Crash " + mode);
        this.crashMap.put(mode,true);
        return ((Boolean) this.crashMap.get(mode));
    }
    public void setCrash(int mode) {
        this.crashMap.put(mode, true);
    }

    public void resetCrashes() {
        for (Integer mode: crashMap.keySet()) {
            this.crashMap.put(mode, false);
        }
    }
    public static Map initCrashMap() {
        Map<Integer, Boolean> tmp = new HashMap<Integer, Boolean>();
        for (int i = 1; i < 9; i++){
            tmp.put(i,false);
        }
        return tmp;
    }

    public synchronized boolean shutdown() {
        return false;
    }

    public void addOperation(int transactionId, String rm) {
        try {
            HashSet<String> transactionRMs = (HashSet) this.rmMap.get(transactionId);
            Transaction t = this.transactionMap.get(transactionId);
            if (!rm.equals(CUSTOMER)) {
                t.addClient(rmFromString(rm));
            }
            if (!transactionRMs.contains(rm)) {
                transactionRMs.add(rm);
            }
        } catch (NullPointerException e) {
            Trace.info("Transaction " + transactionId + " has not started.");
        }
    }

    private MiddlewareResourceManager rmFromString(String rm) {
        if (rm.equals(Constants.FLIGHT)) {
            return this.flightRM;
        } else if (rm.equals(Constants.CAR)) {
            return this.carRM;
        } else if (rm.equals(Constants.ROOM)) {
            return this.roomRM;
        } else {
            return null;
        }
    }

    public Set<String> getTransactionRms(int transactionId) {
        Integer xId = new Integer(transactionId);
        return this.rmMap.get(xId);
    }

    private class TtlWorker implements Runnable {
        final Transaction tx;

        public TtlWorker(Transaction tx) {
            this.tx = tx;
        }

        @Override
        public void run() {
            synchronized (this.tx) {
                if (tx.getStatus() != Transaction.Status.STARTED) {
                    return;
                }
                long ttl = this.tx.getTtl();
                if (ttl <= 0) {
                    Trace.info("Reached time to live for transaction " + this.tx.getId() + " IT SHOULD ABORT NOW BUT SHITTY DESIGN PREVENTS THIS FROM BEING EASY");
                    System.out.println("ABORTING CLIENT");
                    try {
                        abort(tx.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
