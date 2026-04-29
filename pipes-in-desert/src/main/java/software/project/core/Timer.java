package software.project.core;

/**
 * Measures and controls the time allocated to each player's turn.
 * <p>
 * The timer maintains the total turn duration and the remaining time,
 * and provides controls to start, pause, resume, and stop the countdown.
 * The timer ticks down at regular intervals and notifies the turn manager
 * when time expires, ensuring that turns are properly limited according
 * to the game configuration.
 * </p>
 */
public class Timer {
    private int duration;
    private int timeLeft;
    private boolean isRunning;

    public Timer(int durationSeconds) {
        this.duration = durationSeconds;
        this.timeLeft = 0;
        this.isRunning = false;
    }

    /**
     * Begins the countdown from the full duration.
     * <p>
     * Sets timeLeft to turnDuration and isRunning to true.
     * Typically called at the beginning of a player's turn.
     * </p>
     */
    public void start() {
        timeLeft = duration;
        isRunning = true;
    }

    /**
     * Temporarily suspends the countdown.
     * <p>
     * Sets isRunning to false while preserving the current timeLeft.
     * Used when the game is paused.
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
     * Sets isRunning to false and resets timeLeft to zero.
     * Used when a turn ends normally or the game finishes.
     * </p>
     */
    public void stop() {
        isRunning = false;
        timeLeft = 0;
    }

    /**
     * Decrements timeLeft by one second.
     * <p>
     * Called repeatedly by a system clock while isRunning is true.
     * When timeLeft reaches zero, this method triggers the turn manager
     * to end the current turn.
     * </p>
     */
    public void tick() {
        if (!isRunning) {
            return;
        }

        timeLeft -= 1;

        if (timeLeft <= 0) {
            timeLeft = 0;
            isRunning = false;
        }
    }

    /**
     * Configures the total length of a player's turn.
     *
     * @param duration the turn duration in seconds
     */
    public void setTurnDuration(int duration) {
        this.duration = duration;
    }
}
