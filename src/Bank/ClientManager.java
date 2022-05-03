package Bank;

import java.io.*;
import java.net.Socket;

public class ClientManager implements Runnable{

    private Socket socket;
    private DataInputStream dataFromClient;
    private BankManager bankManager;

    ClientManager(Socket socket, BankManager bankManager) {
        this.socket = socket;
        this.bankManager = bankManager;
        try {
            dataFromClient = new DataInputStream(socket.getInputStream());
        } catch (Exception e){}
    }

    @Override
    public void run() {

        String clientQuery = "";

        try {
            clientQuery = dataFromClient.readUTF();
            bankManager.handleClientRequest(clientQuery);

            System.out.println("close client socket.");
            socket.close();
        } catch (Exception e) {}

    }



}
