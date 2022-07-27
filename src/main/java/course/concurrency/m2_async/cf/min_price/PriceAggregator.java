package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        List<CompletableFuture<Double>> pricesFutureList = shopIds.stream()
            .map(shopId -> getMinPrice(itemId, shopId))
            .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            pricesFutureList.toArray(new CompletableFuture[0])
        );

        return allFutures
            .thenApply(unused -> pricesFutureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()))
            .thenApply(doubles -> doubles.stream()
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(Double.NaN))
            .join();
    }

    private CompletableFuture<Double> getMinPrice(long itemId, Long shopId) {
        return CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
            .exceptionally((e) -> null)
            .completeOnTimeout(null, 2900, TimeUnit.MILLISECONDS);
    }
}
