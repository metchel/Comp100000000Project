package Server.Network;

public class CommitSuccessResponse extends Response {
    private int xId;
    private static final long serialVersionUID = -2;
    public CommitSuccessResponse(int transactionId, boolean success) {
        super();
        this.xId = transactionId;
        this.addStatus(success);
        if (success) {
            this.addMessage("Successfully committed transaction " + transactionId);
        }
    }
}