package Bank;

import java.io.DataOutputStream;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        Socket socket;

        {
            int i = 0;
            while (true) {
                try {
                    socket = new Socket("127.0.0.1", 1234);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("Client 1 request #" + i++ + " | ");
                    Thread.sleep(2000);

                } catch (Exception e) {
                    // Exit when bank closes
                    System.exit(1);
                }
            }
        }

    }

}
