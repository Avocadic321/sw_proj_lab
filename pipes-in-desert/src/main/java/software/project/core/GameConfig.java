package software.project.core;

public class GameConfig {
    // Default values
    private static final int DEFAULT_GOAL_SCORE = 50;
    private static final int DEFAULT_TURN_DURATION = 120;
    private static final boolean DEFAULT_REAL_TIME_SCORING = false;
    private static final int DEFAULT_PLAYERS = 4;

    // Limits
    public static final int MIN_GOAL_SCORE = 10;
    public static final int MAX_GOAL_SCORE = 1000;
    public static final int MIN_TURN_DURATION = 10;
    public static final int MAX_TURN_DURATION = 300;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 8;

    // Configuration fields
    private int goalScore;
    private int turnDurationSeconds;
    private boolean realTimeScoring;
    private int numberOfPlayers;

    // Constructor with defaults
    public GameConfig() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
    }

    // Getters
    public int getGoalScore() {
        return goalScore;
    }
    public int getTurnDurationSeconds() {
        return turnDurationSeconds;
    }
    public boolean isRealTimeScoring() {
        return realTimeScoring;
    }
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    // Setters (skeleton print + assign)
    public void setGoalScore(int score) {
        System.out.printf("[GameConfig] setGoalScore(%d)%n", score);
        this.goalScore = score;
    }

    public void setTurnDurationSeconds(int seconds) {
        System.out.printf("[GameConfig] setTurnDurationSeconds(%d)%n", seconds);
        this.turnDurationSeconds = seconds;
    }

    public void setRealTimeScoring(boolean enabled) {
        System.out.printf("[GameConfig] setRealTimeScoring(%b)%n", enabled);
        this.realTimeScoring = enabled;
    }

    public void setNumberOfPlayers(int count) {
        System.out.printf("[GameConfig] setNumberOfPlayers(%d)%n", count);
        this.numberOfPlayers = count;
    }

    public void resetToDefault() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
    }

    @Override
    public String toString() {
        return String.format(
            "GameConfig {\n" +
                "  Goal Score: %d\n" +
                "  Turn Duration: %d seconds\n" +
                "  Real-time Scoring: %s\n" +
                "  Number of Players: %d\n" +
                "}",
            goalScore,
            turnDurationSeconds,
            realTimeScoring ? "enabled" : "disabled",
            numberOfPlayers
        );
    }
}
