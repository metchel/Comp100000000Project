package Server.Transactions;

public class Operation {
    private OperationType type;
    private String key;
    private Object value;

    public Operation(OperationType type; String key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public OperationType getType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public Object undo() {
        return null;
    }

    public enum OperationType  {
        ADD, 
        DELETE, 
        RESERVE,
        UPDATE
    }
}