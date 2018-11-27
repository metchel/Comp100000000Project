package Server.ResourceManager;

import java.util.Map;
import java.util.*;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import Server.ResourceManager.TransactionLog;

public class ShadowManager {
    public static final String MASTER = "master";
    public static final String VERSION_A = "versionA";
    public static final String VERSION_B = "versionB";
    public static final String ERRORS = "SomethingsWrong";
    public static final Map DEFAULTMAP;
    static {
        DEFAULTMAP = new HashMap<>();
        DEFAULTMAP.put(VERSION_B,0);
    }


    public TransactionLog masterRecord;
    public TransactionLog versionA;
    public TransactionLog versionB;
    public String name;

    public ShadowManager(String rmname){
        this.masterRecord = new TransactionLog(MASTER+rmname);
        try {
            this.masterRecord.writeToLog(DEFAULTMAP);
        } catch (Exception e){
            System.out.println("SM Constructor");
            e.printStackTrace();
        }
        this.name = rmname;
        this.versionA = new TransactionLog(VERSION_A+rmname);
        this.versionB = new TransactionLog(VERSION_B+rmname);
    }

    public Map loadMasterRecord() throws IOException, ClassNotFoundException {
        return this.masterRecord.readFromLog();
    }

    public boolean writeToStorage(Map currentState, int xid) throws IOException, ClassNotFoundException{
        String locToWrite = this.getUnusedLocation();

        Map<String,Integer> mrmap = new HashMap<> ();
        mrmap.put(locToWrite,xid);

        if (locToWrite.equals(VERSION_A)){
            this.versionA.writeToLog(currentState);

        }
        else if (locToWrite.equals(VERSION_B)){
            this.versionB.writeToLog(currentState);
        }
        else {
            return false;
        }

        this.masterRecord.writeToLog(mrmap);

        return true;

    }

    public Map loadFromStorage() throws IOException, ClassNotFoundException {
        String lastCommit = this.getLastCommitLocation();
        if (lastCommit.equals(VERSION_A)){
            return this.versionA.readFromLog();
        }
        else if (lastCommit.equals(VERSION_B)){
            return this.versionB.readFromLog();
        }
        else {
            return Collections.emptyMap();
        }
    }


    public String getUnusedLocation() throws IOException, ClassNotFoundException{
        Map mr = this.loadMasterRecord();
        if (mr.size() != 1){
            System.out.println("This doesn't seem to be the master record");
            return ERRORS;
        }
        if (mr.containsKey(VERSION_A)){
            return VERSION_B;
        }
        else if(mr.containsKey(VERSION_B)){
            return VERSION_A;
        }
        else {
            System.out.println("Somethings wrong with master record");
            return ERRORS;
        }
    }

    public String getLastCommitLocation() throws IOException, ClassNotFoundException {
        Map mr = this.loadMasterRecord();
        if (mr.size() != 1){
            System.out.println("This doesn't seem to be the master record");
            return ERRORS;
        }
        if (mr.containsKey(VERSION_A)){
            return VERSION_A;
        }
        else if(mr.containsKey(VERSION_B)){
            return VERSION_B;
        }
        else {
            System.out.println("Somethings wrong with master record");
            return ERRORS;
        }

    }



}