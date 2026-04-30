package software.project.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import software.project.models.*;

import software.project.utils.GameState;
import software.project.utils.Teams;

/**
 * Coordinates gameplay flow, state, and high-level actions.
 */
public class Game {
    /** Manages turns and timing. */
    private TurnManager turnManager;
    /** All elements currently in the game. */
    private GameMap gameMap;
    /** Saboteur team instance. */
    private Team saboteur;
    /** Plumber team instance. */
    private Team plumber;
    /** Current game state. */
    private GameState state;
    /** Game configuration settings. */
    private GameConfig config;

    /**
     * Creates a game with the provided configuration.
     *
     * @param config game configuration
     */
    public Game(GameConfig config) {
        this.gameMap = new GameMap();
        this.turnManager = new TurnManager(config.getTurnDurationSeconds());
        this.state = GameState.INITIALIZING;
        this.config = config;
    }

    /** Initializes elements, teams, and starts the first turn. */
    public void startGame() {
        state = GameState.INITIALIZING;

        plumber = new Team(Teams.PLUMBERS);
        saboteur = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            String id = "PLUMBER" + i;
            plumber.addPlayer(new Plumber(id, gameMap.getSpawnPoint()));
        }
        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            String id = "SABOTEUR" + i;
            saboteur.addPlayer(new Saboteur(id, gameMap.getSpawnPoint()));
        }

        turnManager.startTurn();
        state = GameState.RUNNING;
    }

    /** Pauses the game if it is currently running. */
    public void pauseGame() {
        if (state != GameState.RUNNING) {
            return;
        }

        state = GameState.PAUSED;
        turnManager.suspendTurn();
    }

    /** Resumes the game if it is currently paused. */
    public void resumeGame() {
        if (state != GameState.PAUSED) {
            return;
        }
        state = GameState.RUNNING;
        turnManager.resumeTurn();
    }

    /** Ends the game session. */
    public void endGame() {}

    public void checkWinner() {

    }

    public void breakSpecificPump(String pumpId) {

    }

    public void produceComponentAt(String cisternId, String type) {

    }

    public void breakRandomPump() {

    }

    public void produceRandomComponent() {

    }

    public void performRandomEvents() {
        breakRandomPump();
        produceRandomComponent();
    }

    /** TODO: Separate method */
    public void processRandomEvent() {
    }

    /** Simulates water flow through the network. */
    public void simulateWaterFlow() {
    }

    public GameState getState() {
        return state;
    }
}
