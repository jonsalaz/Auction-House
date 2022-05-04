package AuctionHouse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionHouseApplication {
    public static void main(String[] args) {
        // TODO temp init port for testing
        int port = 1423;
        Socket bank = null;

        // Register with the bank and request a port.
        try {
            bank = new Socket("127.0.0.1", 1234);
            /** port = BankRegistration(bank); */
            BankRegistration(bank, port);
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
                Thread thread = new Thread(new AHClientManager(client, bank));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int BankRegistration(Socket bank, int port) {
        // int port = 1352;
        DataOutputStream out;
        DataInputStream in;
        try {
            // Request registration with the bank.
            out = new DataOutputStream(bank.getOutputStream());
            // TODO may want to send whole AH serverSocket to bank and parse port in bank for id? idk
            /** Action ClientType ClientId */
            out.writeUTF("Register AuctionHouse " + port);

            // Receive Port from Bank
            /** in = new DataInputStream(bank.getInputStream());
            port = in.readInt();*/

            // Close streams once port is received.
            /** in.close(); */
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return port;
    }
}
