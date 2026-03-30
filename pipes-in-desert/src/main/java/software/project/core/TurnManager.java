package software.project.core;

import software.project.models.Player;

/**
 * Manages the turn-based flow of a game session.
 *
 * <p>Controls turn order, tracks the currently active player, and coordinates
 * turn transitions. The timer enforces time limits per player turn. When a turn
 * ends (either by player action or timer expiration), the manager advances to
 * the next player.</p>
 *
 * @see Timer
 * @see Player
 */
public class TurnManager {
    public Timer timer;
    public Player currentPlayer;
    public boolean isRunning;

    /** Creates a new turn manager with default values. */
    TurnManager() {
        timer = new Timer();
        currentPlayer = null;
        isRunning = false;
    }

    /**
     * Begins a new turn for the current player.
     *
     * <p>Starts the turn timer and sets the manager state to running.</p>
     */
    public void startTurn() {
        System.out.println("[TurnManager] startTurn()");
        timer.start();

        System.out.println("[TurnManager] isRunning = true");
        isRunning = true;
    }

    /**
     * Suspends the current turn without ending it.
     *
     * <p>Stops the timer but preserves the turn state for later resumption.
     * Used when the game is paused.</p>
     */
    public void suspendTurn() {
        System.out.println("[TurnManager] suspendTurn()");
        timer.stop();
    }

    /**
     * Resumes a previously suspended turn.
     *
     * <p>Restarts the timer from where it was stopped.</p>
     */
    public void resumeTurn() {
        System.out.println("[TurnManager] resumeTurn()");
        timer.start();
    }

    /**
     * Terminates the current turn.
     *
     * <p>Stops the timer, clears the running flag, and advances to the next
     * player.</p>
     */
    public void endTurn() {
        System.out.println("[TurnManager] endTurn()");
        timer.stop();
        isRunning = false;
        System.out.println("[TurnManager] isRunning = false");
        nextPlayer();
    }

    public void nextPlayer() {
        System.out.println("[TurnManager] nextPlayer()");
    }

    /**
     * Sets the duration of each player's turn.
     *
     * @param seconds  turn length in seconds
     */
    public void setTimerDuration(int seconds) {
        timer.setTurnDuration(seconds);
    }

    public void playerEndsTurn() {
        System.out.println("[TurnManager] playerEndsTurn()");
    }

    /**
     * Handles turn expiration when the timer reaches zero.
     *
     * <p>Automatically ends the current turn.</p>
     */
    public void timeExpired() {
        System.out.println("[TurnManager] timeExpired()");
        endTurn();
    }
}
