package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicLong;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicLong price = new AtomicLong(0L);
    private Bid latestBid;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new Bid(0L, 0L, 0L);
    }

    public boolean propose(Bid bid) {
        Long bidPrice;
        do {
            bidPrice = bid.price;
        } while (price.compareAndSet(bidPrice - latestBid.price, bidPrice));
        notifier.sendOutdatedMessage(latestBid);
        latestBid = bid;
        return true;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
