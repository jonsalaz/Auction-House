package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
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
                case("getAHs"): {
                    getAuctionHousesFromBank();
                    break;
                }
                case("bid"): {
                    submitBidToAH(query);
                    break;
                }
                default: break;
            }
        }
    }

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
                System.out.println("An account with username " + clientUsername
                        + " has been registered with the bank!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    /** Query format: Add AuctionHouse ####-####-.... where (#### is an AH port) */
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

    private static void submitBidToAH(String[] userQuery) {
        String auctionHouseId = userQuery[1];
        String itemId = userQuery[2];
        String bidAmount = userQuery[3];

        AHConnection ah = connectedAHs.get(auctionHouseId);
        ah.sendMessage("bid " + clientUsername + " " + itemId + " " + bidAmount);
        ah.run();
    }

    /** Utility function for providing user with list of CL commands */
    private static void printUserCommands() {
        System.out.println("\nUser commands:");
        System.out.println("Return list of active auction houses - getAHs");
        System.out.println("Bid on an item - bid auctionHouseId itemId bidAmount");
    }

    /*
    public void handleServerResponse(String response) {

        String[] seperatedResponse = response.split(" ");
        String clientInstruction = seperatedResponse[0];

        switch(clientInstruction) {
            case("AddAH"): {
                establishAHConnection(response);
                break;
            }
            case(""): {

                break;
            }
            default:

        }
    }
     */

}
