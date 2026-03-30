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
        timer.start();

        System.out.println("[TurnManager] isRunning = true");
        isRunning = true;
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
        timer.stop();
        isRunning = false;
        System.out.println("[TurnManager] isRunning = false");
        nextPlayer();
    }

    public void nextPlayer() {
        System.out.println("[TurnManager] nextPlayer()");
    }

    public void setTimerDuration(int seconds) {
        timer.setTurnDuration(seconds);
    }

    public void playerEndsTurn() {
        System.out.println("[TurnManager] playerEndsTurn()");
    }

    public void timeExpired() {
        System.out.println("[TurnManager] timeExpired()");
        endTurn();
    }
}
