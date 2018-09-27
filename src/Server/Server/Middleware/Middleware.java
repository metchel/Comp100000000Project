package Middleware;

import Server.Interface.*;
import java.util.*;
import java.rmi.RemoteException;
import java.io.*;


public abstract class Middleware implements IResourceManager {

    static IResourceManager flight_resourceManager = null;
    static IResourceManager car_resourceManager = null;
    static IResourceManager hotel_resourceManager = null;
    protected String m_name = "";

    public Middleware(String p_name) {
        m_name = p_name;
    }

    // IMiddleware implements Runnable, must implement that here
    public void run() {

    }

    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        if (flight_resourceManager.addFlight(xid, flightNum, flightSeats, flightPrice)) {
            System.out.println("Flight added - MW");
        } else {
            System.out.println("Flight could not be added - MW");
        }
    }

    public boolean addCars(int xid, String location, int numCars, int price) throws RemoteException {
        if (car_resourceManager.addCars(xid, location, numCars, price)) {
            System.out.println("Cars added - MW");
        } else {
            System.out.println("Cars could not be added - MW");
        }
    }

    public boolean addRooms(int xid, String location, int count, int price) throws RemoteException {
        if (hotel_resourceManager.addRooms(xid, location, numRooms, price)) {
            System.out.println("Rooms added");
        } else {
            System.out.println("Rooms could not be added");
        }
    }

    public int newCustomer(int xid) throws RemoteException {
    }
    public boolean newCustomer(int xid, int cid) throws RemoteException {
    }
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
    }
    public boolean deleteCars(int id, String location) throws RemoteException {
    }
    public boolean deleteRooms(int id, String location) throws RemoteException {
    }
    public boolean deleteCustomer(int id, int customerID) throws RemoteException {
    }
    public int queryFlight(int id, int flightNumber) throws RemoteException {
    }
    public int queryCars(int id, String location) throws RemoteException {
    }
    public int queryRooms(int id, String location) throws RemoteException {
    }
    public String queryCustomerInfo(int id, int customerID) throws RemoteException {
    }
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException{
    }
    public int queryCarsPrice(int id, String location) throws RemoteException{
    }
    public int queryRoomsPrice(int id, String location) throws RemoteException{
    }
    public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException {
    }
    public boolean reserveCar(int id, int customerID, String location) throws RemoteException {
    }
    public boolean reserveRoom(int id, int customerID, String location) throws RemoteException {
    }
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
    }
    public String getName() throws RemoteException{
    }
    // TODO
    public synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}