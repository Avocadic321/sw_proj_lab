package software.project.core;

import software.project.models.Player;

public class TurnManager {
    public Timer timer;
    public Player currentPlayer;
    public boolean isRunning;

    public void startTurn() {
        System.out.println("[TurnManager] startTurn()");
    }

    public void endTurn() {
        System.out.println("[TurnManager] endTurn()");
    }

    public void nextPlayer() {
        System.out.println("[TurnManager] nextPlayer()");
    }
}
