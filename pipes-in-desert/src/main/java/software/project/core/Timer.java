package software.project.core;

/**
 * Tracks and updates the countdown for a single player turn.
 *
 * <p>
 * The timer supports start, pause, resume, and stop operations, and keeps
 * enough state for the turn manager to decide when a turn has expired.
 * </p>
 */
public class Timer {
    private int duration;
    private int timeLeft;
    private boolean isRunning;
    private boolean expired;

    /**
     * Creates a timer with the specified duration.
     *
     * @param durationSeconds total turn length in seconds
     */
    public Timer(int durationSeconds) {
        this.duration = durationSeconds;
        this.timeLeft = 0;
        this.isRunning = false;
        this.expired = true;
    }

    /**
     * Begins the countdown from the full duration.
     * <p>
     * Sets timeLeft to turnDuration and isRunning to true. Typically called at the
     * beginning of a player's turn.
     * </p>
     */
    public void start() {
        timeLeft = duration;
        isRunning = true;
        expired = false;
    }

    /**
     * Temporarily suspends the countdown.
     * <p>
     * Sets isRunning to false while preserving the current timeLeft. Used when the
     * game is paused.
     * </p>
     */
    public void pause() {
        isRunning = false;
    }

    /**
     * Restarts the countdown from the paused value.
     * <p>
     * Sets isRunning back to true. Called when the game resumes after a pause.
     * </p>
     */
    public void resume() {
        isRunning = true;
    }

    /**
     * Ends the countdown entirely.
     * <p>
     * Sets isRunning to false and resets timeLeft to zero. Used when a turn ends
     * normally or the game finishes.
     * </p>
     */
    public void stop() {
        isRunning = false;
        timeLeft = 0;
        expired = true;
    }

    /**
     * Decrements timeLeft by one second.
     * <p>
     * Called repeatedly by a system clock while isRunning is true. When timeLeft
     * reaches zero, this method triggers the
     * turn manager to end the current turn.
     * </p>
     */
    public void tick() {
        if (!isRunning || expired) {
            return;
        }

        timeLeft -= 1;

        if (timeLeft <= 0) {
            timeLeft = 0;
            isRunning = false;
            expired = true;
        }
    }

    /**
     * Returns true when the timer has reached zero.
     */
    public boolean hasExpired() {
        return expired;
    }

    /**
     * Returns the remaining time in seconds.
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Returns the configured duration in seconds.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Updates the configured turn duration.
     *
     * @param duration new duration in seconds
     */
    public void setTurnDuration(int duration) {
        this.duration = duration;
    }
}
