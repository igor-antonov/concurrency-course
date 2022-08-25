package course.concurrency.m3_shared.immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private final Map<Long, Order> currentOrders = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(0L);

    private long nextId() {
        return nextId.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = new Order(id, items);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        currentOrders.computeIfPresent(orderId, (id, order) -> order.withPaymentInfo(paymentInfo));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    public void setPacked(long orderId) {
        currentOrders.computeIfPresent(orderId, (id, order) -> order.withPacked(true));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    private void deliver(Order order) {
        if (!isDelivered(order.getId())) {
            currentOrders.computeIfPresent(order.getId(), (id, o) -> o.withStatus(Order.Status.DELIVERED));
        }
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
