package Server.Transactions;

import Server.Common.Customer;
import Server.Common.RMItem;

public class ReserveOperation extends Operation {
    private final String cid;
    private final Customer customer;

    public ReserveOperation(OperationType type, String cid, Customer customer, String key, RMItem value) {
        super(type, key, value);
        this.cid = cid;
        this.customer = customer;
    }

    public String getCustomerId() {
        return this.cid;
    }

    public Customer getCustomer() {
        return this.customer;
    }
}