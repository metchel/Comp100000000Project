import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;

public class TransactionLog {
    private final File file;
    private final String fileName;
    private final String PREFIX = "logfiles/";
    private final String SUFFIX = ".log";

    public TransactionLog(String fileName) {
        this.fileName = fileName;
        this.file = new File(PREFIX 
            + fileName 
            + SUFFIX)
            .mkdir();
    }

    public writeToLog(Map data) throws IOException {
        final String path = this.getFullPath();

        final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.write(data);
        out.close();
    }

    public readFromLog() {
        final String path = this.getFullPath();

        final ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
        final Map map = (Map)input.readObject();
    }

    private String getFullPath() {
        return file.getAbsolutePath();
    }
}