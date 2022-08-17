package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RestaurantService {

    private final AtomicInteger counter;
    private final ConcurrentHashMap<String, Integer> stat;
    private final Restaurant mockRestaurant;

    public RestaurantService() {
        this.stat = new ConcurrentHashMap();
        this.counter = new AtomicInteger(0);
        this.mockRestaurant = new Restaurant("A");
    }

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return mockRestaurant;
    }

    public void addToStat(String restaurantName) {
        stat.put(restaurantName, counter.incrementAndGet());
    }

    public Set<String> printStat() {
        return stat.entrySet()
            .stream()
            .map((e) -> getStat(e.getKey(), e.getValue()))
            .collect(Collectors.toSet());
    }

    private String getStat(String name, Integer i) {
        return String.format("%s - %s", name, i);
    }
}
