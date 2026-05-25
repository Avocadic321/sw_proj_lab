package software.project.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import software.project.map.Cistern;
import software.project.map.GameMap;
import software.project.map.Pump;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.models.Team;
import software.project.utils.Debug;
import software.project.models.Teams;

/**
 * Coordinates gameplay flow, state, and high-level actions.
 */
public class GameModel {
    /**
     * Manages turns and timing.
     */
    private final TurnManager turnManager;
    /**
     * All elements currently in the game.
     */
    private final GameMap gameMap;
    /**
     * Saboteur team instance.
     */
    private Team saboteurs;
    /**
     * Plumber team instance.
     */
    private Team plumbers;
    /**
     * Current game state.
     */
    private GameState state;
    /**
     * Game configuration settings.
     */
    private final GameConfig config;

    private final WaterSimulator waterSimulator;

    private final Random random = new Random();

    // Game Loop Thread
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> gameLoopTask;

    /**
     * Creates a game with the provided configuration.
     *
     * @param config game configuration
     */
    public GameModel(GameConfig config) {
        this.gameMap = new GameMap();
        this.turnManager = new TurnManager(config.getTurnDurationSeconds());
        this.state = GameState.INITIALIZING;
        this.config = config;
        this.waterSimulator = new WaterSimulator(gameMap);
    }

    /**
     * Initializes elements, teams, and starts the first turn.
     */
    public void startGame() {
        state = GameState.INITIALIZING;

        // Create Teams
        plumbers = new Team(Teams.PLUMBERS);
        saboteurs = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            // Random Spawn point
          int randomSpawnPoint =  random.nextInt(gameMap.getAllPipes().size());
          while(!gameMap.getAllPipes().get(randomSpawnPoint).canOccupy()){
              randomSpawnPoint = random.nextInt(gameMap.getAllPipes().size());
          }
            plumbers.addPlayer(new Plumber(gameMap.getAllPipes().get(randomSpawnPoint)));
            do {
                randomSpawnPoint = random.nextInt(gameMap.getAllPipes().size());
            } while (!gameMap.getAllPipes().get(randomSpawnPoint).canOccupy());
            saboteurs.addPlayer(new Saboteur(gameMap.getAllPipes().get(randomSpawnPoint)));
        }

        turnManager.setTeams(plumbers, saboteurs);
        state = GameState.RUNNING;
        turnManager.startTurn();

        if (!config.isTestMode()) {
            Debug.log("Starting Game Loop");
            startGameLoop();
        } else {
            Debug.log("Test Mode – game loop not started");
        }
    }

    /**
     * Starts the background game loop that runs every second.
     */
    public void startGameLoop() {
        Debug.log("startGameLoop() called.");
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            Debug.log("New scheduler created.");
        }
        if (gameLoopTask == null || gameLoopTask.isDone()) {
            gameLoopTask = scheduler.scheduleAtFixedRate(
                    this::tick, 0, 1, TimeUnit.SECONDS);
            Debug.log("Game loop task scheduled at 1 sec interval.");
        } else {
            Debug.log("Game loop task already running.");
        }
    }

    /**
     * Stops the currently running game loop.
     */
    public void stopGameLoop() {
        Debug.log("stopGameLoop() called.");
        if (gameLoopTask != null) {
            gameLoopTask.cancel(false);
            gameLoopTask = null;
            Debug.log("Game loop task cancelled.");
        } else {
            Debug.log("No active game loop task to cancel.");
        }
    }

    /**
     * Terminates the game loop scheduler completely.
     */
    public void terminateGameLoop() {
        Debug.log("terminateGameLoop() called.");
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
            gameLoopTask = null;
            Debug.log("Scheduler shut down completely.");
        }
    }

    /**
     * Pauses the game if it is currently running.
     */
    public void pauseGame() {
        if (state != GameState.RUNNING)
            return;
        Debug.log("Game paused.");
        state = GameState.PAUSED;
        turnManager.suspendTurn();
        stopGameLoop();
    }

    /**
     * Resumes the game if it is currently paused.
     */
    public void resumeGame() {
        if (state != GameState.PAUSED)
            return;
        Debug.log("Game resumed.");
        state = GameState.RUNNING;
        turnManager.resumeTurn();
        startGameLoop();
    }

    /**
     * Ends the game session.
     */
    public void endGame() {
        if (state == GameState.FINALIZED)
            return;
        Debug.log("Ending game. Shutting down game loop...");
        state = GameState.FINALIZED;
        turnManager.endTurn();
        terminateGameLoop();
        this.getTurnManager().clearPropertyChangerListeners();
        System.out.println("[EVENT] GAME_OVER");
    }

    private void tick() {
        if (state != GameState.RUNNING) {
            return;
        }
        // 1. Tick the Timer in TurnManager
        turnManager.tick();

        // 2. Simulate the Water Flow
        int leakedAmount = waterSimulator.tickFlow();

        // 3. Add the scores
        calculateScore(leakedAmount);

        // 4. Check win condition
        checkWinner();

        if (turnManager.justEnded()) {
            onTurnEnded(); // random events, win check
            if (state == GameState.RUNNING) {
                turnManager.startNextTurn();
            }
        }
    }

    private void calculateScore(int leakedAmount) {
        if (leakedAmount > 0) {
            saboteurs.addScore(leakedAmount * GameConfig.SCORE_PER_WATER_LEAKED);
            Debug.log("Saboteur score +%d (total: %d)",
                    leakedAmount * GameConfig.SCORE_PER_WATER_LEAKED, saboteurs.getScore());
        }

        int totalStored = 0;
        for (Cistern c : gameMap.getAllCisterns()) {
            totalStored += c.getStoredWater();
        }
        int oldPlumberScore = plumbers.getScore();
        int additional = (totalStored * GameConfig.SCORE_PER_WATER_STORED) - oldPlumberScore;
        if (additional != 0) {
            plumbers.addScore(additional);
            Debug.log("Plumber score +%d (total: %d)", additional, plumbers.getScore());
        }
    }

    public void checkWinner() {
        if (state != GameState.RUNNING) {
            return;
        }
        int plumberScore = plumbers.getScore();
        int saboteurScore = saboteurs.getScore();
        int goal = config.getGoalScore();

        if (plumberScore >= goal) {
            Debug.log("Plumbers reached goal. Ending game.");
            endGame();
            System.out.printf("[EVENT] PLUMBERS WIN! %d Points (Goal: %d)%n", plumberScore, goal);
        } else if (saboteurScore >= goal) {
            Debug.log("Saboteurs reached goal. Ending game.");
            endGame();
            System.out.printf("[EVENT] SABOTEURS WIN! %d Points (Goal: %d)%n", saboteurScore, goal);
        }
    }

    /**
     * Breaks a random unbroken pump on the map (random event).
     */
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

        if (unbrokenPumps.isEmpty()) {
            return;
        }

        Pump target = unbrokenPumps.get(random.nextInt(unbrokenPumps.size()));
        target.breakElement();
        System.out.printf("[EVENT] RANDOM_PUMP_BROKEN %s%n", target.getId());
    }

    /**
     * Produces a random component (pipe or pump) at a random cistern.
     */
    public void produceRandomComponent() {
        List<Cistern> cisterns = gameMap.getAllCisterns();
        if (cisterns.isEmpty())
            return;

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

    /**
     * Triggers all random events if random events are enabled.
     */
    public void triggerRandomEvents() {
        if (!config.areRandomEventsEnabled()) {
            return;
        }
        breakRandomPump();
        produceRandomComponent();
    }

    /**
     * Breaks a specific pump by ID.
     *
     * @param pumpId the ID of the pump to break
     */
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

    /**
     * Produces a specific component type at a specified cistern.
     *
     * @param cisternId the ID of the cistern
     * @param type      the component type ("PIPE" or "PUMP")
     */
    public void produceComponentAt(String cisternId, String type) {
        Debug.log("produceComponentAt called for %s type=%s%n", cisternId, type);
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

    /**
     * Simulates water flow through the network.
     */
    public void flow(int ticks) {
        if (state != GameState.RUNNING) {
            System.out.println("[ERROR] FLOW GAME_NOT_RUNNING");
            return;
        }

        for (int i = 0; i < ticks; i++) {
            int leakedAmount = waterSimulator.tickFlow();
            calculateScore(leakedAmount);
            checkWinner();

            if (state == GameState.FINALIZED) {
                break;
            }
        }
    }

    /**
     * Called at the end of each turn to handle random events and win conditions.
     */
    public void onTurnEnded() {
        Debug.log("onTurnEnded() running per‑turn actions");

        triggerRandomEvents();
        checkWinner();
    }

    /**
     * Returns the current game map.
     *
     * @return the game map containing all game elements
     */
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * Returns the turn manager.
     *
     * @return the turn manager that handles turn timing and player rotation
     */
    public TurnManager getTurnManager() {
        return turnManager;
    }

    /**
     * Returns the plumbers team.
     *
     * @return the plumbers team
     */
    public Team getPlumbersTeam() {
        return plumbers;
    }

    /**
     * Returns the saboteurs team.
     *
     * @return the saboteurs team
     */
    public Team getSaboteursTeam() {
        return saboteurs;
    }

    /**
     * Returns the current game state.
     *
     * @return the current game state (RUNNING, PAUSED, FINALIZED, etc.)
     */
    public GameState getState() {
        return state;
    }

    @Override
    public String toString() {
        String currentPlayer = turnManager.getCurrentPlayer() == null
                ? "NONE"
                : turnManager.getCurrentPlayer().getId();
        String activeTeam = turnManager.getActiveTeam() == null
                ? "NONE"
                : turnManager.getActiveTeam().name();

        return String.format(
                "[STATE] GAME GAME state=%s currentPlayer=%s activeTeam=%s plumbersScore=%d saboteursScore=%d mapElements=%d",
                state,
                currentPlayer,
                activeTeam,
                plumbers == null ? 0 : plumbers.getScore(),
                saboteurs == null ? 0 : saboteurs.getScore(),
                gameMap.getElements().size());
    }
}
