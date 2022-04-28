package AuctionHouse;

import java.net.Socket;

public class ClientManager implements Runnable {
    Socket client;

    public ClientManager(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //TODO client login and request information.
    }
}
