package software.project.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    private final Random random = new Random();

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
        List<Pump> pumpList = gameMap.getAllPumps();
        if (pumpList.isEmpty()) {
            return;
        }

        List<Pump> unbrokenPumps = new ArrayList<>();
        for (Pump pump : pumpList) {
            if (!pump.isBroken()) {
                unbrokenPumps.add(pump);
            }
        }

        if (unbrokenPumps.isEmpty()) { return; }

        Pump target = unbrokenPumps.get(random.nextInt(unbrokenPumps.size()));
        target.breakElement();
        System.out.printf("[EVENT] RANDOM_PUMP_BROKEN %s%n", target.getId());
    }

    public void produceRandomComponent() {
        List<Cistern> cisterns = gameMap.getAllCisterns();
        if (cisterns.isEmpty()) return;

        Cistern c = cisterns.get(random.nextInt(cisterns.size()));
        boolean producePipe = random.nextBoolean();

        if (producePipe && c.getStoredPipe() == null) {
            c.producePipe();
            System.out.printf("[EVENT] COMPONENT_PRODUCED %s PIPE%n", c.getId());
        } else if (!producePipe && c.getStoredPump() == null) {
            c.producePump();
            System.out.printf("[EVENT] COMPONENT_PRODUCED %s PUMP%n", c.getId());
        }

        // TODO: Add logic later for placing the produced component
    }

    public void performRandomEvents() {
        breakRandomPump();
        produceRandomComponent();
    }


    /** Simulates water flow through the network. */
    public void simulateWaterFlow() {
    }

    public GameState getState() {
        return state;
    }
}
