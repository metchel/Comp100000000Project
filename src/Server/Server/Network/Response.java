package Server.Network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -3189054142886919472L;
    private String message;

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}