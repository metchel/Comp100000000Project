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
import Server.Middleware.Transaction.Status;
import Server.Sockets.RequestHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class MiddlewareCoordinator implements TransactionManager {
    private RequestHandler handler;
    private final ScheduledExecutorService ttlWorkerPool;
    private final Map<Integer, Transaction> transactionMap;
    private final Map<Integer, Status> transactionStatusMap;
    private final Map<Integer, Set<String>> rmMap;
    private static final String CUSTOMER = Constants.CUSTOMER;
    private static final String FLIGHT = Constants.FLIGHT;
    private static final String ROOM = Constants.ROOM;
    private static final String CAR = Constants.CAR;

    public MiddlewareCoordinator() {
        this.transactionMap = new HashMap<Integer, Transaction>();
        this.transactionStatusMap = new HashMap<Integer, Status>();
        this.rmMap = new HashMap<Integer, Set<String>>();
        this.ttlWorkerPool = Executors.newScheduledThreadPool(2);
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
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

    public synchronized boolean hasCommited(Integer xid) {
        return this.transactionStatusMap.get(xid) == Status.COMMITTED;
    }

    public synchronized boolean hasAborted(Integer xid) {
        return this.transactionStatusMap.get(xid) == Status.ABORTED;
    }

    public synchronized boolean hasStarted(Integer xid) {
        return transactionStatusMap.get(xid) == Status.STARTED;
    }
    
    public synchronized boolean exists(Integer xid) {
        return transactionMap.get(xid) != null;
    }

    public synchronized boolean commit(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        boolean commitSuccess = t.commit();
        if (commitSuccess) {
            this.transactionStatusMap.put(transactionId, Status.COMMITTED);
        }
        return commitSuccess;
    }

    public synchronized void abort(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        this.transactionStatusMap.put(transactionId, Status.ABORTED);
        t.abort();
    }

    public synchronized boolean shutdown() {
        return false;
    }

    public void addOperation(int transactionId, String rm) {
        try {
            HashSet<String> transactionRMs = (HashSet)this.rmMap.get(transactionId);
            if (!transactionRMs.contains(rm)) {
                    transactionRMs.add(rm);
            }
        } catch(NullPointerException e) {
            Trace.info("Transaction " + transactionId + " has not started.");
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
            synchronized(this.tx) {
                if (tx.getStatus() != Transaction.Status.STARTED) {
                    return;
                }
                long ttl = this.tx.getTtl();
                if (ttl <= 0) {
                    Trace.info("Reached time to live for transaction " + this.tx.getId() + " IT SHOULD ABORT NOW BUT SHITTY DESIGN PREVENTS THIS FROM BEING EASY");
                }
            }
        }
    }
}