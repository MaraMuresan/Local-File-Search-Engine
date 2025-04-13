package ranking;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RecencyBoostStrategy implements RankingBoostStrategy{
    @Override
    public float applyBoost(float baseScore, long fileSize, Timestamp timestamp) {
        long daysAgo = ChronoUnit.DAYS.between(timestamp.toInstant(), Instant.now());
        float recencyBoost = 0;

        if (daysAgo <= 1) recencyBoost = 10;
        else if (daysAgo <= 7) recencyBoost = 5;
        else if (daysAgo <= 30) recencyBoost = 2;

        return baseScore + recencyBoost;
    }
}
