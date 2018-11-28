package Server.Network;

import Server.Common.Command;

import java.util.Map;
import java.io.Serializable;
import java.util.HashMap;

public class RequestData implements Serializable {
    private static final long serialVersionUID = 214867365778553221L;
    private Integer xId;
    private Command command;
    private Map<String, Object> commandArgs;
    
    public RequestData() {}

    public RequestData addXId(int xId) {
        this.xId = xId;
        return this;
    }

    public RequestData addCommand(Command command) {
        this.command = command;
        return this;
    }

    public RequestData addArgument(String key, Object val) {
        if (this.commandArgs == null) {
            this.commandArgs = new HashMap<String, Object>();
        }

        this.commandArgs.put(key, val);
        return this;
    }

    public Integer getXId() {
        return this.xId;
    }

    public Command getCommand() {
        return this.command;
    }

    public Map getCommandArgs() {
        return this.commandArgs;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        final String delim = "|";
        if (this.commandArgs == null || this.commandArgs.isEmpty()) {
            return this.xId.toString() + delim
            + this.command.toString();
        } else {
            return this.xId.toString() + delim
            + this.command.toString() + delim
            + this.commandArgs.toString();
        }
    }
}