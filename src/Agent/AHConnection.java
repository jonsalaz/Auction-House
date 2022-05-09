/** Jonathan Salazar , Cyrus McCormick
 * AHConnection: Implements Runnable to handle
 * each of agent's auction house connection's
 * requests on seperate thread, by receiving message
 * stating request parameters from agent & sending
 * request to bank. Parses response from AH and outputs
 * to console.
 */

package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AHConnection implements Runnable {
    private String host;
    private Integer ahPort;
    private Integer bankPort;
    private Socket sockToAH;
    private Queue queue = new ConcurrentLinkedQueue<String>();
    private DataOutputStream outToAH;
    private DataInputStream inFromAH;

    public AHConnection(String host, Integer bankPort, Integer ahPort) {
        this.host = host;
        this.bankPort = bankPort;
        this.ahPort = ahPort;

        try {
            sockToAH = new Socket(host, ahPort);
            this.outToAH = new DataOutputStream(sockToAH.getOutputStream());
            this.inFromAH = new DataInputStream(sockToAH.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection refused.");
        }
    }

    /** Runnable task writes request to AH and hands
     *  response off to response handler function */
    @Override
    public void run() {
        try {
            while (!queue.isEmpty()) {
                String requestToAH = queue.poll().toString();
                outToAH.writeUTF(requestToAH);
                handleResponses(inFromAH, requestToAH);
            }

        } catch (Exception e){}
    }

    /** Allows agent to add request to AHConnection queue */
    public void sendMessage(String message) {
        queue.add(message);
    }

    /** Parses AH responses and hands off to appropriate function */
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

    /** Given response & input stream, decide if bid was accepted, if so,
     * wait until bid is either outbid or won */
    private void bidResponse(String response, DataInputStream inFromAH) throws IOException {
        if (response.equalsIgnoreCase("Bid rejected")) {
            System.out.println("Bid was rejected");
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
        // Do not want to import StringUtils
        System.out.println("------------------------AH ID: "
                + ahPort + "------------------------");
        System.out.printf("%-15s %-15s %-15s %-15s\n", "ItemId", "Item Name", "Current Bid", "Time remaining");
        System.out.print(response);
        System.out.println("------------------------------" +
                "-----------------------------\n");
    }

    /** Parameters: itemID is used as ID of transaction by bank
     * When notified that an auction w/ agent bid has ended,
     *  tell bank to finalize transaction and transfer funds from agent to AH */
    private void finalizeAuction(String itemId) {
        try {
            Socket sockToBank = new Socket(host, bankPort);
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
            System.out.println("Terminating connection to AH #" + ahPort);
            inFromAH.close();
            outToAH.close();
            sockToAH.close();
        } catch (Exception e){}
    }
}
