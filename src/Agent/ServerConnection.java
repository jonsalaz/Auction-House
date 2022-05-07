package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection implements Runnable {
    private Integer port;
    private Socket socketToServer;
    private DataInputStream inFromServer;
    private DataOutputStream outToServer;
    private Agent agent;
    private Queue queue = new ConcurrentLinkedQueue<String>();

    public ServerConnection(String localHost, Integer port, Agent agent) {
        this.port = port;
        this.agent = agent;

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
            while (!response.equalsIgnoreCase("quit")) {
                while (!queue.isEmpty()) {
                    outToServer.writeUTF(queue.poll().toString());
                    response = inFromServer.readUTF();
                    agent.handleServerResponse(response);
                }
                //request = inFromServer.readUTF();
                //System.out.println(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        /*

        while (true) {
            try {
                while (!queue.isEmpty()) {
                    String messageForServer = queue.poll().toString();
                    System.out.println(messageForServer);
                    out.writeUTF(messageForServer);
                }
                agent.receiveServerQuery(in.readUTF());
            } catch (IOException e) {
                live = false;
                e.printStackTrace();
            }
        }

         */

    }

    public void sendMessage(String message) {
        queue.add(message);
        System.out.println(message);
    }
}
