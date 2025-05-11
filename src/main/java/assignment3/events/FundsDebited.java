package assignment3.events;

import java.time.Instant;

public class FundsDebited implements Event{
    public final String userId;
    public final double amount;
    private final Instant timestamp;

    public FundsDebited(String userId, double amount) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
