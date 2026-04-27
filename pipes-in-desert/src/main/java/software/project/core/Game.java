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
        this.turnManager = new TurnManager();
        this.state = GameState.INITIALIZING;
        this.config = config;
    }

    /** Creates a game with default configuration. */
    public Game() {
        this(new GameConfig());
    }

    /** Initializes elements, teams, and starts the first turn. */
    public void startGame() {
        state = GameState.INITIALIZING;

        plumber = new Team(Teams.PLUMBERS);
        saboteur = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            plumber.addPlayer(new Plumber());
        }
        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            saboteur.addPlayer(new Saboteur());
        }

        turnManager.setTimerDuration(config.getTurnDurationSeconds());
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
        List<Pump> pumpsToBreak = selectRandomWorkingPumps();
        for (Pump selectedPump : pumpsToBreak) {
            selectedPump.breakElement();
        }

        List<Cistern> cisternList = gameMap.getAllCisterns();

        for (Cistern targetCistern : cisternList) {
            boolean generatePipe = Math.random() > 0.5;
            if (generatePipe) {
                Pipe newPipe = targetCistern.producePipe();
                gameMap.addElement(newPipe);
            } else {
                Pump newPump = targetCistern.producePump();
                gameMap.addElement(newPump);
            }
        }
    }

    /**
     * Selects a random subset of working pumps to break.
     *
     * @return list of pumps to break
     */
    private List<Pump> selectRandomWorkingPumps() {
        // Filter the main list for working pumps
        List<Pump> workingPumps = new ArrayList<>();
        /*for (Element e : elements) {
            if (e instanceof Pump && !((Pump) e).isBroken()) {
                workingPumps.add((Pump) e);
            }
        }*/

        // To make it "Random": Shuffle the list and take the first few
        // For this example, let's say we break 50% of working pumps
        Collections.shuffle(workingPumps);
        int countToBreak = (int) Math.ceil(workingPumps.size() * 0.5);

        return workingPumps.subList(0, countToBreak);
    }

    /** Simulates water flow through the network. */
    public void simulateWaterFlow() {
        // Reset totals at the start of every simulation
        int storedWaterTotal = 0;
        int leakedWaterTotal = 0;

        List<Spring> springs = gameMap.getAllSprings();

        for (Spring sourceSpring : springs) {
            // Ensure water production isn't 0
            int waterAmount = sourceSpring.generateWater();
            if (waterAmount == 0)
                waterAmount = 500; // Default fallback for skeleton

            // BFS Queue to trace the flow
            List<Pipe> pipeQueue = new ArrayList<>(sourceSpring.getConnectedPipes());

            while (!pipeQueue.isEmpty()) {
                Pipe activePipe = pipeQueue.removeFirst();
                // Check 1: Leaking due to sabotage?
                if (activePipe.isBroken()) {
                    leakedWaterTotal += waterAmount;
                }
                // Check 2: Leaking due to being unplugged?
                else if (activePipe.hasFreeEnd()) {
                    leakedWaterTotal += waterAmount;
                }
                // Check 3: Functional pipe
                else {
                    int forwardedAmount = activePipe.transferWater(waterAmount);

                    // Find the next component
                    Pump activePump = activePipe.getNextPump();
                    if (activePump != null) {
                        processPumpFlow(activePump, forwardedAmount, pipeQueue);
                    } else {
                        // If it's a functional pipe but leads nowhere, it's effectively a free end
                        leakedWaterTotal += forwardedAmount;
                    }
                }
            }
        }

    }

    /**
     * Routes water through a pump and its outgoing connections.
     *
     * @param activePump pump handling the flow
     * @param amount     incoming water amount
     * @param pipeQueue  queue of pipes to process
     */
    private void processPumpFlow(Pump activePump, int amount, List<Pipe> pipeQueue) {
        // Check 1: Pump Health
        if (activePump.isBroken()) {
            // System.out.println("[Game] Flow Interrupted: Pump is broken.");
            // In many designs, a broken pump causes water to leak or just stop.
            // We'll count it as a leak for the Saboteurs.
            // TODO: Use the Team Saboteur field rather than leakedWaterTotal
            // leakedWaterTotal += amount;
        }
        // Check 2: Pump Capacity
        else if (activePump.isTankFull()) {
            // System.out.println("[Game] Flow Interrupted: Pump tank is full.");
        }
        // Check 3: Pump is working
        else {
            int pumpedAmount = activePump.transferWater(amount);

            // Branch A: Water moves into the next pipe
            Pipe outgoingPipe = activePump.getOutgoingPipe();
            if (outgoingPipe != null) {
                pipeQueue.add(outgoingPipe);
                System.out.println("[Game] Water pumped into outgoing pipe.");
            }

            // Branch B: Pump is connected directly to a Cistern
            if (activePump.isConnectedToCistern()) {
                Cistern targetCistern = activePump.getTargetCistern();
                if (targetCistern != null) {
                    targetCistern.receiveWater(pumpedAmount);
                    // storedWaterTotal += pumpedAmount; // Score for Plumbers
                    // TODO: Use the correct fields in Teams rather than having it here
                }
            }
        }
    }
}
