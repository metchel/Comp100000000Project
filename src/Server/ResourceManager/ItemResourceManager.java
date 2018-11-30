package Server.ResourceManager;

import Server.Sockets.CoordinatorStub;

public class ItemResourceManager extends TransactionResourceManager {
    private final CoordinatorStub coordinator;
    public ItemResourceManager(String name, CoordinatorStub coordinator) {
        super(name);
        this.coordinator = coordinator;
    }

    public void vote(int xid) {
        
    }
}