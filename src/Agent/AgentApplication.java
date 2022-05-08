package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AgentApplication {
    private static String clientUsername;
    private static String initBalance;
    private static String localHost = "127.0.0.1";
    private static Scanner scanner = new Scanner(System.in);
    /** auctionHousePort, ServerConnection(socket, input & output streams) */
    private static HashMap<Integer, AHConnection> connectedAHs = new HashMap<>();

    public static void main(String[] args) {
        clientUsername = args[0];
        initBalance = args[1];

        registerWithBank();
        printUserCommands();

        String userInput = "";
        while (!userInput.equals("quit")) {
            System.out.print("User input: ");
            userInput = scanner.nextLine();
            System.out.println();

            String[] query = userInput.split(" ");
            String instruction = query[0];

            switch (instruction) {
                case ("ah"): {
                    getAuctionHousesFromBank();
                    break;
                }
                case ("bid"): {
                    submitBidToAH(query);
                    break;
                }
                case ("items"): {
                    getItemsFromAHs();
                }
                default:
                    break;
            }
        }

        terminateAHConnections();
    }

    /** Upon creation of agent, register an account with the bank using
     * CLA specified agent username & initial balance */
    private static void registerWithBank() {
        try {
            Socket sockToBank = new Socket("127.0.0.1", 1234);
            DataOutputStream outToServer = new DataOutputStream(sockToBank.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(sockToBank.getInputStream());

            outToServer.writeUTF("Register Agent " + clientUsername + " " + initBalance);
            String response = inFromServer.readUTF();

            outToServer.close();
            inFromServer.close();
            sockToBank.close();

            if (response.equals("Invalid username")) {
                System.out.println(clientUsername + " is already registered with bank.");
            }
            else {
                establishAHConnection(response);
                System.out.println("An account with username " + clientUsername
                        + " has been registered with the bank!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Retrieve a list of AH ports from bank & establish AH connection */
    private static void getAuctionHousesFromBank() {
        try {
            Socket sockToBank = new Socket("127.0.0.1", 1234);
            DataOutputStream outToServer = new DataOutputStream(sockToBank.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(sockToBank.getInputStream());

            outToServer.writeUTF("GetAHs");
            String response = inFromServer.readUTF();
            establishAHConnection(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Query format: Add AuctionHouse ####-####-.... where (#### is an AH port)
     * parse port string provided by bank & for e/a port, establish a new
     * connection from agent to AH by initializing AHConnection */
    private static void establishAHConnection(String query) {

        if (!query.contains("returnAH")) {
            System.out.println("No auction houses registered with bank.");
            return;
        }

        String[] clientQuery = query.split(" ");
        String portString = clientQuery[1];
        String[] ahPorts;

        if (portString.contains("-"))  ahPorts = portString.split("-");
        else {
            // if only one port exists
            ahPorts = new String[1];
            ahPorts[0] = portString;
        }

        for (String strPort : ahPorts) {
            Integer ahPort = Integer.valueOf(strPort);
            if (!connectedAHs.containsKey(ahPort)) {
                try {
                    AHConnection newConnection = new AHConnection(localHost, ahPort);
                    connectedAHs.put(ahPort, newConnection);
                    newConnection.run();
                    System.out.println("Established connection with Auction House #" + ahPort);
                } catch(Exception e){
                    System.out.println("exception");
                }

            }
        }
    }

    /** When user submits bid via CL, ensure that bid is valid and if so let
     * AHConnection thread handle request */
    private static void submitBidToAH(String[] userQuery) {
        Integer auctionHouseId = Integer.valueOf(userQuery[1]);
        String itemId = userQuery[2];
        String bidAmount = userQuery[3];

        AHConnection ah = connectedAHs.get(auctionHouseId);

        try {
            ah.sendMessage("bid " + clientUsername + " " + itemId + " " + bidAmount);
            ah.run();
        } catch (Exception e) {
            System.out.println("Bid submitted is invalid.");
        }

    }

    /** When user requests list of items from AH, let AHConnection thread
     * handle fetch */
    private static void getItemsFromAHs() {
        for (Map.Entry<Integer, AHConnection> e : connectedAHs.entrySet()) {
            AHConnection ah = e.getValue();
            ah.sendMessage("items");
            ah.run();
            printUserCommands();
        }
    }

    /** Utility function for providing user with list of CL commands */
    private static void printUserCommands() {
        System.out.println("\nUser commands:");
        System.out.println("Refresh connection to auction houses - ah");
        System.out.println("List items for sale by auction houses - items");
        System.out.println("Bid on an item - bid auctionHouseId itemId bidAmount");
        System.out.println("Terminate program - quit");
    }

    private static void terminateAHConnections() {
        for (Map.Entry<Integer, AHConnection> e : connectedAHs.entrySet()) {
            AHConnection ahConnection = e.getValue();
            ahConnection.terminateConnection();
        }
    }
}
