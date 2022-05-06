package AuctionHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class AHManager {
    ArrayList<Auction> auctions;

    public AHManager() {
        this.auctions = initializeAuctions();
    }

    private ArrayList<Auction> initializeAuctions() {
        ArrayList<Auction> initialAuctions = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("/items");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String[] splitLine = null;
        String line = null;
        ArrayList<Auction> options = new ArrayList<>();
        try {
            line = reader.readLine();
            while(line != null) {
                splitLine = line.split(" ");
                Auction auction = new Auction(Integer.parseInt(splitLine[0]),
                                                splitLine[1],
                                                Integer.parseInt(splitLine[2]));
                options.add(auction);
                line = reader.readLine();
            }
        } catch (IOException ignored) {}
        Random random = new Random();
        for(int i = 0; i < 3; i++) {
            auctions.add(options.get(random.nextInt(options.size())));
        }

        return initialAuctions;
    }
}
