/** Jonathan Salazar , Cyrus McCormick
 * AHManager: Responsible for receiving agent requests
 * from AHClientManager, processing these requests
 * and responding to the corresponding AHConnection object.
 *
 */

package AuctionHouse;

import java.io.*;
import java.net.InetAddress;
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

    private String bankHost;
    private Integer bankPort;
    /** Creates initial auctions & responsibility of checking if
     * an auction has completed to separate thread, this allows
     * continuous condition checks on auction to decide if it's finished */
    public AHManager(String bankHost, Integer bankPort) {
        this.bankHost = bankHost;
        this.bankPort = bankPort;
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

    /** For each auction, check whether auction time has decayed & close auction if so */
    private void finalizeAuctions() {
        if(auctions.isEmpty()) return;
        for (Auction auction: auctions) {
            //After a 30 second delay, the auctions are checked for finalization.
            if(System.currentTimeMillis() - auction.getStartTime() > 30*1000) {
                closeAuction(auction);
                return;
            }
        }
    }

    /** When one auction expires, create another to replace it */
    private void replaceAuction() {
        Random random = new Random();
        Auction replacement = options.get(random.nextInt(options.size()));
        replacement.setStartTime(System.currentTimeMillis());
        for(Auction auction: auctions) {
            if(auction.getId() == replacement.getId()) {
                replaceAuction();
                return;
            }
        }
        auctions.add(replacement);
    }

    /** When one auction expires, create another to replace it */
    private void closeAuction(Auction auction) {
        auctions.remove(auction);
        auction.finish();
        replaceAuction();
    }

    /** Parses lines containing item parameters from items file,
     * initializes new auction object and adds it to options list */
    private void initializeAuctions() {
        this.auctions = new ArrayList<>();
        this.options = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("/AuctionHouse/Items");
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

    /** When an AHConnection requests a list of current auction items,
     * build a formatted string of current auctions to respond with */
    public void provideListings(DataOutputStream out) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (Auction auction: auctions) {
                if(auction.getStartTime() <= 0) {
                    continue;
                }
                // This is not ideal but string builder does not appear to have an appendFormat method
                String s = String.format("%-15s %-15s %-15s %-15s\n",
                        auction.getId(),
                        auction.getName(),
                        "$" + auction.getCurrentBid(),
                        (30*1000 - (System.currentTimeMillis() - auction.getStartTime()) ) / 1000 + "s"
                        );
                stringBuilder.append(s);
            }
            out.writeUTF(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Request format: bid user itemID amount
     *  Responsible for handling AHConnection bid request,
     *  for each auction check if match with requested ID, if so
     *  determine if bid is valid. If bid is valid communicate bid to bank,
     *  send first response to AHConnection that bid is accepted then
     *  second response stating auction was either won or outbid */
    public void bidHandler(DataOutputStream out, String user, int id, long amount, String address) {
        //Search for auction with matching ID.
        for(Auction auction: auctions) {
            if(auction.getId() == id) {
                //Check if bid amount is higher than current auctions bid amount.
                if(auction.getCurrentBid() < amount) {
                    try {
                        //Connect with bank to make a request.
                        Socket bank = new Socket(bankHost, bankPort);
                        DataOutputStream outBank = new DataOutputStream(bank.getOutputStream());
                        DataInputStream inBank = new DataInputStream(bank.getInputStream());
                        // Bid request provided to bank.
                        outBank.writeUTF("Bid " + user + " " + address + " " + id + " " + amount);
                        //Bank response provided to user.
                        System.out.println("GETTING BANK STATUS " + user);
                        String status = inBank.readUTF();
                        System.out.println("PRINTING BANK STATUS:" + status);
                        out.writeUTF(status);
                        if(status.equalsIgnoreCase("Bid accepted")) {
                            System.out.println("SETTING BID INFO");
                            auction.setCurrentBid(amount);
                            auction.setWinner(out);
                        }
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
                return;
            }
        }
        try {
            out.writeUTF("Bid rejected");
        } catch (IOException ignored) {}
    }
}
