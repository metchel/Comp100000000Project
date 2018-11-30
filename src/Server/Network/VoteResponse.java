package Server.Network;

public class VoteResponse extends Response {
    private static final long serialVersionUID = -1;
    private int xId;
    private String vote;
    public VoteResponse(int transactionId, String yesOrNo) {
        super();
        this.xId = transactionId;
        this.addCurrentTimeStamp();
        if (yesOrNo.equals("YES")) {
            this.addStatus(true);
        } 
        if (yesOrNo.equals("NO")) {
            this.addStatus(false);
        }
        this.vote = yesOrNo;
        this.addMessage("Votes " + yesOrNo + " for transaction " + transactionId);
    }

    public String getVote() {
        return this.vote;
    }

    public int getTransactionId() {
        return this.xId;
    }
}