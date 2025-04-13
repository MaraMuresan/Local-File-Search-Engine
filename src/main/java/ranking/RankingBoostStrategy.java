package ranking;

import java.sql.Timestamp;

public interface RankingBoostStrategy {
    float applyBoost(float baseScore, long fileSize, Timestamp timestamp);
}
