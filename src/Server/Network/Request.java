package Server.Network;

import java.util.Date;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = -3189054142886919472L;
    private Date timeStamp;
    private RequestData data;

    public Request() {}

    public Request addCurrentTimeStamp() {
        this.timeStamp = new Date();
        return this;
    }

    public Request addData(RequestData data) {
        this.data = data;
        return this;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public RequestData getData() {
        return this.data;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return this.data.toString();
    }
}