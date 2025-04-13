package ranking;

public class RankingBoostStrategyFactory {
    public static RankingBoostStrategy createStrategy(String type) {
        return switch (type.toLowerCase()) {
            case "size" -> new SizeBoostStrategy();
            case "recency" -> new RecencyBoostStrategy();
            case "both" -> new CombinedBoostStrategy();
            default -> new CombinedBoostStrategy();
        };
    }
}
