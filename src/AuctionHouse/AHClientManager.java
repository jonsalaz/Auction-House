package AuctionHouse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.Locale;

public class AHClientManager implements Runnable {
    Socket client;
    Socket bank;
    AHManager manager;
    public AHClientManager(Socket client, Socket bank, AHManager manager) {
        this.client = client;
        this.bank = bank;
        this.manager = manager;
    }

    @Override
    public void run() {
        //TODO client login and request information.
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            String request = "";
            while(!request.toLowerCase(Locale.ROOT).equals("quit")) {
                request = in.readUTF();
                String[] details = request.split(" ");
                request = details[0];
                switch(request) {
                    //Request for listed items.
                    case("items"):
                        provideListings();
                        break;
                    //Request to place bid.
                    case("bid"):
                        //TODO Bidding logic.
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

    private void provideListings() {
        try {
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            while (true) {
                //TODO Write a line to output for each item currently listed.
                // Item House ID, Item ID, description, minimum bid, current bid
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
