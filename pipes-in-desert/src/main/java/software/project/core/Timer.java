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
    private int turnDuration;
    private int timeLeft;
    private boolean isRunning;

    public Timer(int turnDuration) {
        this.turnDuration = turnDuration;
    }

    /**
     * Begins the countdown from the full duration.
     * <p>
     * Sets timeLeft to turnDuration and isRunning to true.
     * Typically called at the beginning of a player's turn.
     * </p>
     */
    public void start() {
        System.out.println("[Timer] start()");
    }

    /**
     * Temporarily suspends the countdown.
     * <p>
     * Sets isRunning to false while preserving the current timeLeft.
     * Used when the game is paused.
     * </p>
     */
    public void pause() {
        System.out.println("[Timer] pause()");
    }

    /**
     * Restarts the countdown from the paused value.
     * <p>
     * Sets isRunning back to true. Called when the game resumes after a pause.
     * </p>
     */
    public void resume() {
        System.out.println("[Timer] resume()");
    }

    /**
     * Ends the countdown entirely.
     * <p>
     * Sets isRunning to false and resets timeLeft to zero.
     * Used when a turn ends normally or the game finishes.
     * </p>
     */
    public void stop() {
        System.out.println("[Timer] stop()");
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
        System.out.println("[Timer] tick()");
    }

    /**
     * Configures the total length of a player's turn.
     *
     * @param duration the turn duration in seconds
     */
    public void setTurnDuration(int duration) {
        turnDuration = duration;
    }
}
