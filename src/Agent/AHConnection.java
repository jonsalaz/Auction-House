package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AHConnection implements Runnable {
    private String localHost;
    private Integer port;
    private Socket sockToAH;
    private Queue queue = new ConcurrentLinkedQueue<String>();

    public AHConnection(String localHost, Integer port) {
        this.localHost = localHost;
        this.port = port;

        try {
            sockToAH = new Socket(localHost, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            //DataOutputStream outToAH = new DataOutputStream(sockToAH.getOutputStream());
            //DataInputStream inFromAH = new DataInputStream(sockToAH.getInputStream());

            while (!queue.isEmpty()) {
                String requesttoAH = queue.poll().toString();
                System.out.println(requesttoAH);

                //outToAH.writeUTF(requesttoAH);

                //String responseFromAH = inFromAH.readUTF();
                //if (responseFromAH.equals())



            }




        } catch (Exception e){
            e.printStackTrace();
        }




        /*
        try {
            inFromServer = new DataInputStream(socketToServer.getInputStream());
            String response = "";
            while (true) {
                while (!queue.isEmpty()) {
                    String request = queue.poll().toString();
                    System.out.println(request);
                    outToServer.writeUTF(request);
                    response = inFromServer.readUTF();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }

    public void sendMessage(String message) {
        queue.add(message);
        System.out.println(message);
    }
}
