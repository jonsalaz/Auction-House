package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Container class to hold transactions of bids while auction ongoing */
class Transaction{
    private String agentId;
    private String auctionId;
    private Long bidAmount;

    public Transaction(String agentId, String auctionId, Long bidAmount) {
        this.agentId = agentId;
        this.auctionId = auctionId;
        this.bidAmount = bidAmount;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public Long getBidAmount() {
        return bidAmount;
    }
}

public class BankManager {

    // testing
    private int totalRequestsMade = 0;

    private HashMap<String, Long> accounts = new HashMap<>();
    /** funds held until auction won, (K: item id, V: TransactionAccounts) */
    private HashMap<String, Transaction> bidsInEscrow = new HashMap<>();
    private List<Integer> auctionHousePorts = new ArrayList<>();

    public BankManager() {
    }
    
    public void handleClientRequest(Socket clientSocket) {

        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            String[] clientQuery = inputStream.readUTF().split(" ");

            String clientInstruction = clientQuery[0];

            if (clientInstruction.equals("Register")) {
                registerAccount(clientSocket, clientQuery);
            }

            if (clientInstruction.equals("Bid")) {
                setAuctionBid(clientSocket, clientQuery);
            }

            if (clientInstruction.equals("Finalize")) {
                finalizeAuction(clientSocket, clientQuery);
            }

            printRequest(clientQuery);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAccount(Socket clientSocket, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        String clientType = query[1];
        String clientId = query[2];

        if (!accounts.containsKey(clientId)) {
            if (clientType.equals("AuctionHouse")) {
                System.out.println("Registering AH");
                accounts.put(clientId, Long.valueOf(0));
                auctionHousePorts.add(Integer.valueOf(clientId));
                outputStream.writeUTF("Registration successful");
            }
            else {
                // TODO agent account balanced should be specified on agent creation
                accounts.put(clientId, Long.valueOf(0));
                System.out.println("Registering Agent");
                System.out.println(clientSocket.getLocalAddress() + " " + clientSocket.getLocalPort());

                if (auctionHousePorts.size() > 0) {
                    System.out.println("Sending AH address to client");
                    outputStream.writeUTF("Register AuctionHouse " + auctionHousePorts);
                }

            }
        }

        // if account already exists w/ id
        else {
            if (clientType.equals("AuctionHouse")) {
                outputStream.writeUTF("Invalid port");
            }
            else outputStream.writeUTF("Invalid username");
        }

        outputStream.close();
    }

    /** Query format: Bid AgentId AuctionHouseId ItemId BidAmount */
    private void setAuctionBid(Socket clientSocket, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        String agentId = query[1];
        String auctionId = query[2];
        String itemId = query[3];
        Long bidAmount = Long.valueOf(query[4]);

        if (accounts.containsKey(agentId) && accounts.containsKey(auctionId)) {

            /** if bid is more than agent has in account, reject */
            if (bidAmount > accounts.get(agentId)) {
                outputStream.writeUTF("Bid rejected");
                outputStream.close();
                return;
            }

            /** if previous bids exist on item, must free funds */
            if (bidsInEscrow.containsKey(itemId)) {
                freeFunds(itemId);
            }

            /** hold agent's funds & update agents account balance */
            Transaction newBid = new Transaction(agentId, auctionId, bidAmount);
            bidsInEscrow.put(itemId, newBid);

            Long agentBalance = accounts.get(agentId);
            agentBalance -= bidAmount;
            accounts.put(agentId, agentBalance);
        }

        outputStream.close();
    }

    /** Query format: Finalize ItemId */
    private void finalizeAuction(Socket clientSocket, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        String itemId = query[1];

        if (!bidsInEscrow.containsKey(itemId)) {
            outputStream.writeUTF("Invalid auction item ID");
            outputStream.close();
            return;
        }

        /** Update auction house's account balance */
        Transaction winningTransaction = bidsInEscrow.get(itemId);
        String auctionHouseId = winningTransaction.getAuctionId();
        Long auctionHouseBalance = accounts.get(auctionHouseId);
        Long updatedBalance = auctionHouseBalance + winningTransaction.getBidAmount();
        accounts.put(auctionHouseId, updatedBalance);
        outputStream.writeUTF("Auction House balance updated: $" + auctionHouseBalance
                + " -> $" + updatedBalance);


        outputStream.close();
    }

    /** Helper function to release funds from prior bid when higher bid accepted */
    private void freeFunds(String itemId) {
        Transaction prevTransaction = bidsInEscrow.get(itemId);
        Long updatedAgentBalance = accounts.get(prevTransaction.getAgentId());
        updatedAgentBalance += accounts.get(prevTransaction.getBidAmount());
        accounts.put(prevTransaction.getAgentId(), updatedAgentBalance);
    }

    public void printRequest(String[] req) {
        for (String s : req) System.out.print(s + " ");
        System.out.println(" | total reqs by clients: " + totalRequestsMade++);
    }
}
