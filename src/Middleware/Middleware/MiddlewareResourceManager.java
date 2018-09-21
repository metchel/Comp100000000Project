package Middleware;

import Server.Interface.IResourceManager;

public class MiddlewareResourceManager implements IResourceManager {

    private ArrayList<ResourceManager> resourceManagers = new ArrayList<ResourceManager>();

    public MiddlewareResourceManager(ResourceManager[] resourceManagers) {
        for (ResourceManager rm: resourceManagers) {
            this.resourceManagers.add(rm);
        }
    }

    // TODO
    public synchronized boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized int newCustomer(int id) throws RemoteException {
        return -1;
    }

    // TODO
    public synchronized boolean newCustomer(int id, int cid) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean deleteCars(int id, String location) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean deleteRooms(int id, String location) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean deleteCustomer(int id, int customerID) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized int queryFlight(int id, int flightNumber) throws RemoteException {
        return -1;
    }

    // TODO
    public synchronized int queryCars(int id, String location) throws RemoteException {
        return -1;
    }

    // TODO
    public synchronized int queryRooms(int id, String location) throws RemoteException {
        return -1;
    }

    // TODO
    public synchronized String queryCustomerInfo(int id, int customerID)  throws RemoteException {
        return "";
    }

    // TODO
    public synchronized int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return -1;
    }

    // TODO
    public synchronized int queryCarsPrice(int id, String location) throws RemoteException {
        return -1
    }

    // TODO
    public int synchronized queryRoomsPrice(int id, String location) throws RemoteException {
        return -1;
    }

    //TODO
    public synchronized boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException {
        return false;
    }

    // TODO
    public synchronized boolean reserveCar(int id, int customerID, String location) throws RemoteException {
        returm false;
    }

    // TODO
    public synchronized boolean reserveRoom(int id, int customerID, String location) throws RemoteException {
        return false;
    }

    // TODO
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        return false;
    }

    // TODO
    public String getName() throws RemoteException {
        return ""
    }
}