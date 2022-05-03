package AuctionHouse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Locale;

public class ClientManager implements Runnable {
    Socket client;

    public ClientManager(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //TODO client login and request information.
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            String request = "";
            while(!request.toLowerCase(Locale.ROOT).equals("quit")) {
                    request = in.readUTF();
                    System.out.println("The following request has been made: " + request);
            }

            client.close();
        } catch (Exception e) {}
    }
}
