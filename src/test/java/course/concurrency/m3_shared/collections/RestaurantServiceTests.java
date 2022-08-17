package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import course.concurrency.exams.auction.ExecutionStatistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

@Disabled
public class RestaurantServiceTests {

    private static final int TEST_COUNT = 10;
    private static final ExecutionStatistics stat = new ExecutionStatistics();

    private static final int iterations = 4_000_000;
    private static final int poolSize = Runtime.getRuntime().availableProcessors() * 2;

    private ExecutorService executor;
    private RestaurantService service;

    @BeforeEach
    public void setup() {
        executor = Executors.newFixedThreadPool(poolSize);
        service = new RestaurantService();
    }

    @AfterAll
    public static void printStat() {
        stat.printStatistics();
    }

    @RepeatedTest(TEST_COUNT)
    public void test() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        executor.submit(() -> {
            try {
                latch.await();
            } catch (InterruptedException ignored) {
            }

            for (int it = 0; it < iterations; it++) {
                service.getByName("A");
            }
        });

        long start = System.currentTimeMillis();
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();

        assertEquals(Set.of("A - " + iterations), service.printStat());
        stat.addData("service", end - start);
    }

    @RepeatedTest(TEST_COUNT)
    public void testDifferentNames() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger marcellisCount = new AtomicInteger(0);
        AtomicInteger burgerKingCount = new AtomicInteger(0);

        executor.submit(() -> {
            try {
                latch.await();
            } catch (InterruptedException ignored) {}
            getNameByCount("Marcellis", marcellisCount, 5);
        });

        executor.submit(() -> {
            try {
                latch.await();
            } catch (InterruptedException ignored) {}
            getNameByCount("Burger King", burgerKingCount, 7);
        });

        long start = System.currentTimeMillis();
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();

        assertTrue(service.printStat().contains("Marcellis - 5"));
        assertTrue(service.printStat().contains("Burger King - 7"));
        stat.addData("service",end - start);
    }

    private void getNameByCount(String name, AtomicInteger counter, int max) {
        int actual;
        do {
            actual = counter.getAndIncrement();
            service.getByName(name);
        } while (!counter.compareAndSet(max, actual));
    }
}
