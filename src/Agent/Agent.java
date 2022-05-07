package Agent;

import java.util.HashMap;

public class Agent {
    private String clientId;
    private ServerConnection bankConnection;
    /** auctionHousePort, ServerConnection(socket, input & output streams) */
    private HashMap <Integer, ServerConnection> connectedAHs;
    private String localHost = "127.0.0.1";

    public Agent(String clientId, String initBalance) {
        this.clientId = clientId;
        this.connectedAHs = new HashMap<>();
        initAgent(initBalance);
    }

    private void initAgent(String initBalance) {

        try {

            bankConnection = new ServerConnection(localHost, 1234, this);
            bankConnection.sendMessage("Register Agent " + clientId + " " + initBalance);
            bankConnection.run();

        } catch (Exception e) {}
    }

    /** Query format: Add AuctionHouse ####-####-.... where (#### is an AH port) */
    private void establishAHConnection(String query) {
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
                    connectedAHs.put(ahPort, new ServerConnection(localHost, ahPort, this));
                } catch(Exception e){
                    System.out.println("exception");
                }

            }
        }

    }

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


        System.out.println(response);
    }


}
