package Server.Network;

import java.util.Date;
import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -3189054142886919472L;
    private Date timeStamp;
    private Boolean status;
    private String message;

    public Response() {}

    public Response addCurrentTimeStamp() {
        Date current = new Date();
        return this;
    }

    public Response addStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public Response addMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String toString() {
        final String delim = "|";
        return this.timeStamp.toString() + delim
            + this.status.toString() + delim
            + this.message;
    }
}