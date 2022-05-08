/** Jonathan Salazar , Cyrus McCormick
 * BankClientManager: Unique instance for e/a
 * client which connects to bank, hands socket off
 * to bank manager to handle requests
 *  */

package Bank;

import java.net.Socket;

public class BankClientManager implements Runnable{

    private Socket socket;
    private BankManager bankManager;

    BankClientManager(Socket socket, BankManager bankManager) {
        this.socket = socket;
        this.bankManager = bankManager;
    }

    /** Hand client socket off to bank manager for request management */
    @Override
    public void run() {
        try {
            bankManager.handleClientRequest(socket);
        } catch (Exception e) {}

    }
}
