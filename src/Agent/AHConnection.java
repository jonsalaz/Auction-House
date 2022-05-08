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
    private DataOutputStream outToAH;
    private DataInputStream inFromAH;

    public AHConnection(String localHost, Integer port) {
        this.localHost = localHost;
        this.port = port;

        try {
            sockToAH = new Socket(localHost, port);
            this.outToAH = new DataOutputStream(sockToAH.getOutputStream());
            this.inFromAH = new DataInputStream(sockToAH.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!queue.isEmpty()) {
                String requestToAH = queue.poll().toString();
                outToAH.writeUTF(requestToAH);
                handleResponses(inFromAH, requestToAH);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        queue.add(message);
    }

    private void handleResponses(DataInputStream inFromAH, String request) throws IOException {
        String[] query = request.split(" ");
        String instruction = query[0];
        String response = inFromAH.readUTF();

        switch (instruction) {
            case("items"): {
                itemsResponse(response);
                break;
            }
            case("bid"): {
                bidResponse(response, inFromAH);
                break;
            }
        }
    }

    private void bidResponse(String response, DataInputStream inFromAH) throws IOException {
        if (response.equalsIgnoreCase("Bid rejected")) {
            System.out.println("Insufficient balance in account to place bid.");
        }
        else if (response.equalsIgnoreCase("Bid accepted")) {
            System.out.println(response);
            response = inFromAH.readUTF().toLowerCase();
            String[] splitResponse = response.split(" ");

            if(response.contains("outbid")) {
                System.out.println("You were outbid on item #" + splitResponse[1]);
            }
            else if (response.contains("win")){
                finalizeAuction(splitResponse[1]);
                System.out.println("Auction for item #" + splitResponse[1] + " won.");
            }

        }
    }

    /** Utility function to print AH items   */
    private void itemsResponse(String response) {
        System.out.println("----------------------");
        System.out.println("Auctions for AH ID: " + port);
        System.out.print(response);
        System.out.println("----------------------\n");
    }

    /** Parameters: itemID is used as ID of transaction by bank
     * When notified that an auction w/ agent bid has ended,
     *  tell bank to finalize transaction and transfer funds from agent to AH */
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

    /** Close data streams and port when agent submits quit command to CL */
    public void terminateConnection() {
        try {
            inFromAH.close();
            outToAH.close();
            sockToAH.close();
        } catch (Exception e){}
    }
}
