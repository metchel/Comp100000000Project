package Server.Sockets;
import Server.Network.*;
import java.io.*;
import java.net.*;


public class CoordinatorStub {
    private final InetAddress inetAddress;
    private final int port;

    public CoordinatorStub(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public Response sendAskDecisionRequest(AskDecisionRequest r) {
        try {
            final Socket SOCKET = new Socket(inetAddress, port);
            final ObjectOutputStream OOS = new ObjectOutputStream(SOCKET.getOutputStream());
            final ObjectInputStream OIS = new ObjectInputStream(SOCKET.getInputStream());
            OOS.writeObject(r);
            return (Response) OIS.readObject();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendVoteResponse(VoteResponse v) {
        try {
            final Socket SOCKET = new Socket(inetAddress, port);
            final ObjectOutputStream OOS = new ObjectOutputStream(SOCKET.getOutputStream());
            OOS.writeObject(v);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}