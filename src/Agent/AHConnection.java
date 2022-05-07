package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AHConnection implements Runnable {
    private Integer port;
    private String type;
    private Socket socketToServer;
    private DataInputStream inFromServer;
    private DataOutputStream outToServer;
    private Queue queue = new ConcurrentLinkedQueue<String>();

    public AHConnection(String localHost, Integer port) {
        this.port = port;
        this.type = type;

        try {
            socketToServer = new Socket(localHost, port);
            outToServer = new DataOutputStream(socketToServer.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

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

                if (type.equals("Bank")) {
                    queue.add("GetAHs");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        queue.add(message);
        System.out.println(message);
    }
}
