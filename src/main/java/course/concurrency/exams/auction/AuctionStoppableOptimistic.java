package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicMarkableReference<Bid> latestBid;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicMarkableReference<>(new Bid(0L, 0L, 0L), false);
    }

    public boolean propose(Bid bid) {
        if (latestBid.isMarked()) {
            return false;
        }
        Bid expected;

        do {
            expected = latestBid.getReference();
            if (bid.price <= expected.price) {
                return false;
            }
        } while (!latestBid.compareAndSet(expected, bid, false, latestBid.isMarked()));

        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        latestBid.set(latestBid.getReference(), true);
        return getLatestBid();
    }
}
