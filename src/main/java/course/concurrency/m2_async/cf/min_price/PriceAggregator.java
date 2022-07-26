package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        ExecutorService executorService = new ForkJoinPool();
        double result = Double.NaN;
        try {
            result = CompletableFuture.supplyAsync(() -> shopIds.stream()
                    .map(shopId -> getMinPrice(itemId, shopId, executorService))
                    .filter(Objects::nonNull)
                    .min(Double::compareTo)
                    .orElse(Double.NaN))
                .get();
        } catch (Exception e) {
            System.out.printf("Error while getting price for item: %d. Message: %s%n", itemId, e.getMessage());
        } finally {
            executorService.shutdown();
        }
        return result;
    }

    private Double getMinPrice(long itemId, Long shopId, ExecutorService executorService) {
        try {
            return executorService.submit(() -> priceRetriever.getPrice(itemId, shopId)).get(10, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.printf("Error while getting price for item: %d and shopId: %d. Exception class: %s%n", itemId, shopId, e.getClass());
            return null;
        }
    }
}
