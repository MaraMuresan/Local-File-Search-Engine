package ranking;

import java.sql.Timestamp;

public class SizeBoostStrategy implements RankingBoostStrategy {
    @Override
    public float applyBoost(float baseScore, long fileSize, Timestamp timestamp) {
        float sizeBoost = fileSize / 1024f / 10f;  //(+0.1 / 10KB)
        return baseScore + sizeBoost;
    }
}
