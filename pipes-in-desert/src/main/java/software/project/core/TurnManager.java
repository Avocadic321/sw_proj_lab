package software.project.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Team;
import software.project.models.Teams;
import software.project.utils.Constants;
import software.project.utils.Debug;

/**
 * Manages the turn-based flow of a game session.
 *
 * <p>
 * Controls turn order, tracks the currently active player, and coordinates turn
 * transitions. The timer enforces time
 * limits per player turn. When a turn ends (either by player action or timer
 * expiration), the manager advances to the
 * next player.
 * </p>
 *
 * @see Timer
 * @see Player
 */
public class TurnManager {
    private final Timer timer;
    private final List<Player> players = new ArrayList<>();
    private int currentIndex = 0;
    private Player currentPlayer;
    private PropertyChangeSupport playerSupport;
    private boolean isRunning;
    private Teams activeTeam;

    private boolean turnEnded = false;
    private boolean smallActionUsed = false;
    private boolean bigActionUsed = false;

    /**
     * Creates a turn manager with the specified per-turn duration.
     *
     * @param turnDuration turn length in seconds
     */
    TurnManager(int turnDuration) {
        timer = new Timer(turnDuration);
        currentPlayer = null;
        isRunning = false;
        playerSupport = new PropertyChangeSupport(this);
    }

    /**
     * Registers a listener for turn-advance notifications.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        playerSupport.addPropertyChangeListener(pcl);
    }

    /**
     * Removes a previously registered turn-advance listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        playerSupport.removePropertyChangeListener(pcl);
    }

    /**
     * Removes all registered turn-advance listeners.
     */
    public void clearPropertyChangerListeners() {
        var listeners = playerSupport.getPropertyChangeListeners();
        for (int i = 0; i < listeners.length; i++) {
            playerSupport.removePropertyChangeListener(listeners[i]);
        }
    }

    /**
     * Loads the team rosters and initializes turn order.
     *
     * <p>
     * Players are interleaved so the turn order alternates between teams when
     * possible.
     * </p>
     */
    public void setTeams(Team plumbers, Team saboteurs) {
        players.clear();
        List<Player> plList = plumbers.getPlayers();
        List<Player> saList = saboteurs.getPlayers();

        // interleave: PLUMBER0, SABOTEUR0, PLUMBER1, SABOTEUR1, ...
        int max = Math.max(plList.size(), saList.size());
        for (int i = 0; i < max; i++) {
            if (i < plList.size())
                players.add(plList.get(i));
            if (i < saList.size())
                players.add(saList.get(i));
        }

        if (!players.isEmpty()) {
            currentIndex = 0;
            currentPlayer = players.getFirst();
            activeTeam = (currentPlayer instanceof Plumber) ? Teams.PLUMBERS : Teams.SABOTEURS;
        }

        if (Debug.ENABLED) {
            Debug.log("Teams set. Player order: ");
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                Debug.log("  [%d] %s (%s)", i, p.getId(), (p instanceof Plumber ? "PLUMBER" : "SABOTEUR"));
            }
            Debug.log("First player: %s (%s)", currentPlayer.getId(), activeTeam);
        }
    }

    /**
     * Begins a new turn for the current player.
     *
     * <p>
     * Starts the turn timer and sets the manager state to running.
     * </p>
     */
    public void startTurn() {
        if (players.isEmpty())
            return;
        resetActionLimits();
        timer.start();
        isRunning = true;
        Debug.log("Turn started: %s (%s) | Time left: %ds",
                currentPlayer.getId(), activeTeam, timer.getTimeLeft());
    }

    /**
     * Advances the turn timer by one tick and ends the turn when it expires.
     */
    public void tick() {
        if (!isRunning || currentPlayer == null)
            return;
        timer.tick();

        // Debug - log remaining time in mm::ss
        if (Debug.ENABLED) {
            int secsLeft = timer.getTimeLeft();
            int mins = secsLeft / 60;
            int secs = secsLeft % 60;
            Debug.log("Timer: %02d:%02d", mins, secs);
        }

        if (timer.hasExpired()) {
            Debug.log("Time expired for %s (%s)", currentPlayer.getId(), activeTeam);
            endTurn();
        }
    }

    /**
     * Terminates the current turn.
     *
     * <p>
     * This stops the timer and flags the turn so the game loop can advance to
     * the next player.
     * </p>
     */
    public void endTurn() {
        if (currentPlayer == null)
            return;
        Player finishingPlayer = currentPlayer;
        Teams finishingTeam = activeTeam;
        timer.stop();
        isRunning = false;
        turnEnded = true;

        Debug.log("Turn ended: %s (%s)",
                finishingPlayer.getId(), finishingTeam);
    }

    /**
     * Advances to the next player and immediately starts that player's turn.
     */
    public void startNextTurn() {
        if (players.isEmpty())
            return;
        advanceToNextPlayer();
        if (currentPlayer != null) {
            startTurn();
        }
    }

    /**
     * Moves turn state to the next player in the roster.
     */
    private void advanceToNextPlayer() {
        if (players.isEmpty())
            return;
        currentIndex = (currentIndex + 1) % players.size();
        Player oldPlayer = currentPlayer;
        currentPlayer = players.get(currentIndex);
        playerSupport.firePropertyChange(Constants.PLAYER_ADVANCED, oldPlayer, currentPlayer);
        activeTeam = (currentPlayer instanceof Plumber) ? Teams.PLUMBERS : Teams.SABOTEURS;
        Debug.log("Advanced to index %d: %s (%s)", currentIndex, currentPlayer.getId(), activeTeam);
    }

    /**
     * Suspends the current turn without ending it.
     *
     * <p>
     * Stops the timer but preserves the turn state for later resumption. Used when
     * the game is paused.
     * </p>
     */
    public void suspendTurn() {
        timer.pause();
    }

    /**
     * Resumes a previously suspended turn.
     *
     * <p>
     * Restarts the timer from where it was stopped.
     * </p>
     */
    public void resumeTurn() {
        timer.resume();
    }

    /**
     * Returns true once for the turn that has just ended.
     */
    public boolean justEnded() {
        if (turnEnded) {
            turnEnded = false;
            return true;
        }
        return false;
    }

    /**
     * Returns the player whose turn is currently active.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the team that owns the current turn.
     */
    public Teams getActiveTeam() {
        return activeTeam;
    }

    /**
     * Returns the remaining time in the current turn.
     */
    public int getTimeLeft() {
        return timer.getTimeLeft();
    }

    /**
     * Returns the configured turn duration.
     */
    public int getTurnDuration() {
        return timer.getTimeLeft();
    }

    /**
     * Returns true if the current player still has their move action available.
     */
    public boolean canUseSmallAction() {
        return !smallActionUsed;
    }

    /**
     * Returns true if the current player still has their non-move action available.
     */
    public boolean canUseBigAction() {
        return !bigActionUsed;
    }

    /**
     * Consumes the turn's small action if it has not been used yet.
     *
     * @return true if the action was consumed successfully
     */
    public boolean useSmallAction() {
        if (smallActionUsed) {
            return false;
        }
        smallActionUsed = true;
        maybeEndTurnAfterActions();
        return true;
    }

    /**
     * Consumes the turn's big action if it has not been used yet.
     *
     * @return true if the action was consumed successfully
     */
    public boolean useBigAction() {
        if (bigActionUsed) {
            return false;
        }
        bigActionUsed = true;
        maybeEndTurnAfterActions();
        return true;
    }

    /**
     * Resets the per-turn action counters.
     */
    private void resetActionLimits() {
        smallActionUsed = false;
        bigActionUsed = false;
    }

    /**
     * Ends the turn automatically once both allowed actions have been used.
     */
    private void maybeEndTurnAfterActions() {
        if (smallActionUsed && bigActionUsed) {
            endTurn();
        }
    }
}
