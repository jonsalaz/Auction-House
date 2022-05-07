package AuctionHouse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class AHClientManager implements Runnable {
    private Socket client;
    private Socket bank;
    private AHManager manager;
    private int port;
    public AHClientManager(Socket client, Socket bank, AHManager manager, int port) {
        this.port = port;
        this.client = client;
        this.bank = bank;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out;
            String request = "";
            while(!request.toLowerCase(Locale.ROOT).equals("quit")) {
                request = in.readUTF().toLowerCase();
                String[] details = request.split(" ");
                request = details[0];
                switch(request) {
                    //Request for listed items.
                    case("items"):
                        out = new DataOutputStream(client.getOutputStream());
                        manager.provideListings(out);
                        break;
                    //Request to place bid.
                    case("bid"):
                        // bid user itemID amount
                        out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
                        manager.bidHandler(out, details[1], Integer.parseInt(details[2]), Long.parseLong(details[3]), port);
                        break;
                    //Request to disconnect from auction house.
                    case("quit"):
                        break;
                    default:
                        System.out.println("Invalid Request");
                        break;
                }
            }
            client.close();
        } catch (Exception e) {}
    }
}
