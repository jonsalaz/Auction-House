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

    static void clientConnect() {

            try {

                while (true) {

                    ServerSocket serverSocket = new ServerSocket(1234);
                    System.out.println("Server waiting for connection");
                    Socket clientSocket = serverSocket.accept();
                    ClientManager clientManager = new ClientManager(clientSocket, bankManager);
                    clientManager.run();

                    serverSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

    }


    Socket socket = null;
}
