package AuctionHouse;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Auction {
    private int id;
    private String name;
    private int value;
    private float currentBid;
    private long startTime;
    private DataOutputStream winner;

    public Auction(int id, String name, int value, long startTime) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.currentBid = value;
        this.startTime = startTime;
    }

    /**
     * Getter function for start time.
     * @return Auction start time.
     */
    public long getStartTime() {
        return startTime;
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
    public float getCurrentBid() {
        return currentBid;
    }

    /**
     * Getter function for the name of the item being auctioned.
     * @return The name of the item being auctioned.
     */
    public String getName() {
        return name;
    }

    /**
     * Informs user of winning bid with format "win <id>" and finalizes the process.
     */
    public void finish() {
        if(winner != null){
            try {
                winner.writeUTF("win " + this.id);
            } catch (Exception ignored) {}
        }
    }

    public void setWinner(DataOutputStream winner) {
        this.winner = winner;
    }

    public void setCurrentBid(float currentBid) {
        this.currentBid = currentBid;
    }
}
