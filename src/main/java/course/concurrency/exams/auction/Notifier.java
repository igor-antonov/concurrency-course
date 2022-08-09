package course.concurrency.exams.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Notifier {

    ExecutorService executorService = new ForkJoinPool(64);
    AtomicInteger integer = new AtomicInteger(0);

    public void sendOutdatedMessage(Bid bid) {
        executorService.submit(this::imitateSending);
    }

    private void imitateSending() {
        try {
            integer.incrementAndGet();
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
