package AuctionHouse;

public class Auction {
    private int id;
    private String name;
    private int value;
    public Auction(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getCurrentBid() {
        return value;
    }

    public String getName() {
        return name;
    }
}
