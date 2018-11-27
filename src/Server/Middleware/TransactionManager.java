package Server.Middleware;

import java.io.IOException;

public interface TransactionManager {
    public int start() throws IOException;
    public boolean commit(int transactionId) throws IOException, 
        TransactionAbortedException, InvalidTransactionException;
    public boolean abort(int transactionId) throws IOException;
    public boolean shutdown() throws IOException;
}