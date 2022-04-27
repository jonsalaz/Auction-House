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

        String SQL = "";

        try {
            SQL = dataFromClient.readUTF();
        } catch (Exception e) {}
        bankManager.printRequest(SQL);
        try {
            System.out.println("close client socket.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        InputStream inputStream;
        BufferedReader bufferedReader = null;
        DataOutputStream dataOutputStream = null;

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Client connected");
        } catch(Exception e) {
            e.printStackTrace();
        }
        String line;
        while (true) {
            try {
                line = bufferedReader.readLine();
                System.out.println(line + "111");

            } catch (Exception e) {
                //e.printStackTrace();
            }

        }

         */





    }



}
