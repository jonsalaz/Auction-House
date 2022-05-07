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
            DataOutputStream outToAH = new DataOutputStream(sockToAH.getOutputStream());
            DataInputStream inFromAH = new DataInputStream(sockToAH.getInputStream());

            while (!queue.isEmpty()) {
                String requesttoAH = queue.poll().toString();

                outToAH.writeUTF(requesttoAH);
                //String responseFromAH = inFromAH.readUTF();
                handleResponses(inFromAH);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        queue.add(message);
    }

    private void handleResponses(DataInputStream inFromAH) throws IOException {
        String response = inFromAH.readUTF();
        System.out.println(response);


        if (response.equals("Bid rejected")) {
            System.out.println("Insufficient balance in account to place bid.");
        }
        else if (response.equals("Bid accepted")) {

            System.out.println(response);
            response = inFromAH.readUTF();

            if (response.contains("win")){
                System.out.println("Auction for ");
            }

        }

    }

    private void finalizeAuction(String itemId) {

        try {
            Socket sockToBank = new Socket("127.0.0.1", 1234);
            DataOutputStream outToBank = new DataOutputStream(sockToBank.getOutputStream());
            DataInputStream inFromBank = new DataInputStream(sockToBank.getInputStream());

            outToBank.writeUTF("Finalize " + itemId);
            System.out.println(inFromBank.readUTF());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
