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
 * <li><b>plumberCount</b> - Total plumbers in the game</li>
 * <li><b>saboteurCount</b> - Total saboteurs in the game</li>
 * <li><b>harshness</b> - Frequency of random events per turn</li>
 * </ul>
 *
 * <p>
 * All setters validate input against minimum/maximum limits. Invalid values
 * are rejected with an error message printed to console.
 * </p>
 */
public class GameConfig {
    // Test Mode Flag
    private boolean testMode = false;

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
    private static final boolean DEFAULT_RANDOM_EVENTS_ENABLED = true;

    // Number of Players Constants (per team)
    public static final int DEFAULT_PLAYERS = 2;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 8;

    // Harshness
    public enum Harshness {
        LIGHT(5, 1),
        MEDIUM(3, 3),
        HEAVY(1, 5);

        private final int intervalTurnsForPumpBreak;
        private final int intervalTurnsForComponentProduction;

        Harshness(int intervalTurnsForPumpBreak, int intervalTurnsForComponentProduction) {
            this.intervalTurnsForPumpBreak = intervalTurnsForPumpBreak;
            this.intervalTurnsForComponentProduction = intervalTurnsForComponentProduction;
        }

        public int getIntervalTurnsForPumpBreak() {
            return intervalTurnsForPumpBreak;
        }

        public int getIntervalTurnsForComponentProduction() {
            return intervalTurnsForComponentProduction;
        }
    }

    // Plumber
    public static final int DEFAULT_INVENTORY_SIZE = 2;

    /* ===== Game Elements Constants ===== */
    // Cistern
    public static final int CISTERN_DEFAULT_CAPACITY = 500;
    public static final int CISTERN_MAX_CAPACITY = 1000;

    // Pipe
    public static final int PIPE_DEFAULT_CAPACITY = 10;
    public static final int PIPE_MAX_CAPACITY = 20;
    public static final int PIPE_MAX_FLOW_PER_TICK = 5;

    // Pump Constants
    public static final int PUMP_TANK_CAPACITY = 5;
    public static final int PUMP_MAX_FLOW_PER_TICK = 5;

    // Spring
    public static final int SPRING_WATER_GENERATED_PER_TICK = 1;

    // Scoring Constants
    public static final int SCORE_PER_WATER_LEAKED = 1;
    public static final int SCORE_PER_WATER_STORED = 1;

    // Configuration fields
    private int goalScore;
    private int turnDurationSeconds;
    private boolean realTimeScoring;
    private int plumberCount;
    private int saboteurCount;
    private boolean randomEventsEnabled;
    private Harshness harshness;

    /**
     * Creates a new config with all default values.
     */
    public GameConfig() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.plumberCount = DEFAULT_PLAYERS;
        this.saboteurCount = DEFAULT_PLAYERS;
        this.randomEventsEnabled = DEFAULT_RANDOM_EVENTS_ENABLED;
        this.harshness = Harshness.MEDIUM;
    }

    public boolean isTestMode() {
        return testMode;
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
     *         {@code false} if scores update only after each
     *         full round
     */
    public boolean isRealTimeScoring() {
        return realTimeScoring;
    }

    /**
     * Returns the total number of plumbers.
     *
     * @return plumber count
     */
    public int getPlumberCount() {
        return plumberCount;
    }

    /**
     * Returns the total number of saboteurs.
     *
     * @return saboteur count
     */
    public int getSaboteurCount() {
        return saboteurCount;
    }

    /**
     * Returns whether random events are enabled.
     *
     * @return true if random events are enabled, false otherwise
     */
    public boolean areRandomEventsEnabled() {
        return randomEventsEnabled;
    }

    /**
     * Returns the configured harshness level.
     *
     * @return harshness level
     */
    public Harshness getHarshness() {
        return harshness;
    }

    /**
     * Returns the number of turns between random pump breaks.
     *
     * @return interval in turns
     */
    public int getPumpBreakIntervalTurns() {
        return harshness.getIntervalTurnsForPumpBreak();
    }

    /**
     * Returns the number of turns between random component production.
     *
     * @return interval in turns
     */
    public int getComponentProductionIntervalTurns() {
        return harshness.getIntervalTurnsForComponentProduction();
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
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
     * Sets the total number of plumbers.
     *
     * @param count must be between MIN_PLAYERS and MAX_PLAYERS
     */
    public void setPlumberCount(int count) {
        if (count >= MIN_PLAYERS && count <= MAX_PLAYERS) {
            this.plumberCount = count;
        } else {
            System.out.printf("[ERROR] SET_PLUMBERS OUT_OF_RANGE [%d, %d]%n", MIN_PLAYERS, MAX_PLAYERS);
        }
    }

    /**
     * Sets the total number of saboteurs.
     *
     * @param count must be between MIN_PLAYERS and MAX_PLAYERS
     */
    public void setSaboteurCount(int count) {
        if (count >= MIN_PLAYERS && count <= MAX_PLAYERS) {
            this.saboteurCount = count;
        } else {
            System.out.printf("[ERROR] SET_SABOTEURS OUT_OF_RANGE [%d, %d]%n", MIN_PLAYERS, MAX_PLAYERS);
        }
    }

    /**
     * Enables or disables random events in the game.
     *
     * @param randomEventsEnabled true to enable random events, false to disable
     */
    public void setRandomEventsEnabled(boolean randomEventsEnabled) {
        this.randomEventsEnabled = randomEventsEnabled;
    }

    /**
     * Sets the game harshness level.
     *
     * @param harshness the new harshness level
     */
    public void setHarshness(Harshness harshness) {
        if (harshness != null) {
            this.harshness = harshness;
        }
    }

    /**
     * Restores all settings to their default values.
     */
    public void resetToDefault() {
        this.goalScore = DEFAULT_GOAL_SCORE;
        this.turnDurationSeconds = DEFAULT_TURN_DURATION;
        this.realTimeScoring = DEFAULT_REAL_TIME_SCORING;
        this.plumberCount = DEFAULT_PLAYERS;
        this.saboteurCount = DEFAULT_PLAYERS;
        this.randomEventsEnabled = DEFAULT_RANDOM_EVENTS_ENABLED;
        this.harshness = Harshness.MEDIUM;
    }

    /**
     * Returns a string representation of the configuration.
     *
     * @return formatted configuration string
     */
    @Override
    public String toString() {
        return String.format(
                "[STATE] GAME_CONFIG goalScore=%d turnDuration=%d realTimeScoring=%s plumbers=%d saboteurs=%d harshness=%s random=%s",
                goalScore,
                turnDurationSeconds,
                realTimeScoring ? "ON" : "OFF",
                plumberCount,
                saboteurCount,
                harshness.name(),
                randomEventsEnabled ? "ON" : "OFF");
    }
}
