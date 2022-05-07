package AuctionHouse;

import java.io.*;
import java.net.Socket;
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
            //After a 30 second delay, the auctions are checked for finalization.
            if(System.currentTimeMillis() - auction.getStartTime() > 30*1000) {
                System.out.println("Closing auction #: " + auction.getId());
                closeAuction(auction);
                return;
            }
        }
    }

    private void replaceAuction() {
        Random random = new Random();
        Auction replacement = options.get(random.nextInt(options.size()));
        for(Auction auction: auctions) {
            if(auction.getId() == replacement.getId()) {
                replaceAuction();
                return;
            }
        }
//        System.out.println("Auction Replaced");
        auctions.add(replacement);
    }

    private void closeAuction(Auction auction) {
        auctions.remove(auction);
        auction.finish();
//        System.out.println("Replacing auction.");
        replaceAuction();
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
        for(int i = 0; i < 3; i++) {
            replaceAuction();
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

    // bid user itemID amount
    public void bidHandler(DataOutputStream out, String user, int id, long amount, int port) {
        //Search for auction with matching ID.
        for(Auction auction: auctions) {
            if(auction.getId() == id) {
                //Check if bid amount is higher than current auctions bid amount.
                if(auction.getCurrentBid() < amount) {
                    try {
                        //Connect with bank to make a request.
                        Socket bank = new Socket("127.0.0.1", 1234);
                        DataOutputStream outBank = new DataOutputStream(bank.getOutputStream());
                        DataInputStream inBank = new DataInputStream(bank.getInputStream());
                        // Bid request provided to bank.
                        outBank.writeUTF("Bid " + user + " " + port + " " + id + " " + amount);
                        //Bank response provided to user.
                        String status = inBank.readUTF();
                        if(status.equalsIgnoreCase("Bid accepted")) {
                            auction.setCurrentBid(amount);
                            auction.setWinner(out);
                        }
                        out.writeUTF(status);
                    } catch (IOException e) {
                        System.out.println("Cannot connect to bank");
                        try {
                            out.writeUTF("Bid rejected");
                        } catch (Exception ignored) {
                            System.out.println("Connection to user lost");
                        }
                    }
                }
                // If requested bid amount is lower than current bid amount, reject bid.
                else {
                    try {
                        out.writeUTF("Bid rejected");
                    } catch (Exception ignored) {
                        System.out.println("Connection to user lost.");
                    }
                }
                //Once correct auction is found. Break.
                break;
            }
        }
    }
}
