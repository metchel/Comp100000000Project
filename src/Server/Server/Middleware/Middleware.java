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
      return (flight_resourceManager.addFlight(xid, flightNum, flightSeats, flightPrice));
    }

    public boolean addCars(int xid, String location, int numCars, int price) throws RemoteException {
        return (car_resourceManager.addCars(xid, location, numCars, price));
    }

    public boolean addRooms(int xid, String location, int count, int price) throws RemoteException {
        return (hotel_resourceManager.addRooms(xid, location, count, price));
    }

    public int newCustomer(int xid) throws RemoteException {
    }

    public boolean newCustomer(int xid, int cid) throws RemoteException {
    }

    public boolean deleteFlight(int xid, int flightNum) throws RemoteException {
        return (flight_resourceManager.deleteFlight(xid, flightNum));
    }
    public boolean deleteCars(int xid, String location) throws RemoteException {
        return (car_resourceManager.deleteCars(xid, location));
    }
    public boolean deleteRooms(int xid, String location) throws RemoteException {
        return (hotel_resourceManager.deleteRooms(xid, location));
    }
    public boolean deleteCustomer(int xid, int customerID) throws RemoteException {
    }

    public int queryFlight(int xid, int flightNumber) throws RemoteException {
        return (flight_resourceManager.queryFlight(xid, flightNumber));
    }
    public int queryCars(int xid, String location) throws RemoteException {
        return (car_resourceManager.queryCars(xid, location));
    }
    public int queryRooms(int xid, String location) throws RemoteException {
        return (hotel_resourceManager.queryRooms(xid, location));
    }
    public String queryCustomerInfo(int xid, int customerID) throws RemoteException {
    }
    public int queryFlightPrice(int xid, int flightNumber) throws RemoteException{
        return (flight_resourceManager.queryFlightPrice(xid, flightNumber));
    }
    public int queryCarsPrice(int xid, String location) throws RemoteException{
        return (car_resourceManager.queryCarsPrice(xid, location));
    }
    public int queryRoomsPrice(int xid, String location) throws RemoteException{
        return (hotel_resourceManager.queryRoomsPrice(xid, location));
    }
    public boolean reserveFlight(int xid, int customerID, int flightNumber) throws RemoteException {
        return (flight_resourceManager.reserveFlight(xid, customerID, flightNumber));
    }
    public boolean reserveCar(int xid, int customerID, String location) throws RemoteException {
        return (car_resourceManager.reserveCar(xid, customerID, location));
    }
    public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException {
        return (hotel_resourceManager.reserveRoom(xid, customerID, location));
    }
    public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
    }
    public String getName() throws RemoteException{
        return m_name;
    }

    public synchronized void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort) {}

}