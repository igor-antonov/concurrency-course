package course.concurrency.m2_async.cf.min_price;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
        CompletableFuture<Double>[] pricesFutureArray = shopIds.stream()
            .map(shopId -> getMinPrice(itemId, shopId))
            .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(pricesFutureArray);

        return allFutures
            .thenApply(unused -> Arrays.stream(pricesFutureArray)
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .mapToDouble(value -> value)
                .min()
                .orElse(Double.NaN))
            .join();
    }

    private CompletableFuture<Double> getMinPrice(long itemId, Long shopId) {
        return CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
            .exceptionally((e) -> null)
            .completeOnTimeout(null, 2900, TimeUnit.MILLISECONDS);
    }
}
