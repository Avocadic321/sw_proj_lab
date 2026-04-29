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
    private static final boolean DEFAULT_RANDOM = false;

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
    private boolean random;

    /** Creates a new config with all default values. */
    public GameConfig() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
        this.random = DEFAULT_RANDOM;
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

    public boolean isRandom() {
        return random;
    }

    /**
     * Sets the winning score threshold.
     *
     * @param score must be between MIN_GOAL_SCORE and MAX_GOAL_SCORE
     */
    public void setGoalScore(int score) {
        if (score >= MIN_GOAL_SCORE && score <= MAX_GOAL_SCORE) {
            this.goalScore = score;
        } else {
            System.out.printf("[ERROR] SET_GOAL OUT_OF_RANGE [%d, %d]%n", MIN_GOAL_SCORE, MAX_GOAL_SCORE);
        }
    }

    /**
     * Sets how long each player's turn lasts.
     *
     * @param seconds must be between MIN_TURN_DURATION and MAX_TURN_DURATION
     */
    public void setTurnDurationSeconds(int seconds) {
        if (seconds >= MIN_TURN_DURATION && seconds <= MAX_TURN_DURATION) {
            this.turnDurationSeconds = seconds;
        } else {
            System.out.printf("[ERROR] SET_TURN_DURATION OUT_OF_RANGE [%d, %d]%n", MIN_TURN_DURATION, MAX_TURN_DURATION);
        }
    }

    /**
     * Enables or disables real-time scoring mode.
     *
     * @param enabled true = scores update every second, false = scores update after
     *                each full round
     */
    public void setRealTimeScoring(boolean enabled) {
        this.realTimeScoring = enabled;
    }

    /**
     * Sets the total number of players.
     *
     * @param count must be between MIN_PLAYERS and MAX_PLAYERS
     */
    public void setNumberOfPlayers(int count) {
        if (count >= MIN_PLAYERS && count <= MAX_PLAYERS) {
            this.numberOfPlayers = count;
        } else {
            System.out.printf("[ERROR] SET_PLAYERS OUT_OF_RANGE [%d, %d]%n",  MIN_PLAYERS, MAX_PLAYERS);
        }
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    /** Restores all settings to their default values. */
    public void resetToDefault() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
        this.random = DEFAULT_RANDOM;
    }

    /**
     * Returns a string representation of the configuration.
     *
     * @return formatted configuration string
     */
    @Override
    public String toString() {
        return String.format(
            "[STATE] GAME_CONFIG goalScore=%d turnDuration=%d realTimeScoring=%s numberOfPlayers=%d random=%s",
            goalScore,
            turnDurationSeconds,
            realTimeScoring ? "ON" : "OFF",
            numberOfPlayers,
            random ? "ON" : "OFF"
        );
    }
}
