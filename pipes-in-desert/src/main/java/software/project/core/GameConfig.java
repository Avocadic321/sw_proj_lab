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

    // Setters with validation
    public void setGoalScore(int score) {
        if (goalScore >= MIN_GOAL_SCORE &&  goalScore <= MAX_GOAL_SCORE) {
            System.out.printf("[GameConfig] setGoalScore(%d)%n", score);
            this.goalScore = score;
        } else {
            System.out.println("[GameConfig] Goal score must be >= " + MIN_GOAL_SCORE);
        }
    }

    public void setTurnDurationSeconds(int seconds) {
        if (turnDurationSeconds >= MIN_TURN_DURATION && seconds <= MAX_TURN_DURATION) {
            System.out.printf("[GameConfig] setTurnDurationSeconds(%d)%n", seconds);
            this.turnDurationSeconds = seconds;
        } else {
            System.out.println("[GameConfig] Turn duration must be >= " + MIN_TURN_DURATION);
        }
    }

    public void setRealTimeScoring(boolean enabled) {
        System.out.printf("[GameConfig] setRealTimeScoring(%b)%n", enabled);
        this.realTimeScoring = enabled;
    }

    public void setNumberOfPlayers(int count) {
        if (count >= MIN_PLAYERS && count <= MAX_PLAYERS) {
            System.out.printf("[GameConfig] setNumberOfPlayers(%d)%n", count);
            this.numberOfPlayers = count;
        } else {
            System.out.println("[GameConfig] Number of players must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
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
