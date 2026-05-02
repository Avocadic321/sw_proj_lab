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
    // Goal Score Constants
    public static final int DEFAULT_GOAL_SCORE = 1000;
    public static final int MIN_GOAL_SCORE = 10;
    public static final int MAX_GOAL_SCORE = 10000;

    // Turn Constants
    public static final int DEFAULT_TURN_DURATION = 15;
    public static final int MIN_TURN_DURATION = 10;
    public static final int MAX_TURN_DURATION = 300;
    public static final boolean DEFAULT_REAL_TIME_SCORING = false;

    // Random Events
    private static final boolean DEFAULT_RANDOM_EVENTS_ENABLED = false;

    // Number of Players Constants
    public static final int DEFAULT_PLAYERS = 4;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 8;

    /* ===== Game Elements Constants =====*/
    // Cistern
    public static final int CISTERN_DEFAULT_CAPACITY = 500;
    public static final int CISTERN_MAX_CAPACITY = 1000;

    // Pipe
    public static final int PIPE_DEFAULT_CAPACITY = 10;
    public static final int PIPE_MAX_CAPACITY = 20;
    public static final int PIPE_FLOW_PER_TICK = 5;

    // Pump Constants
    public static final int PUMP_TANK_CAPACITY = 5;
    public static final int PUMP_FLOW_PER_TICK = 5;

    // Spring
    public static final int SPRING_WATER_GENERATED_PER_TICK = 1;

    // Scoring Constants
    public static final int SCORE_PER_WATER_LEAKED = 1;
    public static final int SCORE_PER_WATER_STORED = 1;

    // Configuration fields
    private int goalScore;
    private int turnDurationSeconds;
    private boolean realTimeScoring;
    private int numberOfPlayers;
    private boolean randomEventsEnabled;

    /** Creates a new config with all default values. */
    public GameConfig() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
        this.randomEventsEnabled = DEFAULT_RANDOM_EVENTS_ENABLED;
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

    public boolean areRandomEventsEnabled() {
        return randomEventsEnabled;
    }

    /**
     * Sets the winning score threshold.
     *
     * @param score must be between MIN_GOAL_SCORE and MAX_GOAL_SCORE
     */
    public void setGoalScore(int score) {
        this.goalScore = score;
    }

    /**
     * Sets how long each player's turn lasts.
     *
     * @param seconds must be between MIN_TURN_DURATION and MAX_TURN_DURATION
     */
    public void setTurnDurationSeconds(int seconds) {
        this.turnDurationSeconds = seconds;
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

    public void setRandomEventsEnabled(boolean randomEventsEnabled) {
        this.randomEventsEnabled = randomEventsEnabled;
    }

    /** Restores all settings to their default values. */
    public void resetToDefault() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.numberOfPlayers = DEFAULT_PLAYERS;
        this.randomEventsEnabled = DEFAULT_RANDOM_EVENTS_ENABLED;
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
            randomEventsEnabled ? "ON" : "OFF"
        );
    }
}
