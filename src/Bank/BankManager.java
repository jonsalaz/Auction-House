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

    private HashMap<String, Long> accounts = new HashMap<>();
    /** funds held until auction won, (K: item id, V: TransactionAccounts) */
    private HashMap<String, Transaction> bidsInEscrow = new HashMap<>();
    private List<Integer> auctionHousePorts = new ArrayList<>();

    public BankManager() {
    }
    
    public void handleClientRequest(Socket clientSock) {

        try {
            DataInputStream inputStream = new DataInputStream(clientSock.getInputStream());
            String[] clientQuery = inputStream.readUTF().split(" ");

            String clientInstruction = clientQuery[0];

            switch (clientInstruction) {
                case("Register"): {
                    registerAccount(clientSock, clientQuery);
                    break;
                }
                case("GetAHs"): {
                    getAuctionHouses(clientSock);
                }
                case("Bid"): {
                    setAuctionBid(clientSock, clientQuery);
                    break;
                }
                case("Finalize"): {
                    finalizeAuction(clientSock, clientQuery);
                    break;
                }
                default: {
                    DataOutputStream out =
                            new DataOutputStream(clientSock.getOutputStream());

                    out.writeUTF("Invalid instruction received");
                    out.close();
                }
            }

            printRequest(clientQuery);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Query format: Register ClientType ClientId (Optional: ClientInitBalance) */
    private void registerAccount(Socket clientSock, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSock.getOutputStream());
        String clientType = query[1];
        String clientId = query[2];

        if (!accounts.containsKey(clientId)) {
            if (clientType.equals("AuctionHouse")) {
                System.out.println("Registering AH");
                accounts.put(clientId, 0L);
                auctionHousePorts.add(Integer.valueOf(clientId));
                outputStream.writeUTF("Registration successful");
            }
            else if (clientType.equals("Agent")) {
                Long initBalance= Long.valueOf(query[3]);
                accounts.put(clientId, initBalance);
                System.out.println("Registering Agent");
                System.out.println(clientSock.getLocalAddress() + " " + clientSock.getLocalPort());

                if (auctionHousePorts.size() > 0) {
                    System.out.println("Sending AH address to client");
                    outputStream.writeUTF("returnAH "+ getFormattedPorts());
                }
                else {
                    outputStream.writeUTF("No auction houses found");
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
    private void setAuctionBid(Socket clientSock, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSock.getOutputStream());
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
            outputStream.writeUTF("Bid accepted");
        }

        outputStream.close();
    }

    /** Query format: Finalize ItemId */
    private void finalizeAuction(Socket clientSock, String[] query) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSock.getOutputStream());
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

    private void getAuctionHouses(Socket clientSock) throws IOException {
        DataOutputStream outputStream =
                new DataOutputStream(clientSock.getOutputStream());

        if (auctionHousePorts.size() == 0) {
            outputStream.writeUTF("No auction houses found.");
        }
        else outputStream.writeUTF("returnAH " + getFormattedPorts());

        System.out.println("\nSending AH addresses to client\n");
        outputStream.close();
    }

    /** Utility function to return list of ports as dash seperated string */
    private String getFormattedPorts() {
        String portString = "";

        if (auctionHousePorts.size() <= 1) {
            for (Integer i : auctionHousePorts) portString += i;
        }
        else {
            for (Integer i : auctionHousePorts) {
                portString += i + "-";

            }
        }
        return portString;
    }

    public void printRequest(String[] req) {
        for (String s : req) System.out.print(s+ " ");
        System.out.println();
    }
}
