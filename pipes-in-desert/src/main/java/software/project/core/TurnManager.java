package software.project.core;

import software.project.models.Player;

/**
 * Manages the turn-based flow of a game session.
 *
 * <p>
 * Controls turn order, tracks the currently active player, and coordinates
 * turn transitions. The timer enforces time limits per player turn. When a turn
 * ends (either by player action or timer expiration), the manager advances to
 * the next player.
 * </p>
 *
 * @see Timer
 * @see Player
 */
public class TurnManager {
    private Timer timer;
    private Player currentPlayer;
    private boolean isRunning;

    /** Creates a new turn manager with default values. */
    TurnManager(int turnDuration) {
        timer = new Timer(turnDuration);
        currentPlayer = null;
        isRunning = false;
    }

    /**
     * Begins a new turn for the current player.
     *
     * <p>
     * Starts the turn timer and sets the manager state to running.
     * </p>
     */
    public void startTurn() {
        timer.start();
        isRunning = true;
    }

    public void nextTurn() {

    }

    /**
     * Suspends the current turn without ending it.
     *
     * <p>
     * Stops the timer but preserves the turn state for later resumption.
     * Used when the game is paused.
     * </p>
     */
    public void suspendTurn() {
        timer.stop();
    }

    /**
     * Resumes a previously suspended turn.
     *
     * <p>
     * Restarts the timer from where it was stopped.
     * </p>
     */
    public void resumeTurn() {
        timer.start();
    }

    /**
     * Terminates the current turn.
     *
     * <p>
     * Stops the timer, clears the running flag, and advances to the next
     * player.
     * </p>
     */
    public void endTurn() {
        timer.stop();
        isRunning = false;

        nextPlayer();
    }

    /**
     * Advances the turn to the next player.
     */
    public void nextPlayer() {

    }
}
