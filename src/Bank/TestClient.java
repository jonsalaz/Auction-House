package Bank;

import java.io.DataOutputStream;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        String clientType = "agent";
        String clientId = "client1";
        String clientRequest;
        Socket socket;

        {
            int i = 0;
            while (true) {
                try {
                    socket = new Socket("127.0.0.1", 1234);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    /** Message to bank server:
                     * Formatted as "clientType clientId clientRequest"
                     * where client request can be "createAccount, blockFunds, transferFunds, ... etc "*/
                    clientRequest = "request#" + i++;
                    out.writeUTF(clientType + " " + clientId + " " + clientRequest);

                    Thread.sleep(2000);

                } catch (Exception e) {
                    // Exit when bank closes
                    System.exit(1);
                }
            }
        }

    }

}
