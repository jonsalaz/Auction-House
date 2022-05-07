package Bank;

import java.net.Socket;

public class BankClientManager implements Runnable{

    private Socket socket;
    private BankManager bankManager;

    BankClientManager(Socket socket, BankManager bankManager) {
        this.socket = socket;
        this.bankManager = bankManager;
    }

    @Override
    public void run() {
        try {

            bankManager.handleClientRequest(socket);
            //System.out.println("close client socket.");
            //socket.close();
        } catch (Exception e) {}

    }



}
