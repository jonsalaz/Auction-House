package AuctionHouse;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AHManager {
    private ArrayList<Auction> auctions;
    private ScheduledExecutorService auctionTimer;

    public AHManager() {
        initializeAuctions();
        this.auctionTimer = Executors.newSingleThreadScheduledExecutor();
        auctionTimer.scheduleAtFixedRate(this::finalizeAuctions, 0,1, TimeUnit.SECONDS);
    }

    private void finalizeAuctions() {
        System.out.println("Checking for finished auctions.");
        for (Auction auction: auctions) {
            //After a 30 second delay, the auctions are checked for finalization.
            if(System.currentTimeMillis() - auction.getStartTime() > 30*1000) {
                closeAuction(auction);
            }
        }
    }

    private void closeAuction(Auction auction) {
        //TODO Close the auction and remove auction from active auctions list.
        System.out.println("Finalizing Auction");
        System.out.println(auction.getId() + " " + auction.getName() + " " + auction.getCurrentBid());
    }

    private void initializeAuctions() {
        this.auctions = new ArrayList<>();
        ArrayList<Auction> initialAuctions = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("items");
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
                                                Integer.parseInt(splitLine[2]),
                                                System.currentTimeMillis());
                options.add(auction);
                line = reader.readLine();
            }
        } catch (IOException ignored) {}
        Random random = new Random();
        for(int i = 0; i < 3; i++) {
            auctions.add(options.get(random.nextInt(options.size())));
        }
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
