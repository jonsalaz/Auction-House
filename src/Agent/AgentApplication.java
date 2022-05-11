/** Jonathan Salazar , Cyrus McCormick
 * AgentApplication: Main method for agent,
 *  responsible for parsing user input,
 *  registering agent account with bank,
 *  and initializing AH connections to handle
 *  user requests
 */

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
    private static String bankHost = "127.0.0.1";
    private static Integer bankPort = 1234;

    private static Scanner scanner = new Scanner(System.in);
    /** auctionHousePort, ServerConnection(socket, input & output streams) */
    private static HashMap<Integer, AHConnection> connectedAHs = new HashMap<>();

    public static void main(String[] args) {
        clientUsername = args[0];
        initBalance = args[1];

        if (args.length > 2) {
            bankHost = args[2];
            if (args.length == 4) bankPort = Integer.parseInt(args[3]);
        }

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
                    break;
                }
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }

        terminateAHConnections();
    }

    /** Upon creation of agent, register an account with the bank using
     * CLA specified agent username & initial balance */
    private static void registerWithBank() {
        try {
            Socket sockToBank = new Socket(bankHost, bankPort);
            DataOutputStream outToServer = new DataOutputStream(sockToBank.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(sockToBank.getInputStream());

            outToServer.writeUTF("Register Agent " + clientUsername + " " + initBalance);
            String response = inFromServer.readUTF();

            outToServer.close();
            inFromServer.close();
            sockToBank.close();

            if (response.equals("Invalid username")) {
                System.out.println(clientUsername + " is already registered with bank.");
                System.exit(1);
            }
            else {
                establishAHConnection(response);
                System.out.println("An account with username " + clientUsername
                        + " has been registered with the bank!\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Retrieve a list of AH ports from bank & establish AH connection */
    private static void getAuctionHousesFromBank() {
        try {
            Socket sockToBank = new Socket(bankHost, bankPort);
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
        String addressString = clientQuery[1];
        String[] addresses;

        if (addressString.contains("-"))  addresses = addressString.split("-");

        else {
            // if only one port exists
            addresses = new String[1];
            addresses[0] = addressString;
        }

        for (String strAdd : addresses) {

            String[] addPort = strAdd.split(":");
            String ahHost = addPort[0];
            Integer ahPort = Integer.valueOf(addPort[1]);
            if (!connectedAHs.containsKey(ahPort)) {
                try {
                    AHConnection newConnection = new AHConnection(ahHost, ahPort, bankHost, bankPort);
                    connectedAHs.put(ahPort, newConnection);
                    newConnection.run();
                    System.out.println("Established connection with Auction House #" + ahPort);
                } catch(Exception e){
                    System.out.println("exception");
                }

            }
        }
    }

    /** Query format: bid AHID ItemID bidAmount
     * When user submits bid via CL, ensure that bid is valid and if so let
     * AHConnection thread handle request */
    private static void submitBidToAH(String[] userQuery) {
        Integer auctionHouseId;
        String itemId;
        String bidAmount;
        try {
            auctionHouseId = Integer.valueOf(userQuery[1]);
            itemId = userQuery[2];
            bidAmount = userQuery[3];
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Please submit a bid in the valid format.");
            return;
        }
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
        }
        printUserCommands();
    }

    /** Utility function for providing user with list of CL commands */
    private static void printUserCommands() {
        System.out.println("User commands:");
        System.out.println("Refresh connection to auction houses - ah");
        System.out.println("List items for sale by auction houses - items");
        System.out.println("Bid on an item - bid auctionHouseId itemId bidAmount");
        System.out.println("Terminate program - quit");
    }

    /** Tell AHConnection to terminate sockets & data streams */
    private static void terminateAHConnections() {
        for (Map.Entry<Integer, AHConnection> e : connectedAHs.entrySet()) {
            AHConnection ahConnection = e.getValue();
            ahConnection.terminateConnection();
        }
    }
}
