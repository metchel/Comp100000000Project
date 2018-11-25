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
    private final String PREFIX = "logfiles/";
    private final String SUFFIX = ".log";

    public TransactionLog(String fileName) {
        this.fileName = fileName;
        this.file = new File(PREFIX 
            + fileName 
            + SUFFIX);
    }

    public void writeToLog(Map data) throws IOException {
        final String path = this.getFullPath();

        final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.writeObject(data);
        out.close();
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