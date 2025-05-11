package assignment3.events;

import java.time.Instant;

public class OrderCancelled implements Event{
    public final String orderId;
    private final Instant timestamp;

    public OrderCancelled(String orderId) {
        this.orderId = orderId;
        this.timestamp = Instant.now();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
