/** Jonathan Salazar , Cyrus McCormick
 * AuctionHouseApplication: Main class for AH,
 * responsible for getting port from user & registering account
 * for AH with bank, then listens for incoming
 * client requests from new agent
 */

package AuctionHouse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AuctionHouseApplication {
    public static void main(String[] args) {
        Socket bank = null;
        int port = -1;

        // Register with the bank and request a port.
        try {
            bank = new Socket("127.0.0.1", 1234);
            /** port = BankRegistration(bank); */
            port = BankRegistration(bank);
        } catch (Exception e) {
            System.out.println("Bank does not exist.");
            System.exit(1);
        }

        AHManager manager = new AHManager();

        /** Accepts incoming client connections from agents */
        ServerSocket server;
        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for a connection");
                Socket client = server.accept();
                System.out.println("Client accepted!");
                Thread thread = new Thread(new AHClientManager(client, bank, manager, port));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Prompts user for port & if port not already in use by another AH,
     * register an account with bank where accountID is == port */
    private static int BankRegistration(Socket bank) {
        int port = -1;
        DataOutputStream out;
        DataInputStream in;
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Please Input Desired Port Number");
            port = scanner.nextInt();

            try {
                // Request registration with the bank.
                out = new DataOutputStream(bank.getOutputStream());
                in = new DataInputStream(bank.getInputStream());

                /** Action ClientType ClientId */
                out.writeUTF("Register AuctionHouse " + port);

                // Check if Bank Approves this port number.
                if(in.readUTF().equals("Registration successful")) {
                    System.out.println("Registration successful");
                    return port;
                }
                else {
                    System.out.print("Port already in use, ");
                    bank = new Socket("127.0.0.1", 1234);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
