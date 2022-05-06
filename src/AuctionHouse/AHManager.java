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
    private ArrayList<Auction> options;

    public AHManager() {
        initializeAuctions();
        this.auctionTimer = Executors.newSingleThreadScheduledExecutor();
        try {
            // this::finalizeAuctions is equivalent to
            // new Runnable {
            //      @Override
            //      public void run() {
            //          finalizeAuctions();
            //      }
            // }
            // OR
            // () -> finalizeAuctions()
            // This is easier and more readable than creating a new runnable object that only contains the logic for
            // closing an auction.
            auctionTimer.scheduleAtFixedRate(this::finalizeAuctions, 1, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finalizeAuctions() {
        System.out.println("Checking for finished auctions.");
        if(auctions.isEmpty()) return;
        for (Auction auction: auctions) {
            System.out.println("Checking auction #: " + auction.getId());
            //After a 30 second delay, the auctions are checked for finalization.
            if(System.currentTimeMillis() - auction.getStartTime() > 30*1000) {
                closeAuction(auction);
                replaceAuction();
                finalizeAuctions();
                return;
            }
        }
    }

    private void replaceAuction() {
        Random random = new Random();
        this.auctions.add(options.get(random.nextInt(options.size())));
    }

    private void closeAuction(Auction auction) {
        auctions.remove(auction);
        auction.finish();
    }

    private void initializeAuctions() {
        this.auctions = new ArrayList<>();
        this.options = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("items");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String[] splitLine = null;
        String line = null;
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
