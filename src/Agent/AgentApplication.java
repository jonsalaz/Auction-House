package Agent;

public class AgentApplication {
    public static void main(String[] args) {

        String clientUsername = args[0];
        String initBalance = args[1];
        Agent agent = new Agent(clientUsername, initBalance);

    }
}
