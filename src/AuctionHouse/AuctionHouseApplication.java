package AuctionHouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionHouseApplication {
    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(1013);
            while (true) {
                System.out.println("Waiting for a connection");
                Socket client = server.accept();
                System.out.println("Client accepted!");
                Thread thread = new Thread(new ClientManager(client));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
