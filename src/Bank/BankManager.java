package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BankManager {

    // testing
    private int totalRequestsMade = 0;

    private HashMap<String, Double> accounts = new HashMap<>();
    private List<Integer> auctionHousePorts = new ArrayList<>();

    public BankManager() {
    }
    
    public void handleClientRequest(Socket clientSocket) {

        try {

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            String[] clientQuery = inputStream.readUTF().split(" ");
            //System.out.println(clientQuery[0] + " " + clientQuery[0].isEmpty());

            String clientInstruction = clientQuery[0];
            String clientType = clientQuery[1];
            System.out.println(clientType);
            String clientId = clientQuery[2];

            if (clientInstruction.equals("Register")) {
                registerAccount(clientSocket, clientType, clientId);
            }

            printRequest(clientQuery);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registerAccount(Socket clientSocket, String clientType, String clientId) throws IOException {


        if (!accounts.containsKey(clientId)) {
            if (clientType.equals("AuctionHouse")) {
                System.out.println("Registering AH");
                accounts.put(clientId, 0.0);
                auctionHousePorts.add(Integer.valueOf(clientId));
            }
            else {
                // TODO agent account balanced should be specified on agent creation
                accounts.put(clientId, 0.0);
                System.out.println("Registering Agent");
                System.out.println(clientSocket.getLocalAddress() + " " + clientSocket.getLocalPort());

                if (auctionHousePorts.size() > 0) {
                    System.out.println("Sending AH address to client");
                    DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                    outputStream.writeUTF("Register AuctionHouse " + auctionHousePorts);
                    outputStream.close();
                }

            }
        }



    }

    public void printRequest(String[] req) {
        for (String s : req) System.out.print(s + " ");
        System.out.println(" | total reqs by clients: " + totalRequestsMade++);
    }
}
