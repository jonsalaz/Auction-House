/** Jonathan Salazar , Cyrus McCormick
 * AuctionHouseApplication: Main class for AH,
 * responsible for getting port from user & registering account
 * for AH with bank, then listens for incoming
 * client requests from new agent
 */

package AuctionHouse;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AuctionHouseApplication {

    private static String host = "127.0.0.1";
    private static Integer bankPort = 1234;

    public static void main(String[] args) {

        if (args.length > 0) {
            host = args[0];
            if (args.length == 2) bankPort = Integer.parseInt(args[1]);
        }

        Socket bank = null;
        int ahPort = -1;

        // Register with the bank and request a port.
        try {
            bank = new Socket(host, bankPort);
            /** port = BankRegistration(bank); */
            ahPort = BankRegistration(bank);
        } catch (Exception e) {
            System.out.println("Bank does not exist.");
            System.exit(1);
        }

        AHManager manager = new AHManager();

        /** Accepts incoming client connections from agents */
        ServerSocket server;
        try {
            server = new ServerSocket(ahPort, 0, InetAddress.getLocalHost());
            while (true) {
                System.out.println("Waiting for a connection");
                Socket client = server.accept();
                System.out.println("Client accepted!");
                Thread thread = new Thread(new AHClientManager(client, bank, manager, ahPort));
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
        int ahPort = -1;
        DataOutputStream out;
        DataInputStream in;
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Please Input Desired Port Number");
            ahPort = scanner.nextInt();

            try {
                // Request registration with the bank.
                out = new DataOutputStream(bank.getOutputStream());
                in = new DataInputStream(bank.getInputStream());

                /** Action ClientType ClientId */
                out.writeUTF("Register AuctionHouse " + InetAddress.getLocalHost() + ":" +ahPort);

                // Check if Bank Approves this port number.
                if(in.readUTF().equals("Registration successful")) {
                    System.out.println("Registration successful");
                    return ahPort;
                }
                else {
                    System.out.print("Port already in use, ");
                    bank = new Socket(host, bankPort);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
