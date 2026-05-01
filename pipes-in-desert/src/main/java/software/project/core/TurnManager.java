package software.project.core;

import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Team;
import software.project.utils.Teams;

import java.util.ArrayList;
import java.util.List;

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
    private final Timer timer;
    private final List<Player> players = new ArrayList<>();
    private int currentIndex = 0;
    private Player currentPlayer;
    private boolean isRunning;
    private Teams activeTeam;

    private boolean turnEnded = false;


    TurnManager(int turnDuration) {
        timer = new Timer(turnDuration);
        currentPlayer = null;
        isRunning = false;
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
    }

    public void tick() {
        if (!isRunning || currentPlayer == null ) return;
        timer.tick();
        if (timer.hasExpired()) {
            endTurn();
        }
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
        if (currentPlayer == null) return;
        timer.stop();
        isRunning = false;
        advanceToNextPlayer();
        turnEnded = true;
        if (currentPlayer != null) {
            startTurn();               // start the next player's turn
        }
    }

    private void advanceToNextPlayer() {
        if (players.isEmpty()) return;
        currentIndex = (currentIndex + 1) % players.size();
        currentPlayer = players.get(currentIndex);
        activeTeam = (currentPlayer instanceof Plumber) ? Teams.PLUMBERS : Teams.SABOTEURS;
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
