package Client;

import org.junit.Test;

import Server.Common.Command;

import static org.junit.Assert.*;

public class TCPCLientTest {

    private TCPClient client;

    public TCPCLientTest() {
        this.client = new TCPClient();
    }

    @Test
    public void testAddFlight() {
    
        final String testPacket = "AddFlight, 1, 2, 3, 4";
        final Vector<String> arguments = this.client.parse(testPacket);

        // Add a flight with flight number 2
        this.client.execute(Command.AddFlight, arguments);

        final String testQueryPacket = "QueryFlight, 1, 2"; 
        final Vector<String> queryArguments = this.client.parse(testQueryPacket);

        // Check that flight 2 now exists and has 3 seats available
        Assert.assertEquals(this.client.execute(Command.QueryFlight, queryArguments), "3");
    }

    @Test
    public void testAddCustomer() {

    }

    @Test
    public void testQueryFlight() {

    }
    
    @Test
    public void testQueryCustomer() {

    }

    @Test
    public void testDeleteFlight() {

    }

    @Test
    public void testBundle() {

    }

    public static void main (String[] args) {

        TCPCLientTest test = new TCPClientTest();

        test.testAddFlight();
    }
}