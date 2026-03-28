package software.project.core;

import software.project.models.Player;

public class TurnManager {
    public Timer timer;
    public Player currentPlayer;
    public boolean isRunning;

    TurnManager() {
        timer = new Timer();
        currentPlayer = null;
        isRunning = false;
    }

    public void startTurn() {
        System.out.println("[TurnManager] startTurn()");
        timer.setTurnDuration(120);
        timer.start();

    }

    public void suspendTurn() {
        System.out.println("[TurnManager] suspendTurn()");
        timer.stop();
    }

    public void resumeTurn() {
        System.out.println("[TurnManager] resumeTurn()");
        timer.start();
    }

    public void endTurn() {
        System.out.println("[TurnManager] endTurn()");
    }

    public void nextPlayer() {
        System.out.println("[TurnManager] nextPlayer()");
    }
}
