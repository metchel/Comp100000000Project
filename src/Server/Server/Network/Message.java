package Server.Network;

import java.util.Vector;

public class Message {
    private String method;
    private Vector<Object> arguments;

    public Message(String method, Vector<Object> arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    public String toString() {
        StringBuilder strB = new StringBuilder();
        int argCount = 0;
        for(Object arg: this.arguments) {
            strB.append("arg" + argCount + "=" + arg.toString() + " ");
            argCount++;
        }

        return strB.toString();
    }
}