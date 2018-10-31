package Server.Network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = -3189054142886919472L;
    
    private Header header;
    private Message message;

    public Request(Header header, Message message) {
        this.header = header;
        this.message = message;
    }

    public String toString() {
        return this.header.toString() + " " + this.message.toString();
    }
}