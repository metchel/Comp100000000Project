package Server.Network;

import java.util.Date;

import Server.Common.RMHashMap;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -3189054142886919472L;
    private Date timeStamp;
    private Boolean status;
    private String message;
    private RMHashMap reservationData;

    public Response() {}

    public Response addCurrentTimeStamp() {
        this.timeStamp = new Date();
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

    public Response addReservationData(Object data) {
        this.reservationData = (RMHashMap)data;
        return this;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public RMHashMap getReservationData() {
        return this.reservationData;
    }

    public String toString() {
        final String delim = "|";
        return this.status.toString() + delim
            + this.message;
    }
}