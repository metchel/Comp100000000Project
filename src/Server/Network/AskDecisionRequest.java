package Server.Network;

public class AskDecisionRequest extends Request {
    private static final long serialVersionUID = 4L;
    private final int xid;
    public AskDecisionRequest(int xid) {
        super();
        this.xid = xid;
        RequestData data = new RequestData();
        data.addXId(xid);
        addData(data);
        addCurrentTimeStamp();
    }
}