package Server.Network;

public class Header {
    private String dataItem;
    private String operation;

    public Header(String dataItem, String operation) {
        this.dataItem = dataItem;
        this.operation = operation;
    }

    public String toString() {
        return this.dataItem + " " + this.operation;
    }
}