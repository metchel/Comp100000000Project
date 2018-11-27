package Server.ResourceManager;

import java.util.Map;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class TransactionLog {
    private final File file;
    private final String fileName;
    private final String PREFIX = "Server/logfiles/";
    private final String SUFFIX = ".log";

    public TransactionLog(String fileName) {
        this.fileName = fileName;
        this.file = new File(PREFIX 
            + fileName 
            + SUFFIX);
        try {
            this.file.createNewFile();
        } catch (Exception e) {
            System.out.println(this.file.getPath());
            e.printStackTrace();
        }
    }

    public void writeToLog(Map data) throws IOException {
        final String path = this.getFullPath();

        final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.writeObject(data);
        out.close();
    }

    public String getFileName(){
        return this.fileName;
    }
    public int getFileSize(){
        return ((int) this.file.length());
    }

    public Map readFromLog() throws IOException, ClassNotFoundException{
        final String path = this.getFullPath();

        final ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
        final Map map = (Map)in.readObject();
        return map;
    }

    private String getFullPath() {
        return file.getAbsolutePath();
    }
}