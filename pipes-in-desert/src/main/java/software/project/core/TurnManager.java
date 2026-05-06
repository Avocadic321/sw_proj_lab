package software.project.core;

import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Team;
import software.project.utils.Debug;
import software.project.utils.Events;
import software.project.utils.Teams;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the turn-based flow of a game session.
 *
 * <p>
 * Controls turn order, tracks the currently active player, and coordinates turn transitions. The timer enforces time
 * limits per player turn. When a turn ends (either by player action or timer expiration), the manager advances to the
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

    TurnManager(int turnDuration) {
        timer = new Timer(turnDuration);
        currentPlayer = null;
        isRunning = false;
        playerSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        playerSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        playerSupport.removePropertyChangeListener(pcl);
    }

    public void setTeams(Team plumbers, Team saboteurs) {
        players.clear();
        List<Player> plList = plumbers.getPlayers();
        List<Player> saList = saboteurs.getPlayers();

        // interleave: PLUMBER0, SABOTEUR0, PLUMBER1, SABOTEUR1, ...
        int max = Math.max(plList.size(), saList.size());
        for (int i = 0; i < max; i++) {
            if (i < plList.size()) players.add(plList.get(i));
            if (i < saList.size()) players.add(saList.get(i));
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
        if (players.isEmpty()) return;
        timer.start();
        isRunning = true;
        Debug.log("Turn started: %s (%s) | Time left: %ds",
            currentPlayer.getId(), activeTeam, timer.getTimeLeft());
    }

    public void tick() {
        if (!isRunning || currentPlayer == null) return;
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
     * Stops the timer, clears the running flag, and advances to the next player.
     * </p>
     */
    public void endTurn() {
        if (currentPlayer == null) return;
        Player finishingPlayer = currentPlayer;
        Teams finishingTeam = activeTeam;
        timer.stop();
        isRunning = false;
        turnEnded = true;

        Debug.log("Turn ended: %s (%s)",
            finishingPlayer.getId(), finishingTeam);
    }

    public void startNextTurn() {
        if (players.isEmpty()) return;
        advanceToNextPlayer();
        if (currentPlayer != null) {
            startTurn();
        }
    }

    private void advanceToNextPlayer() {
        if (players.isEmpty()) return;
        currentIndex = (currentIndex + 1) % players.size();
        Player oldPlayer = currentPlayer;
        currentPlayer = players.get(currentIndex);
        activeTeam = (currentPlayer instanceof Plumber) ? Teams.PLUMBERS : Teams.SABOTEURS;
        Debug.log("Advanced to index %d: %s (%s)", currentIndex, currentPlayer.getId(), activeTeam);
        playerSupport.firePropertyChange(Events.ON_PLAYER_TURN_CHANGE, oldPlayer, this.currentPlayer);
    }

    /**
     * Suspends the current turn without ending it.
     *
     * <p>
     * Stops the timer but preserves the turn state for later resumption. Used when the game is paused.
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

    public boolean justEnded() {
        if (turnEnded) {
            turnEnded = false;
            return true;
        }
        return false;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Teams getActiveTeam() {
        return activeTeam;
    }

    public int getTimeLeft() {
        return timer.getTimeLeft();
    }

    public int getTurnDuration() {
        return timer.getTimeLeft();
    }
}
