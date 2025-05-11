package assignment3.events;

import java.time.Instant;

public class TradeExecuted implements Event{
    public final String buyOrderId;
    public final String sellOrderId;
    public final int quantity;
    public final double price;
    private final Instant timestamp;

    public TradeExecuted(String buyOrderId, String sellOrderId, int quantity, double price) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
