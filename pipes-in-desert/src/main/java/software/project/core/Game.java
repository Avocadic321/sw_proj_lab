package software.project.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import software.project.models.*;
import software.project.utils.Debug;
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
    private Team saboteurs;
    /** Plumber team instance. */
    private Team plumbers;
    /** Current game state. */
    private GameState state;
    /** Game configuration settings. */
    private GameConfig config;

    private WaterSimulator waterSimulator;

    private final Random random = new Random();
    private Thread loopThread;

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
        this.waterSimulator = new WaterSimulator(gameMap);
    }

    /** Initializes elements, teams, and starts the first turn. */
    public void startGame() {
        state = GameState.INITIALIZING;

        // Create Teams
        plumbers = new Team(Teams.PLUMBERS);
        saboteurs = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            String id = "PLUMBER" + i;
            plumbers.addPlayer(new Plumber(id, gameMap.getSpawnPoint()));
        }
        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            String id = "SABOTEUR" + i;
            saboteurs.addPlayer(new Saboteur(id, gameMap.getSpawnPoint()));
        }

        turnManager.setTeams(plumbers, saboteurs);
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
    public void endGame() {
        if (state == GameState.FINALIZED) {
            return;
        }
        turnManager.endTurn();
        state = GameState.FINALIZED;

        System.out.println("[EVENT] GAME_OVER");
    }

    public void checkWinner() {
        if (state != GameState.RUNNING) {
            return;
        }

        int plumberScore = plumbers.getScore();
        int saboteurScore = saboteurs.getScore();
        int goal = config.getGoalScore();

        if (plumberScore >= goal) {
            endGame();
            System.out.printf("[EVENT] PLUMBERS WIN! %d Points (Goal: %d)", plumberScore, goal);
        } else if (saboteurScore >= goal) {
            endGame();
            System.out.printf("[EVENT] SABOTEURS WIN! %d Points (Goal: %d)", plumberScore, goal);
        }
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

    public void triggerRandomEvents() {
        if (!config.areRandomEventsEnabled()) {
            return;
        }
        breakRandomPump();
        produceRandomComponent();
    }

    public void breakSpecificPump(String pumpId) {
        Pump pump = gameMap.getElement(pumpId, Pump.class);
        if (pump == null) {
            System.out.printf("[WARNING] PUMP_NOT_FOUND %s%n", pumpId);
            return;
        }
        if (pump.isBroken()) {
            System.out.printf("[WARNING] PUMP_ALREADY_BROKEN %s%n", pumpId);
            return;
        }
        pump.breakElement();
        System.out.printf("[EVENT] PUMP_BROKEN %s%n", pumpId);
    }

    public void produceComponentAt(String cisternId, String type) {
        if (Debug.ENABLED) {
            System.out.printf("[DEBUG] produceComponentAt called for %s type=%s%n", cisternId, type);
        }
        Cistern cistern = gameMap.getElement(cisternId, Cistern.class);
        if (cistern == null) {
            System.out.printf("[WARNING] CISTERN_NOT_FOUND %s%n", cisternId);
            return;
        }
        if (type.equalsIgnoreCase("PIPE")) {
            cistern.producePipe();
            System.out.printf("[EVENT] COMPONENT_PRODUCED %s PIPE%n", cisternId);
        } else if (type.equalsIgnoreCase("PUMP")) {
            cistern.producePump();
            System.out.printf("[EVENT] COMPONENT_PRODUCED %s PUMP%n", cisternId);
        } else {
            System.out.printf("[WARNING] UNKNOWN_COMPONENT_TYPE %s%n", type);
        }
    }

    /** Simulates water flow through the network. */
    public void simulateWaterFlow() {
    }

    public void onTurnEnded() {
        if (Debug.ENABLED) {
            System.out.println("[DEBUG] onTurnEnded() running per‑turn actions");
        }
        simulateWaterFlow();
        triggerRandomEvents();
        checkWinner();
    }

    public GameMap getGameMap() {
        return gameMap;
    }
    public Team getPlumbersTeam() { return plumbers; }
    public Team getSaboteursTeam() {
        return saboteurs;
    }
    public GameState getState() { return state; }
}
