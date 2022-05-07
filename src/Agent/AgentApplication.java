package Agent;

import java.util.Scanner;

public class AgentApplication {
    public static void main(String[] args) {

        String clientUsername = args[0];
        String initBalance = args[1];
        new Agent(clientUsername, initBalance);

    }
}
