package assignment3.events;

import java.time.Instant;

public class OrderPlaced implements Event{
    public enum Type { BUY, SELL }
    public final String orderId;
    public final String userId;
    public final Type type;
    public final int quantity;
    public final double price;
    private final Instant timestamp;

    public OrderPlaced(String orderId, String userId, Type type, int quantity, double price) {
        this.orderId = orderId;
        this.userId = userId;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
