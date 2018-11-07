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

import java.util.Set;

import Server.Middleware.Transaction.Status;

import java.util.Map;
import java.util.HashMap;

public class MiddlewareCoordinator implements TransactionManager {
    private final Map<Integer, Transaction> transactionMap;
    private final Map<Integer, Status> transactionStatusMap;

    public MiddlewareCoordinator() {
        this.transactionMap = new HashMap<Integer, Transaction>();
        this.transactionStatusMap = new HashMap<Integer, Status>();
    }

    public int start() {
        Transaction t = new Transaction();
        t.start();
        int id = t.getId();
        this.transactionMap.put(id, t);
        this.transactionStatusMap.put(id, Status.STARTED);
        return id;
    }

    public boolean commit(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        boolean commitSuccess = t.commit();
        if (commitSuccess) {
            this.transactionStatusMap.put(transactionId, Status.COMMITTED);
        }
        return commitSuccess;
    }

    public void abort(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        t.abort();
    }

    public boolean shutdown() {
        return false;
    }
}