package Server.Network;

import Server.Middleware.MiddlewareResourceManager;
import java.util.Set;

public class InformGroupRequest extends Request {
    private static final long serialVersionUID = -3;
    private final String participantAddress;
    private final int participantPort;

    public InformGroupRequest(String address, int port) {
        super();
        this.participantAddress = address;
        this.participantPort = port;
    }

    public String getAddress() {
        return this.participantAddress;
    }

    public int getPort() {
        return this.participantPort;
    }
}