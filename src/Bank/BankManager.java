package Bank;

public class BankManager {

    // testing
    private int totalRequestsMade = 0;

    public BankManager() {
    }

    public void printRequest(String req) {
        System.out.println(req + "total reqs made: " + totalRequestsMade++);
    }
}
