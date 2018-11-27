package Server.Network;

public class CanCommitRequest extends Request {
    private static final long serialVersionUID = 0L;
    
    public CanCommitRequest(int xId) {
        super();
        addCurrentTimeStamp();
        RequestData data = new RequestData();
        data.addXId(xId);
        addData(data);
    }
}