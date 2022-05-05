package AuctionHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AHManager {
    ArrayList<Auction> auctions;

    public AHManager() {
        this.auctions = initializeAuctions();
    }

    private ArrayList<Auction> initializeAuctions() {
        ArrayList<Auction> initialAuctions = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("/items");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        int itemCount = 0;
        String[] splitLine = null;
        String line = null;
        try {
            line = reader.readLine();
            while(line != null && itemCount < 3) {
                splitLine = line.split(" ");
                Auction auction = new Auction(Integer.parseInt(splitLine[0]),
                                                splitLine[1],
                                                Integer.parseInt(splitLine[2]));
                auctions.add(auction);
                itemCount++;
            }
        } catch (IOException e) {}
        return initialAuctions;
    }
}
