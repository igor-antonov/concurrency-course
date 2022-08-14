package course.concurrency.exams.auction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final Lock lock = new ReentrantLock();
    private boolean isStop;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.isStop = false;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public boolean propose(Bid bid) {
        if (isCancelBid(bid, latestBid)) {
            return false;
        }
        lock.lock();
        try {
            if (isCancelBid(bid, latestBid)) {
                return false;
            }
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
        } finally {
            lock.unlock();
        }
        return true;
    }

    private boolean isCancelBid(Bid bid, Bid latestBid) {
        return isStop || bid.price <= latestBid.price;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        isStop = true;
        return latestBid;
    }
}
