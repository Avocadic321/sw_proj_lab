package software.project.core;

import software.project.models.*;
import software.project.utils.GameState;
import software.project.utils.Teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    public TurnManager turnManager;
    public List<Element> elements;
    public Team saboteur;
    public Team plumber;
    public GameState state;
    public GameConfig config;

    public Game(GameConfig config) {
        this.elements = new ArrayList<>();
        this.turnManager = new TurnManager();
        this.state = GameState.INITIALIZING;
        this.config = config;
        System.out.println("[Game] Game object created");
    }

    public Game() {
        this(new GameConfig());
    }

    public void startGame() {
        System.out.println("[Game] startGame()");
        System.out.println("[Game] state = INITIALIZING");
        state = GameState.INITIALIZING;

        Spring spring = new Spring();
        Cistern cistern = new Cistern();
        Pump pump = new Pump();

        addElement(spring);
        addElement(cistern);
        addElement(pump);

        plumber = new Team(Teams.PLUMBERS);
        saboteur = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            plumber.addPlayer(new Plumber());
        }
        System.out.println('\n');
        for (int i = 0; i < config.getNumberOfPlayers(); ++i) {
            saboteur.addPlayer(new Saboteur());
        }

        turnManager.setTimerDuration(config.getTurnDurationSeconds());
        turnManager.startTurn();

        System.out.println("[Game] state = RUNNING");
        state = GameState.RUNNING;
    }

    public void pauseGame() {
        System.out.println("[Game] pauseGame()\n");
        if (!(state == GameState.RUNNING)) {
            System.out.println("[Game] pauseGame() - You cannot pause a Game if it not RUNNING");
            return;
        }

        System.out.println("[Game] state = PAUSED\n");
        state = GameState.PAUSED;

        turnManager.suspendTurn();
    }

    public void resumeGame() {
        System.out.println("[Game] resumeGame()\n");
        if (state != GameState.PAUSED) {
            System.out.println("[Game] resumeGame() - You cannot resume a Game if it not PAUSED");
            return;
        }

        System.out.println("[Game] state = RUNNING\n");
        state = GameState.RUNNING;

        turnManager.resumeTurn();
    }

    public void endGame() {
        System.out.println("[Game] endGame()\n");
    }

    public Element getElementById(String id) {
        for (Element e : elements) {
            if (e.id != null && e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }

    public void performRandomEvents() {
        System.out.println("[Game] performRandomEvents()\n");
    }

    public void addElement(Element element) {
        this.elements.add(element);
        System.out.println("[Game] addElement() - " + element.getClass().getSimpleName());
    }

    // UC-14
    public void selectPipe(Pipe targetPipe) {
        System.out.println("[Game] selectPipe() - " + targetPipe);
    }

    public void sabotagePipe(Pipe targetPipe) {
        System.out.println("[Game] sabotagePipe()");

        boolean broken = targetPipe.isBroken();

        if (!broken) {
            System.out.println("[Game] Pipe is functional -> sabotaging");
            targetPipe.breakElement();
            System.out.println("[Game] Result: sabotaged");
        } else {
            System.out.println("[Game] Pipe already leaking -> noChange");
        }
    }

    // UC-15

    public void calculateScore() {
        System.out.println("[Game] calculateScore()");

        simulateWaterFlow();
        Object flowReport = new Object();
        System.out.println("[Game] flowReport generated");

        // Simulate water reaching cisterns
        for (Element element : elements) {
            if (element instanceof Cistern) {
                Cistern cistern = (Cistern) element;
                cistern.receiveWater(10); // just a placeholder amount
            }
        }

        int storedWaterTotal = getTotalStoredWater(flowReport);
        plumber.addScore(storedWaterTotal);

        int leakedWaterTotal = getTotalLeakedWater(flowReport);
        saboteur.addScore(leakedWaterTotal);

        updateDisplayedScores();
    }

    public int getTotalStoredWater(Object flowReport) {
        System.out.println("[Game] getTotalStoredWater()");
        return 42; // placeholder
    }

    public int getTotalLeakedWater(Object flowReport) {
        System.out.println("[Game] getTotalLeakedWater()");
        return 17; // placeholder
    }

    public void updateDisplayedScores() {
        System.out.println("[Game] updateDisplayedScores()");
        System.out.println("[Game] Plumber score = " + plumber.getScore());
        System.out.println("[Game] Saboteur score = " + saboteur.getScore());
    }

    // UC-16

    public void processRandomEvent() {
        System.out.println("[Game] processRandomEvent()");

        List<Pump> pumpsToBreak = selectRandomWorkingPumps();
        System.out.println("[Game] pumpsToBreak = " + pumpsToBreak.size());

        for (Pump selectedPump : pumpsToBreak) {
            System.out.println("[Game] Breaking pump: " + selectedPump);
            selectedPump.breakElement();
        }

        List<Cistern> cisternList = getCisterns();
        System.out.println("[Game] cisternList size = " + cisternList.size());

        for (Cistern targetCistern : cisternList) {
            boolean generatePipe = Math.random() > 0.5;
            if (generatePipe) {
                System.out.println("[Game] Generating Pipe from cistern: " + targetCistern);
                Pipe newPipe = targetCistern.producePipe();
                addElement(newPipe);
            } else {
                System.out.println("[Game] Generating Pump from cistern: " + targetCistern);
                Pump newPump = targetCistern.producePump();
                addElement(newPump);
            }
        }

        updateGameState();
        System.out.println("[Game] Game state updated after random event\n");
    }

    private List<Pump> selectRandomWorkingPumps() {
        System.out.println("[Game] selectRandomWorkingPumps()");
        
        // Filter the main list for working pumps
        List<Pump> workingPumps = new ArrayList<>();
        for (Element e : elements) {
            if (e instanceof Pump && !((Pump) e).isBroken()) {
                workingPumps.add((Pump) e);
            }
        }

        // To make it "Random": Shuffle the list and take the first few
        // For this example, let's say we break 50% of working pumps
        Collections.shuffle(workingPumps);
        int countToBreak = (int) Math.ceil(workingPumps.size() * 0.5);
        
        return workingPumps.subList(0, countToBreak);
    }

    private List<Cistern> getCisterns() {
        System.out.println("[Game] getCisterns()");
        List<Cistern> cisterns = new ArrayList<>();
        
        for (Element e : elements) {
            if (e instanceof Cistern) {
                cisterns.add((Cistern) e);
            }
        }
        return cisterns;
    }

    private void updateGameState() {
        System.out.println("[Game] updateGameState()");
    }

    // UC-17

    public void checkWinner() {
        System.out.println("[Game] checkWinner()");
        
        int plumberScore = plumber.getScore();
        int saboteurScore = saboteur.getScore();
        System.out.println("[Game] Plumber score = " + plumberScore);
        System.out.println("[Game] Saboteur score = " + saboteurScore);

        compareScores(plumberScore, saboteurScore);
    }

    public void compareScores(int plumberScore, int saboteurScore) {
        System.out.println("[Game] compareScores() - plumberScore: " + plumberScore + ", saboteurScore: " + saboteurScore);

        // Example threshold for winning (just for skeleton)
        int goalScore = config.getGoalScore();
        if (plumberScore >= goalScore || saboteurScore >= goalScore) {
            determineWinner();
            ensureNoDrawCondition();
            state = GameState.FINALIZED;
            displayFinalResult(plumberScore > saboteurScore ? plumber : saboteur);
        } else {
            System.out.println("[Game] No winner yet - game keeps running");
        }
    }

    public void determineWinner() {
        System.out.println("[Game] determineWinner()");
    }

    public void ensureNoDrawCondition() {
        System.out.println("[Game] ensureNoDrawCondition()");
    }

    public void displayFinalResult(Team winner) {
        System.out.println("[Game] displayFinalResult() - Winner: " + winner.team);
    }  
    
    // UC18

    private int storedWaterTotal = 0;
    private int leakedWaterTotal = 0;
    
    public void simulateWaterFlow() {
        System.out.println("[Game] simulateWaterFlow()");

        // Reset totals at the start of every simulation
        storedWaterTotal = 0;
        leakedWaterTotal = 0;

        List<Spring> springs = getSprings();
        System.out.println("[Game] Found " + springs.size() + " springs.");

        for (Spring sourceSpring : springs) {
            // Ensure water production isn't 0
            int waterAmount = sourceSpring.generateWater();
            if (waterAmount == 0) waterAmount = 500; // Default fallback for skeleton
            
            System.out.println("[Game] Spring generated: " + waterAmount + " units");

            // BFS Queue to trace the flow
            List<Pipe> pipeQueue = new ArrayList<>(sourceSpring.getConnectedPipes());

            while (!pipeQueue.isEmpty()) {
                Pipe activePipe = pipeQueue.remove(0);
                System.out.println("[Game] Processing pipe: " + activePipe);

                // Check 1: Leaking due to sabotage?
                if (activePipe.isBroken()) {
                    System.out.println("[Game] Flow Interrupted: Pipe is broken.");
                    registerLeak(waterAmount);
                } 
                // Check 2: Leaking due to being unplugged?
                else if (activePipe.hasFreeEnd()) {
                    System.out.println("[Game] Flow Interrupted: Pipe has a free end.");
                    registerLeak(waterAmount);
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
                        registerLeak(forwardedAmount);
                    }
                }
            }
        }
        updateFlowTotals();
    }

    private void processPumpFlow(Pump activePump, int amount, List<Pipe> pipeQueue) {
        // Check 1: Pump Health
        if (activePump.isBroken()) {
            System.out.println("[Game] Flow Interrupted: Pump is broken.");
            // In many designs, a broken pump causes water to leak or just stop. 
            // We'll count it as a leak for the Saboteurs.
            registerLeak(amount);
        } 
        // Check 2: Pump Capacity
        else if (activePump.isTankFull()) {
            System.out.println("[Game] Flow Interrupted: Pump tank is full.");
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
                    storedWaterTotal += pumpedAmount; // Score for Plumbers
                    System.out.println("[Game] SUCCESS: Water reached target cistern!");
                }
            }
        }
    }

    private List<Spring> getSprings() {
        List<Spring> springs = new ArrayList<>();
        for (Element e : elements) {
            if (e instanceof Spring) springs.add((Spring) e);
        }
        return springs;
    }

    private void registerLeak(int amount) {
        leakedWaterTotal += amount;
        System.out.println("[Game] registerLeak() - Total leaked so far: " + leakedWaterTotal);
    }

    private void updateFlowTotals() {
        System.out.println("\n[Game] --- Flow Simulation Results ---");
        System.out.println("[Game] Total Water Stored (Plumbers): " + storedWaterTotal);
        System.out.println("[Game] Total Water Leaked (Saboteurs): " + leakedWaterTotal);
        System.out.println("[Game] --------------------------------\n");
    }

}
