package Bank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        String clientType = "agent";
        String clientId = "client1";
        Socket socketToServer;

        {
            while (true) {
                try {
                    socketToServer = new Socket("127.0.0.1", 1234);
                    DataOutputStream out = new DataOutputStream(socketToServer.getOutputStream());
                    DataInputStream in = new DataInputStream(socketToServer.getInputStream());

                    /** Message to bank server:
                     * Formatted as "clientType clientId clientRequest"
                     * where client request can be "createAccount, blockFunds, transferFunds, ... etc "*/
                    out.writeUTF("Register " + clientType + " " + clientId);
                    System.out.println("Wrote to bank");

                    System.out.println("AH ports: " + in.readUTF());

                    Thread.sleep(2000);

                    in.close();
                    out.close();

                } catch (Exception e) {
                    /** Catch end of file error which occurs when no AHs initialized yet */
                    if (!e.getClass().getSimpleName().equals("EOFException")) {
                        e.printStackTrace();
                    }

                    // Exit when bank closes
                    System.exit(1);
                }
            }
        }

    }

}
