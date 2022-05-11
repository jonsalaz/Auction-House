/** Jonathan Salazar , Cyrus McCormick
 * BankApplication: Main method for bank,
 * responsible for listening to incoming connection
 * requests from clients (AHs, Agents)
 */

package Bank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class BankApplication {

    private static BankManager bankManager;
    private static Integer bankPort = 1234;

    public static void main(String[] args) {
        bankManager = new BankManager();
        if (args.length == 1) bankPort = Integer.parseInt(args[0]);
        clientConnect();
    }

    /** Listen for client connections to bank server */
    static void clientConnect() {

            try {

                while (true) {

                    ServerSocket serverSocket = new ServerSocket(bankPort, 0, InetAddress.getLocalHost());
                    System.out.println("Bank started on: " + serverSocket.getInetAddress().getHostAddress());
                    System.out.println("\nServer waiting for connection");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("ACCEPTED CLIENT\n");
                    BankClientManager clientManager = new BankClientManager(clientSocket, bankManager);
                    Thread thread = new Thread(clientManager);
                    thread.start();

                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
