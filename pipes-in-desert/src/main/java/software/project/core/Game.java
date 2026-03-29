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
        if (!(state == GameState.PAUSED)) {
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

    public void nextTurn() {
        System.out.println("[Game] nextTurn()\n");
    }

    public void performRandomEvents() {
        System.out.println("[Game] performRandomEvents()\n");
    }

    public void simulateWaterFlow() {
        System.out.println("[Game] simulateWaterFlow()\n");
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
}
