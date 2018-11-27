package Server.Network;

public class DoCommitRequest extends Request {
    private static final long serialVersionUID = 1L;

    public DoCommitRequest(int transactionId) {
        super();
        addCurrentTimeStamp();
        RequestData data = new RequestData();
        data.addXId(transactionId);
        addData(data);
    }
}