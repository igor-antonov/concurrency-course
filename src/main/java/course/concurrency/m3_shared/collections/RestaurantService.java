package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RestaurantService {

    private final ConcurrentHashMap<String, AtomicInteger> stat;
    private final Restaurant mockRestaurant;

    public RestaurantService() {
        this.stat = new ConcurrentHashMap();
        this.mockRestaurant = new Restaurant("A");
    }

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return mockRestaurant;
    }

    public void addToStat(String restaurantName) {
        stat.computeIfAbsent(restaurantName, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public Set<String> printStat() {
        return stat.entrySet()
            .stream()
            .map((e) -> getStat(e.getKey(), e.getValue().get()))
            .collect(Collectors.toSet());
    }

    private String getStat(String name, Integer i) {
        return String.format("%s - %s", name, i);
    }
}
