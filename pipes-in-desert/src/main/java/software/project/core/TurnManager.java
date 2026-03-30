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

    public void initialize() {
        System.out.println("[TurnManager] initialize(turnTimer)");
        selectFirstCurrentPlayer();
        startTurn();
    }

    public void selectFirstCurrentPlayer() {
        System.out.println("[TurnManager] selectFirstCurrentPlayer()");
    }

    public void startTurn() {
        System.out.println("[TurnManager] startTurn()");
        timer.start();

        System.out.println("[TurnManager] isRunning = true");
        isRunning = true;
    }

    public void signalEndTurn() {
        System.out.println("[TurnManager] signalEndTurn()");
        endTurn();
    }

    public void suspendTurn() {
        System.out.println("[TurnManager] suspendTurn()");
        timer.stop();
    }

    public void suspendCurrentTurn() {
        System.out.println("[TurnManager] suspendCurrentTurn()");
        pauseActiveTurn();
        timer.stop();
    }

    public void pauseActiveTurn() {
        System.out.println("[TurnManager] pauseActiveTurn()");
    }

    public void resumeTurn() {
        System.out.println("[TurnManager] resumeTurn()");
        timer.start();
    }

    public void continueCurrentTurn() {
        System.out.println("[TurnManager] continueCurrentTurn()");
        restorePreviouslyActivePlayer();
        timer.start();
    }

    public void restorePreviouslyActivePlayer() {
        System.out.println("[TurnManager] restorePreviouslyActivePlayer()");
    }

    public void endTurn() {
        System.out.println("[TurnManager] endTurn()");
        timer.stop();
        isRunning = false;
        System.out.println("[TurnManager] isRunning = false");
        System.out.println("[TurnManager] updateGameState()");
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

    public void timerExpired() {
        System.out.println("[TurnManager] timerExpired()");
        endTurn();
    }
}
