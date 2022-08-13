package course.concurrency.exams.auction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;
    private final Lock lock = new ReentrantLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public boolean propose(Bid bid) {
        if (bid.price <= latestBid.price) {
            return false;
        }
        lock.lock();
        try {
            if (bid.price <= latestBid.price) {
                return false;
            }
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
