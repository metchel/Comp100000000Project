import java.util.List;
import java.util.ArrayList;

public class RequestMessage {


    String sName;
    String cName;
    String xid;
    List<String> argums;

    private RequestMessage(){

    }

    public String toString() {
        return this.sName + " " + this.cName + " " + this.xid + " " + this.argums.toString();
    }
        
    public static class RequestBuilder {
        private String sName;
        private String cName;
        private String xid;
        private List<String> argums = new ArrayList<String>();

        public RequestBuilder withCommand(String command){

            if (command.startsWith("Add")){
                this.cName = "ADD";
                switch(command.substring(3,4)) {
                    case("Cu"): this.sName = "CUSTOMERS"; break;
                    case("Ca"): this.sName = "CARS"; break;
                    case("Fl"): this.sName = "FLIGHTS"; break;
                    case("Ro"): this.sName = "ROOMS"; break;
                }
            }
            else if (command.startsWith("Delete")){
                this.cName = "DELETE";
                switch(command.substring(6,7)) {
                    case("Cu"): this.sName = "CUSTOMERS"; break;
                    case("Ca"): this.sName = "CARS"; break;
                    case("Fl"): this.sName = "FLIGHTS"; break;
                    case("Ro"): this.sName = "ROOMS"; break;
                }
            }
            else if (command.startsWith("Query")) {
                if (command.length() < 13) {
                    this.cName = "QUERY";
                    switch(command.substring(5,6)) {
                        case("Cu"): this.sName = "CUSTOMERS"; break;
                        case("Ca"): this.sName = "CARS"; break;
                        case("Fl"): this.sName = "FLIGHTS"; break;
                        case("Ro"): this.sName = "ROOMS"; break;
                    }
                }
                else {
                    this.cName = "QUERYPRICE";
                    switch(command.substring(5,6)) {
                        case("Ca"): this.sName = "CARS"; break;
                        case("Fl"): this.sName = "FLIGHTS"; break;
                        case("Ro"): this.sName = "ROOMS"; break;
                    }
                }

            }
            else if (command.startsWith("Reserve")){

            }
            else if (command.startsWith("Bundle")){

            }

            this.sName = sName;
            this.cName = cName;
            return this;
        }
        public RequestBuilder inXId(String xid){
            this.xid = xid;
            return this;
        }
        public RequestBuilder withArgument(String argum){
            this.argums.add(argum);
            return this;
        }

        public RequestMessage build() {
            RequestMessage rm = new RequestMessage();
            rm.sName = this.sName;
            rm.cName = this.cName;
            rm.xid = this.xid;
            rm.argums = this.argums;
            return rm;
        }
    }
}