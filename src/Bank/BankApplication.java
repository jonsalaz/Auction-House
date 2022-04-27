package Bank;

import java.net.ServerSocket;
import java.net.Socket;

public class BankApplication {
    public static void main(String[] args) {
        clientConnect();

    }

    static void clientConnect() {
        int port = 1234;
        ServerSocket serverSocket;
        Socket socket;

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                socket = serverSocket.accept();
                new ClientSocketContainer(socket).run();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    Socket socket = null;
}
