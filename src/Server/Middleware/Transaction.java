package Server.Middleware;

import Server.Common.Constants;
import Server.Common.Command;

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

    public Transaction() {
        this.id = nextTransactionId;
        nextTransactionId++;
        this.clients = new HashSet<String>();
        this.commands = new LinkedList<Command>();
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

    enum Status {
        STARTED,
        COMMITTED,
        ABORTED
    }
}