package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicMarkableReference<Bid> latestBid;
    private AtomicBoolean isStop;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicMarkableReference<>(new Bid(0L, 0L, 0L), false);
        this.isStop = new AtomicBoolean(false);
    }

    public boolean propose(Bid bid) {
        if (isStop.get()) {
            return false;
        }
        Bid expected;

        do {
            expected = latestBid.getReference();
            if (bid.price <= expected.price) {
                return false;
            }
        } while (!latestBid.compareAndSet(expected, bid, false, isStop.get()));

        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        isStop.set(true);
        return getLatestBid();
    }
}
