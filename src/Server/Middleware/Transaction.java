package Server.Middleware;

import Server.Common.Constants;
import Server.Common.Command;
import Server.Common.RMItem;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

public class Transaction {
    private final static long DEFAULT_TTL = 10000;
    private static int nextTransactionId;
    private final int id;
    private final Set clients;
    private Status status;
    private final Queue commands;
    private final Set<RMItem> localData;
    private long ttl;

    public Transaction() {
        this.id = getNextTransactionId();
        this.ttl = System.currentTimeMillis() + DEFAULT_TTL;
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

    public boolean voting(){
        this.status = Status.VOTING;
        return true;
    }

    public boolean commit() {
        this.status = Status.COMMITTED;
        //changed this below
        return true;
    }

    public void abort() {
        this.status = Status.ABORTED;
    }

    public void updateTtl() {
        this.ttl = this.ttl + DEFAULT_TTL;
        System.out.println(this.ttl);
    }

    public long getTtl() {
        final long now = System.currentTimeMillis();

        return (this.ttl - now);
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
        ABORTED,
        VOTING
    }
}