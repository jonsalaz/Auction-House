package AuctionHouse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Auction {
    private int id;
    private String name;
    private int value;
    private float currentBid;
    public Auction(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.currentBid = value;
    }

    /**
     * Getter funciton for auction ID.
     * @return The ID of the auction.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter function for the current bid.
     * @return The Current bid of the auction.
     */
    public int getCurrentBid() {
        return value;
    }

    /**
     * Getter function for the name of the item being auctioned.
     * @return The name of the item being auctioned.
     */
    public String getName() {
        return name;
    }
}
