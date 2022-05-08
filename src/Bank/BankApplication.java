/** Jonathan Salazar , Cyrus McCormick
 * BankApplication: Main method for bank,
 * responsible for listening to incoming connection
 * requests from clients (AHs, Agents)
 */

package Bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankApplication {

    private static BankManager bankManager;

    public static void main(String[] args) {
        bankManager = new BankManager();
        clientConnect();
    }

    /** Listen for client connections to bank server */
    static void clientConnect() {

            try {

                while (true) {

                    ServerSocket serverSocket = new ServerSocket(1234);
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
