package Bank;

import java.util.HashMap;

public class BankManager {

    // testing
    private int totalRequestsMade = 0;

    private HashMap<String, Double> accounts = new HashMap<>();

    public BankManager() {
    }


    public void handleClientRequest(String req) {

        String[] lineArr = req.split(" ");
        String clientType = lineArr[0];
        String clientId = lineArr[1];

        if (!accounts.containsKey(clientId)) {
            if (clientType.equals("auction")) accounts.put(clientId, 0.0);
            // TODO agent account balanced should be specified on agent creation
            else accounts.put(clientId, 0.0);
        }

        printRequest(req);
    }

    public void printRequest(String req) {
        System.out.println(req + " | total reqs by clients: " + totalRequestsMade++);
    }
}
