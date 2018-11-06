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
import java.util.Map;
import java.util.HashMap;

public class MiddlewareCoordinator implements TransactionManager {
    public static int nextTransactionId = 0;
    private final Map<Integer, Transaction> transactionMap;

    public MiddlewareCoordinator() {
        this.transactionMap = new HashMap<Integer, Transaction>();
    }

    public int start() {
        int nextT = MiddlewareCoordinator.nextTransactionId + 1;
        Transaction t = new Transaction();
        t.start();
        this.transactionMap.put(new Integer(nextT), t);
        MiddlewareCoordinator.nextTransactionId++;
        return nextT;
    }

    public boolean commit(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        return t.commit();
    }

    public void abort(int transactionId) {
        Transaction t = (Transaction)this.transactionMap.get(transactionId);
        t.abort();
    }

    public boolean shutdown() {
        return false;
    }
}