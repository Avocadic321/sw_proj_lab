package software.project.core;

/**
 * Represents the possible states of a game session throughout its lifecycle.
 * <p>
 * The game progresses through these states in a linear fashion, with the exception that the {@link #RUNNING} state may
 * transition to {@link #PAUSED} and back multiple times during a single session.
 * </p>
 * <p>
 * The typical lifecycle is:
 * <ul>
 * <li>{@link #INITIALIZING} - Game is being set up, elements are being created</li>
 * <li>{@link #RUNNING} - Game is actively being played, turns are in progress</li>
 * <li>{@link #PAUSED} - Game is temporarily suspended, timer frozen</li>
 * <li>{@link #FINALIZED} - Game has ended, winner has been determined</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public enum GameState {
    /**
     * The game is being initialized.
     * <p>
     * In this state, the system creates the initial pipe network elements, sets up teams and players, and prepares the
     * game environment. No player actions are processed during initialization.
     * </p>
     */
    INITIALIZING,

    /**
     * The game is actively running.
     * <p>
     * In this state, turns are in progress, players can perform actions, and the turn timer is counting down. Water
     * flow is simulated and scores are updated according to the configured mode.
     * </p>
     */
    RUNNING,
    /**
     * The game is temporarily paused.
     * <p>
     * In this state, the turn timer is frozen and all player actions are prevented. The game state remains unchanged
     * until the player chooses to resume the session.
     * </p>
     */
    PAUSED,

    /**
     * The game has ended.
     * <p>
     * This is a terminal state reached when one team reaches the goal score. No further actions are permitted, and the
     * winner is displayed to the players.
     * </p>
     */
    FINALIZED
}
