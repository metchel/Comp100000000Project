package Server.Sockets;

import Server.Common.Trace;
import Server.Common.ResourceManager;
import Server.Network.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;

public class ClientWorker implements Runnable {
    final Socket client;
    final RequestHandler handler;

    public ClientWorker(Socket client, RequestHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    public void run() {
        String line;
        BufferedReader in = null;
        PrintWriter out = null;

        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {
            /**
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            **/

            ois = new ObjectInputStream(this.client.getInputStream());
            oos = new ObjectOutputStream(this.client.getOutputStream());
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ClientWorker::run failed on either in or out stream.");
            System.exit(-1);
        }

        while(true) {
            try {
                Response response = handler.handle((Request) ois.readObject());
                oos.writeObject(response);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ClientWorker::run failed on in or out stream.");
                System.exit(-1);
            }
        }
    }
}