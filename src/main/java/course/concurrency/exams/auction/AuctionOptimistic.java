package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicLong;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicLong price = new AtomicLong(0L);

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid;

    public boolean propose(Bid bid) {
        if (bid.price > price.get()) {
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
            price.set(bid.price);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
