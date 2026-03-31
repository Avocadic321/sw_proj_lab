package software.project.core;

/**
 * Stores all user-configurable settings for a game session.
 *
 * <p>
 * This class holds four key parameters that determine how a game plays:
 * </p>
 *
 * <ul>
 * <li><b>goalScore</b> - How many points a team needs to win</li>
 * <li><b>turnDurationSeconds</b> - How many seconds each player gets per
 * turn</li>
 * <li><b>realTimeScoring</b> - Whether scores update every second or only after
 * turns</li>
 * <li><b>numberOfPlayers</b> - Total players in the game, split between
 * Plumbers and Saboteurs</li>
 * </ul>
 *
 * <p>
 * All setters validate input against minimum/maximum limits. Invalid values
 * are rejected with an error message printed to console.
 * </p>
 */
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

    /** Creates a new config with all default values. */
    public GameConfig() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
    }

    /**
     * Returns the score required for a team to win.
     *
     * @return the winning score threshold
     */
    public int getGoalScore() {
        return goalScore;
    }

    /**
     * Returns the maximum duration of a single turn.
     *
     * @return turn length in seconds
     */
    public int getTurnDurationSeconds() {
        return turnDurationSeconds;
    }

    /**
     * Returns whether real-time scoring mode is enabled.
     *
     * @return {@code true} if scores update continuously during turns,
     *         {@code false} if scores update only after each full round
     */
    public boolean isRealTimeScoring() {
        return realTimeScoring;
    }

    /**
     * Returns the total number of players.
     *
     * @return player count
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Sets the winning score threshold.
     *
     * @param score must be between MIN_GOAL_SCORE and MAX_GOAL_SCORE
     */
    public void setGoalScore(int score) {
        if (goalScore >= MIN_GOAL_SCORE && goalScore <= MAX_GOAL_SCORE) {
            System.out.printf("[GameConfig] setGoalScore(%d)%n", score);
            this.goalScore = score;
        } else {
            System.out.println("[GameConfig] Goal score must be >= " + MIN_GOAL_SCORE);
        }
    }

    /**
     * Sets how long each player's turn lasts.
     *
     * @param seconds must be between MIN_TURN_DURATION and MAX_TURN_DURATION
     */
    public void setTurnDurationSeconds(int seconds) {
        if (turnDurationSeconds >= MIN_TURN_DURATION && seconds <= MAX_TURN_DURATION) {
            System.out.printf("[GameConfig] setTurnDurationSeconds(%d)%n", seconds);
            this.turnDurationSeconds = seconds;
        } else {
            System.out.println("[GameConfig] Turn duration must be >= " + MIN_TURN_DURATION);
        }
    }

    /**
     * Enables or disables real-time scoring mode.
     *
     * @param enabled true = scores update every second, false = scores update after
     *                each full round
     */
    public void setRealTimeScoring(boolean enabled) {
        System.out.printf("[GameConfig] setRealTimeScoring(%b)%n", enabled);
        this.realTimeScoring = enabled;
    }

    /**
     * Sets the total number of players.
     *
     * @param count must be between MIN_PLAYERS and MAX_PLAYERS
     */
    public void setNumberOfPlayers(int count) {
        if (count >= MIN_PLAYERS && count <= MAX_PLAYERS) {
            System.out.printf("[GameConfig] setNumberOfPlayers(%d)%n", count);
            this.numberOfPlayers = count;
        } else {
            System.out.println("[GameConfig] Number of players must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
    }

    /** Restores all settings to their default values. */
    public void resetToDefault() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
    }

    /**
     * Returns a string representation of the configuration.
     *
     * @return formatted configuration string
     */
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
                numberOfPlayers);
    }
}
