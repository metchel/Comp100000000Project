package Server.Middleware;

import Server.Common.Constants;
import Server.Common.Command;
import Server.Common.RMItem;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

public class Transaction {
    private static int nextTransactionId;
    private final int id;
    private final Set clients;
    private Status status;
    private final Queue commands;
    private final Set<RMItem> localData;

    public Transaction() {
        this.id = this.getNextTransactionId();
        this.clients = new HashSet<MiddlewareClient>();
        this.commands = new LinkedList<Command>();
        this.localData = new HashSet<RMItem>();
    }

    public static int getNextTransactionId() {
        synchronized(Transaction.class) {
            Transaction.nextTransactionId++;
            return Transaction.nextTransactionId;
        }
    }

    public int start() {
        this.status = Status.STARTED;
        return id;
    }

    public boolean commit() {
        this.status = Status.COMMITTED;
        return false;
    }

    public void abort() {
        this.status = Status.ABORTED;
    }

    public int getId () {
        return this.id;
    }

    public Set getClients() {
        return this.clients;
    }

    public Status getStatus() {
        return this.status;
    }

    public void addCommand(Command c) {
        this.commands.add(c);
    }

    public void addClient(MiddlewareClient client) {
        if (!this.clients.contains(client)) {
            this.clients.add(client);
        }
    }

    enum Status {
        STARTED,
        COMMITTED,
        ABORTED
    }
}