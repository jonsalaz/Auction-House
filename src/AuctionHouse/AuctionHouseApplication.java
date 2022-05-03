package AuctionHouse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionHouseApplication {
    public static void main(String[] args) {
        int port = -1;

        // Register with the bank and request a port.
        try {
            Socket bank = new Socket("127.0.0.1", 1234);
            port = BankRegistration(bank);
        } catch (Exception e) {
            System.out.println("Bank does not exist.");
            System.exit(1);
        }

        ServerSocket server;
        try {
            server = new ServerSocket(port);
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

    private static int BankRegistration(Socket bank) {
        int port = -1;
        DataOutputStream out;
        DataInputStream in;
        try {
            out = new DataOutputStream(bank.getOutputStream());
            out.writeUTF("Register AuctionHouse");
            in = new DataInputStream(bank.getInputStream());
            while(port == -1) {
                port = in.readInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return port;
    }
}
