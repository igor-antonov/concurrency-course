package course.concurrency.m2_async.cf.min_price;

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
        return shopIds.stream()
            .map(shopId -> getMinPrice(itemId, shopId))
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .min(Double::compareTo)
            .orElse(Double.NaN);
    }

    private CompletableFuture<Double> getMinPrice(long itemId, Long shopId) {
        return CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
            .exceptionally((e) -> null)
            .completeOnTimeout(null, 500, TimeUnit.MICROSECONDS);
    }
}
