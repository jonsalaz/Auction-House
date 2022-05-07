package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/** Container class for auction house socket/streams */
class ConnectedAuctionHouse {
    private Integer port;
    private Socket socketToAH;
    private DataInputStream in;
    private DataOutputStream out;

    public ConnectedAuctionHouse(Integer port) throws IOException {
        this.port = port;
        this.socketToAH = new Socket("127.0.0.1", port);
        this.in = new DataInputStream(socketToAH.getInputStream());
        this.out = new DataOutputStream(socketToAH.getOutputStream());
    }

    public long getPort() {
        return port;
    }

    public Socket getSocketToAH() {
        return socketToAH;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }
}

public class Agent {
    private String clientId;
    private Socket socketToBank;
    private DataOutputStream outToBank;
    private DataInputStream inFromBank;
    /** auctionHousePort, Container(socket, input & output streams) */
    private HashMap <Integer, ConnectedAuctionHouse> connectedAHs;

    public Agent(String clientId, String initBalance) {
        this.clientId = clientId;
        this.connectedAHs = new HashMap<>();
        initAgent(initBalance);

        while (true) {

            try {
                System.out.println(inFromBank.readUTF());
            } catch (Exception e) {}
        }

    }

    private void initAgent(String initBalance) {

        try {
            socketToBank = new Socket("127.0.0.1", 1234);
            outToBank = new DataOutputStream(socketToBank.getOutputStream());
            inFromBank = new DataInputStream(socketToBank.getInputStream());

            outToBank.writeUTF("Register Agent " + clientId + " " + initBalance);
            System.out.println("Registering w/ bank");
            String registerResponse = inFromBank.readUTF();
            System.out.println("res: " + registerResponse);

            if (registerResponse.contains("Add AuctionHouse")) {
                establishAHConnection(registerResponse);
            }

        } catch (Exception e) {}
    }

    /** Query format: Add AuctionHouse ####-####-.... where (#### is an AH port) */
    private void establishAHConnection(String query) {
        String[] clientQuery = query.split(" ");
        String portString = clientQuery[2];
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
                    connectedAHs.put(ahPort, new ConnectedAuctionHouse(ahPort));
                } catch(Exception e){
                    System.out.println("exception");
                }

            }
        }

    }


}
