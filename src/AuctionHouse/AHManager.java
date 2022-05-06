package AuctionHouse;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class AHManager {
    private ArrayList<Auction> auctions;

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

    public void provideListings(DataOutputStream out) {
        try {
            for (Auction auction: auctions) {
                out.writeUTF(auction.getId() + " " + auction.getName() + " " + auction.getCurrentBid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
