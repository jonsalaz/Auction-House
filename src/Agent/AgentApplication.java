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
        System.out.println("An account with username " + clientUsername
                + " has been registered with the bank!");
        printUserCommands();

        String userInput = "";
        while (!userInput.equals("quit")) {
            userInput = scanner.nextLine();

            switch (userInput) {
                case("GetAHs"): {
                    getAuctionHousesFromBank();
                    break;
                }
                case("bid"): {
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
            System.out.println(response);

            outToServer.close();
            inFromServer.close();
            sockToBank.close();

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

            /** If auction houses are found*/
            if (response.contains("returnAH")) {
                establishAHConnection(response);
            }
            /** No auction houses exist */
            else {
                System.out.println(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /** Query format: Add AuctionHouse ####-####-.... where (#### is an AH port) */
    private static void establishAHConnection(String query) {
        String[] clientQuery = query.split(" ");
        String portString = clientQuery[1];
        String[] ahPorts;

        if (portString.contains("-"))  ahPorts = portString.split("-");
        else {
            /** if only one port exists */
            ahPorts = new String[1];
            ahPorts[0] = portString;
        }

        for (String strPort : ahPorts) {
            Integer ahPort = Integer.valueOf(strPort);
            if (!connectedAHs.containsKey(ahPort)) {
                try {
                    connectedAHs.put(ahPort,
                            new AHConnection(localHost, ahPort));
                    System.out.println("Established connection with Auction House #" + ahPort);
                } catch(Exception e){
                    System.out.println("exception");
                }

            }
        }

    }

    /** Utility function for providing user with list of CL commands */
    private static void printUserCommands() {
        System.out.println("\nUser commands:");
        System.out.println("Return list of active auction houses: getAHs");
        System.out.println("Bid on an item: bid auctionHouseId itemId bidAmount\n");
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
