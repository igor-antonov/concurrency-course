package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicReference<>(new Bid(0L, 0L, 0L));
    }

    public boolean propose(Bid bid) {
        Bid expected;

        do {
            expected = latestBid.get();
            if (bid.price <= expected.price) {
                return false;
            }
        } while (!latestBid.compareAndSet(expected, bid));

        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
