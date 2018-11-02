package Server.Network;

import Server.Common.Command;

import java.util.Map;

public class RequestData {
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

    public String toString() {
        final String delim = " ";
        return this.xId.toString() + delim
            + this.command.toString() + delim
            + this.commandArgs.toString();
    }
}