package Bank;

import java.io.*;
import java.net.Socket;

public class ClientSocketContainer implements Runnable{

    private Socket socket;

    ClientSocketContainer(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
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
                System.out.println(line);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }





    }



}
