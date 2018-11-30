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
    public static final String STATUS = "status";
    public static final String ERRORS = "SomethingsWrong";
    public static final Map DEFAULTMAP;
    static {
        DEFAULTMAP = new HashMap<>();
        DEFAULTMAP.put(VERSION_B,0);
    }
    public static final Map EMPTYMAP = Collections.emptyMap();

    public TransactionLog masterRecord;
    public TransactionLog versionA;
    public TransactionLog versionB;
    public TransactionLog status;

    private String lastVersion = VERSION_B;
    public String name;

    public ShadowManager(String rmname){
        try {
            this.masterRecord = new TransactionLog(MASTER + rmname);
            //if file was just made, no existing master record.
            if (this.masterRecord.getBool()){
                this.masterRecord.writeToLog(DEFAULTMAP);
            }
            this.lastVersion = this.getLastCommitLocation();

        } catch (Exception e){
            System.out.println("SM Constructor failure");
            e.printStackTrace();
        }
        this.versionA = new TransactionLog(VERSION_A+rmname);
        this.versionB = new TransactionLog(VERSION_B+rmname);
        this.status = new TransactionLog(STATUS+rmname);
        this.name = rmname;
    }


    public Map loadMasterRecord() throws IOException, ClassNotFoundException {
        return this.masterRecord.readFromLog();
    }

    public Map loadStatus() throws IOException, ClassNotFoundException {
        if (this.status.getFileSize() == 0 ){
            return null;
        }
        return this.status.readFromLog();
    }

    public boolean writeToStorage(Map currentState, int xid) throws IOException, ClassNotFoundException{
        String locToWrite = this.getUnusedLocation();

        if (locToWrite.equals(VERSION_A)){
            this.versionA.writeToLog(currentState);
        }
        else if (locToWrite.equals(VERSION_B)){
            this.versionB.writeToLog(currentState);
        }
        else {
            return false;
        }
        return true;

    }

    public boolean writeToMasterRecord(int xid) throws IOException, ClassNotFoundException {
        String locToWrite = this.getUnusedLocation();

        Map<String,Integer> mrmap = new HashMap<>();
        mrmap.put(locToWrite,xid);

        try {
            this.masterRecord.writeToLog(mrmap);
            this.lastVersion = this.getLastCommitLocation();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean writeToStatus(Map mp){
        try {
            this.status.writeToLog(mp);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public Map loadFromOtherStorage() throws IOException, ClassNotFoundException {
        String lastCommit = this.getLastCommitLocation();

        if (this.versionA.getFileSize() == 0 && this.versionB.getFileSize() == 0){
            return null;
        }

        if (lastCommit.equals(VERSION_A)){
            System.out.println("Last commit is "+ VERSION_A);
            if(this.versionB.getFileSize() == 0){
                System.out.println("Other version is EMPTY, "+VERSION_B);
                return null;
            }
            System.out.println("Loading other version"+ VERSION_B);
            return this.versionB.readFromLog();
        }
        else if (lastCommit.equals(VERSION_B)){
            System.out.println("Last commit is "+ VERSION_B);
            if(this.versionB.getFileSize() == 0){
                System.out.println("Last commited version is EMPTY, "+VERSION_A);
                return null;
            }
            System.out.println("Loading other version"+ VERSION_A);
            return this.versionB.readFromLog();
        } else {
            System.out.println("Something really funky happened");
            return null;
        }
    }

    public Map loadFromStorage() throws IOException, ClassNotFoundException {
        String lastCommit = this.getLastCommitLocation();

        if (this.versionA.getFileSize() == 0 && this.versionB.getFileSize() == 0){
            return null;
        }

        if (lastCommit.equals(VERSION_A)){
            System.out.println("Last commit is "+ VERSION_A);
            if(this.versionA.getFileSize() == 0){
                System.out.println("Last commited version is EMPTY, "+VERSION_A);
                return null;
            }
            return this.versionA.readFromLog();
        }
        else if (lastCommit.equals(VERSION_B)){
            System.out.println("Last commit is "+ VERSION_B);
            if(this.versionB.getFileSize() == 0){
                System.out.println("Last commited version is EMPTY, "+VERSION_B);
                return null;
            }
            return this.versionB.readFromLog();
        } else {
            System.out.println("Something really funky happened");
            return null;
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
        System.out.println(mr.toString());
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