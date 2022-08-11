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
        Bid newVal;

        do {
            expected = latestBid.get();
            newVal = bid.price > expected.price ? bid : expected;
            if (newVal == expected) {
                return false;
            }
        } while (latestBid.compareAndSet(expected, newVal));

        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
