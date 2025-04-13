package ranking;

import java.sql.Timestamp;

public class CombinedBoostStrategy implements RankingBoostStrategy {
    private final RankingBoostStrategy sizeStrategy = new SizeBoostStrategy();
    private final RankingBoostStrategy recencyStrategy = new RecencyBoostStrategy();

    @Override
    public float applyBoost(float baseScore, long fileSize, Timestamp timestamp) {
        float scoreWithSize = sizeStrategy.applyBoost(baseScore, fileSize, timestamp);
        return recencyStrategy.applyBoost(scoreWithSize, fileSize, timestamp);
    }
}
