package Server.ResourceManager;

import Server.Network.AskDecisionRequest;
import Server.Sockets.CoordinatorStub;
import Server.Network.*;
import Server.Common.*;

public class ItemResourceManager extends TransactionResourceManager {
    private final CoordinatorStub coordinator;
    public ItemResourceManager(String name, CoordinatorStub coordinator) {
        super(name);
        this.coordinator = coordinator;

        for (Integer xid: this.getStatusMap().keySet()) {
            String status = this.getStatusMap().get(xid);
            if (status.equals("PREPARED")) {
                askDecision(xid);
            }
        }
    }

    public void vote(int xid) {
        
    }

    public void askDecision(int xid) {
        Trace.info("ASKING FOR DECISION FROM COORDINATOR");
        CommitSuccessResponse decision = this.coordinator.sendAskDecisionRequest(new AskDecisionRequest(xid));
        if (decision != null) {
            if (decision.getStatus().equals(true)) {
                this.commit(xid);
            } else {
                this.abort(xid);
            }
        }
    }
}