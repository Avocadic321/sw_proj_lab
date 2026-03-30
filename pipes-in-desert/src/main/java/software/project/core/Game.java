package software.project.core;

import software.project.models.ActiveElement;
import software.project.models.Cistern;
import software.project.models.Element;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Plumber;
import software.project.models.Player;
import software.project.models.Pump;
import software.project.models.Saboteur;
import software.project.models.Spring;
import software.project.models.Team;
import software.project.utils.GameState;
import software.project.utils.Teams;

import java.util.ArrayList;
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
        System.out.println("[Game] Game() created");
    }

    public Game() {
        this(new GameConfig());
    }

    public void startNewGameCommand() {
        System.out.println("[Game] startNewGameCommand()");
        startGame();
    }

    public void startGame() {
        System.out.println("[Game] startGame()");
        System.out.println("[Game] state=INITIALIZING");
        state = GameState.INITIALIZING;

        prepareGameSession();
        addElement(new Spring());
        addElement(new Cistern());
        addElement(new Pump());

        createTeams();
        setGoalScore(config.getGoalScore());

        turnManager.initialize();

        System.out.println("[Game] state=RUNNING");
        state = GameState.RUNNING;
    }

    public void prepareGameSession() {
        System.out.println("[Game] prepareGameSession()");
    }

    public void createTeams() {
        System.out.println("[Game] createTeams(plumberTeam,saboteurTeam)");
        plumber = new Team(Teams.PLUMBERS);
        saboteur = new Team(Teams.SABOTEURS);

        for (int i = 0; i < config.getNumberOfPlayers(); i++) {
            if (i % 2 == 0) {
                plumber.addPlayer(new Plumber());
            } else {
                saboteur.addPlayer(new Saboteur());
            }
        }
    }

    public void setGoalScore(int targetScore) {
        System.out.println("[Game] setGoalScore(" + targetScore + ")");
        config.setGoalScore(targetScore);
    }

    public void initiateConfiguration() {
        System.out.println("[Game] initiateConfiguration()");
    }

    public void enterTargetScore(int goalScore) {
        System.out.println("[Game] enterTargetScore(" + goalScore + ")");
        setGoalScore(goalScore);
    }

    public void enterTurnDuration(int duration) {
        System.out.println("[Game] enterTurnDuration(" + duration + ")");
        turnManager.setTimerDuration(duration);
    }

    public void setNumberOfPlayers(int playerCount) {
        System.out.println("[Game] setNumberOfPlayers(" + playerCount + ")");
        config.setNumberOfPlayers(playerCount);
    }

    public void setRealtimeScoring(boolean enabled) {
        System.out.println("[Game] setRealtimeScoring(" + enabled + ")");
        storeRealtimeScoringSetting(enabled);
    }

    public void storeRealtimeScoringSetting(boolean enabled) {
        System.out.println("[Game] storeRealtimeScoringSetting(" + enabled + ")");
        config.setRealTimeScoring(enabled);
    }

    public boolean validateConfiguration() {
        System.out.println("[Game] validateConfiguration()");
        return true;
    }

    public void saveConfiguration() {
        System.out.println("[Game] saveConfiguration()");
    }

    public void pauseCommand() {
        System.out.println("[Game] pauseCommand()");
        pauseGame();
    }

    public void resumeCommand() {
        System.out.println("[Game] resumeCommand()");
        resumeGame();
    }

    public void pauseGame() {
        System.out.println("[Game] state=PAUSED");
        state = GameState.PAUSED;
        turnManager.suspendCurrentTurn();
        blockGameplayInteractions();
    }

    public void resumeGame() {
        System.out.println("[Game] state=RUNNING");
        state = GameState.RUNNING;
        turnManager.continueCurrentTurn();
    }

    public void blockGameplayInteractions() {
        System.out.println("[Game] blockGameplayInteractions()");
    }

    public void endGame() {
        System.out.println("[Game] endGame()");
    }

    public void selectTargetAdjacentElement(Element targetElement) {
        System.out.println("[Game] selectTargetAdjacentElement(" + targetElement + ")");
    }

    public boolean moveTo(Player player, Element targetElement) {
        System.out.println("[Game] moveTo(targetElement)");
        Element currentElement = player == null ? null : player.currentPosition;
        isDirectlyConnected(currentElement, targetElement);
        if (targetElement != null) {
            targetElement.canOccupy();
            if (currentElement != null) {
                currentElement.removeOccupant(player);
            }
            targetElement.addOccupant(player);
        }
        return true;
    }

    public boolean isDirectlyConnected(Element currentElement, Element targetElement) {
        System.out.println("[Game] isDirectlyConnected(currentElement,targetElement)");
        return true;
    }

    public Element getElementById(String id) {
        System.out.println("[Game] getElementById(" + id + ")");
        for (Element element : elements) {
            if (element.id != null && element.id.equals(id)) {
                return element;
            }
        }
        return null;
    }

    public void addElement(Element element) {
        System.out.println("[Game] addElement(" + element.getClass().getSimpleName() + ")");
        elements.add(element);
    }

    public void performRandomEvents() {
        System.out.println("[Game] performRandomEvents()");
        List<Pump> pumpsToBreak = selectRandomWorkingPumps();
        for (Pump selectedPump : pumpsToBreak) {
            selectedPump.breakElement();
        }

        List<Cistern> cisternList = getCisterns();
        for (int i = 0; i < cisternList.size(); i++) {
            Cistern targetCistern = cisternList.get(i);
            if (i % 2 == 0) {
                addElement(targetCistern.producePipe());
            } else {
                addElement(targetCistern.producePump());
            }
        }

        updateGameState();
    }

    public void processRandomEvent() {
        System.out.println("[Game] processRandomEvent()");
        performRandomEvents();
    }

    public List<Pump> selectRandomWorkingPumps() {
        System.out.println("[Game] selectRandomWorkingPumps()");
        List<Pump> result = new ArrayList<>();
        for (Element element : elements) {
            if (element instanceof Pump pump && !pump.isBroken()) {
                result.add(pump);
            }
        }
        return result;
    }

    public List<Cistern> getCisterns() {
        System.out.println("[Game] getCisterns()");
        List<Cistern> cisterns = new ArrayList<>();
        for (Element element : elements) {
            if (element instanceof Cistern cistern) {
                cisterns.add(cistern);
            }
        }
        return cisterns;
    }

    public void updateGameState() {
        System.out.println("[Game] updateGameState()");
    }

    public void selectPipe(Pipe targetPipe) {
        System.out.println("[Game] selectPipe(" + targetPipe + ")");
    }

    public void selectFreePipeEnd(PipeEnd freeEnd) {
        System.out.println("[Game] selectFreePipeEnd(" + freeEnd + ")");
    }

    public void selectTargetElement(ActiveElement targetElement) {
        System.out.println("[Game] selectTargetElement(" + targetElement + ")");
    }

    public boolean disconnect(Pipe selectedPipe, PipeEnd selectedEnd) {
        System.out.println("[Game] disconnect(selectedPipe, selectedEnd)");
        if (selectedPipe != null && selectedEnd != null) {
            selectedPipe.disconnect(selectedEnd);
        }
        updatePipeNetworkStructure();
        return true;
    }

    public boolean connect(Pipe selectedPipe, PipeEnd freeEnd, ActiveElement targetElement) {
        System.out.println("[Game] connect(selectedPipe, freeEnd, targetElement)");
        if (freeEnd != null) {
            freeEnd.isFree();
            if (targetElement != null) {
                targetElement.validateConnection(selectedPipe, freeEnd);
                freeEnd.connectTo(targetElement);
            }
        }
        updatePipeNetworkStructure();
        return true;
    }

    public void updatePipeNetworkStructure() {
        System.out.println("[Game] updatePipeNetworkStructure()");
    }

    public boolean insertPumpIntoPipe(Plumber activePlumber, Pipe targetPipe) {
        System.out.println("[Game] insertPumpIntoPipe(targetPipe)");
        if (activePlumber == null || targetPipe == null) {
            return false;
        }

        Object carried = activePlumber.getCarriedItem();
        if (!(carried instanceof Pump carriedPump)) {
            return false;
        }

        Pipe[] splitPipes = targetPipe.splitForPump(carriedPump);
        carriedPump.setDirection(splitPipes[0], splitPipes[1]);
        activePlumber.clearCarriedItem();

        addElement(splitPipes[0]);
        addElement(splitPipes[1]);
        addElement(carriedPump);
        updateConnections();
        return true;
    }

    public void updateConnections() {
        System.out.println("[Game] updateConnections()");
    }

    public void selectPump(Pump targetPump) {
        System.out.println("[Game] selectPump(" + targetPump + ")");
    }

    public void selectInputPipe(Pipe inputPipe) {
        System.out.println("[Game] selectInputPipe(" + inputPipe + ")");
    }

    public void selectOutputPipe(Pipe outputPipe) {
        System.out.println("[Game] selectOutputPipe(" + outputPipe + ")");
    }

    public boolean setPumpDirection(Player player, Pump targetPump, Pipe inputPipe, Pipe outputPipe) {
        System.out.println("[Game] setDirection(inputPipe,outputPipe)");
        if (targetPump == null || inputPipe == null || outputPipe == null) {
            return false;
        }
        return targetPump.setDirection(inputPipe, outputPipe);
    }

    public void selectDamagedPipe(Pipe targetPipe) {
        System.out.println("[Game] selectDamagedPipe(" + targetPipe + ")");
    }

    public boolean repairPipe(Plumber plumberPlayer, Pipe targetPipe) {
        System.out.println("[Game] repairPipe(targetPipe)");
        if (targetPipe == null) {
            return false;
        }
        if (targetPipe.isBroken()) {
            targetPipe.repair();
            return true;
        }
        return false;
    }

    public void selectCistern(Cistern sourceCistern) {
        System.out.println("[Game] selectCistern(" + sourceCistern + ")");
    }

    public boolean requestComponent(Cistern sourceCistern, boolean requestPump) {
        System.out.println("[Game] requestComponent()");
        if (sourceCistern == null) {
            return false;
        }

        Plumber activePlumber = getActivePlayer();
        if (activePlumber == null) {
            return false;
        }

        if (requestPump) {
            activePlumber.setCarriedItem(sourceCistern.producePump());
        } else {
            activePlumber.setCarriedItem(sourceCistern.producePipe());
        }
        return true;
    }

    public Plumber getActivePlayer() {
        System.out.println("[Game] getActivePlayer()");
        if (turnManager.currentPlayer instanceof Plumber activePlumber) {
            return activePlumber;
        }
        return null;
    }

    public void selectBrokenPump(Pump targetPump) {
        System.out.println("[Game] selectBrokenPump(" + targetPump + ")");
    }

    public boolean repairPump(Plumber plumberPlayer, Pump targetPump) {
        System.out.println("[Game] repairPump(targetPump)");
        if (targetPump == null) {
            return false;
        }
        if (targetPump.isBroken()) {
            targetPump.repair();
            return true;
        }
        return false;
    }

    public void sabotagePipe(Pipe targetPipe) {
        System.out.println("[Game] sabotagePipe(targetPipe)");
        if (targetPipe != null && !targetPipe.isBroken()) {
            targetPipe.breakElement();
        }
    }

    public void calculateScore() {
        System.out.println("[Game] calculateScore()");
        simulateWaterFlow();
        Object flowReport = new Object();

        for (Element element : elements) {
            if (element instanceof Cistern cistern) {
                cistern.receiveWater(1);
            }
        }

        int storedWaterTotal = getTotalStoredWater(flowReport);
        int leakedWaterTotal = getTotalLeakedWater(flowReport);

        if (plumber != null) {
            plumber.addScore(storedWaterTotal);
        }
        if (saboteur != null) {
            saboteur.addScore(leakedWaterTotal);
        }

        updateDisplayedScores();
    }

    public int getTotalStoredWater(Object flowReport) {
        System.out.println("[Game] getTotalStoredWater(flowReport)");
        return 1;
    }

    public int getTotalLeakedWater(Object flowReport) {
        System.out.println("[Game] getTotalLeakedWater(flowReport)");
        return 1;
    }

    public void updateDisplayedScores() {
        System.out.println("[Game] updateDisplayedScores()");
    }

    public void checkWinner() {
        System.out.println("[Game] checkWinner()");
        int plumberScore = plumber == null ? 0 : plumber.getScore();
        int saboteurScore = saboteur == null ? 0 : saboteur.getScore();
        compareScores(plumberScore, saboteurScore);
    }

    public void compareScores(int plumberScore, int saboteurScore) {
        System.out.println("[Game] compareScores(plumberScore, saboteurScore)");
        if (plumberScore >= config.getGoalScore() || saboteurScore >= config.getGoalScore()) {
            determineWinner();
            ensureNoDrawCondition();
            System.out.println("[Game] state=FINISHED");
            state = GameState.FINALIZED;
            displayFinalResult(plumberScore >= saboteurScore ? plumber : saboteur);
        }
    }

    public void determineWinner() {
        System.out.println("[Game] determineWinner()");
    }

    public void ensureNoDrawCondition() {
        System.out.println("[Game] ensureNoDrawCondition()");
    }

    public void displayFinalResult(Team winner) {
        System.out.println("[Game] displayFinalResult(winner)");
    }

    public void simulateWaterFlow() {
        System.out.println("[Game] simulateWaterFlow()");

        List<Spring> springList = getSprings();
        for (Spring sourceSpring : springList) {
            int waterAmount = sourceSpring.generateWater();
            List<Pipe> pipeQueue = new ArrayList<>(sourceSpring.getConnectedPipes());

            while (!pipeQueue.isEmpty()) {
                Pipe activePipe = pipeQueue.remove(0);
                if (activePipe.isBroken() || activePipe.hasFreeEnd()) {
                    registerLeak(activePipe);
                    continue;
                }

                int forwardedAmount = activePipe.transferWater(waterAmount);
                Pump activePump = activePipe.getNextPump();
                if (activePump == null) {
                    registerLeak(activePipe);
                    continue;
                }

                if (activePump.isBroken()) {
                    System.out.println("[Game] activePump is broken");
                    continue;
                }

                if (activePump.isTankFull()) {
                    System.out.println("[Game] activePump tank is full");
                    continue;
                }

                int pumpedAmount = activePump.transferWater(forwardedAmount);
                Pipe outgoingPipe = activePump.getOutgoingPipe();
                enqueuePipe(pipeQueue, outgoingPipe);

                if (activePump.isConnectedToCistern()) {
                    Cistern targetCistern = activePump.getTargetCistern();
                    if (targetCistern != null) {
                        targetCistern.receiveWater(pumpedAmount);
                    }
                }
            }
        }

        updateFlowTotals(getTotalStoredWater(null), getTotalLeakedWater(null));
    }

    public List<Spring> getSprings() {
        System.out.println("[Game] getSprings()");
        List<Spring> springs = new ArrayList<>();
        for (Element element : elements) {
            if (element instanceof Spring spring) {
                springs.add(spring);
            }
        }
        return springs;
    }

    public void registerLeak(Pipe activePipe) {
        System.out.println("[Game] registerLeak(activePipe)");
    }

    public void enqueuePipe(List<Pipe> pipeQueue, Pipe outgoingPipe) {
        System.out.println("[Game] enqueuePipe(outgoingPipe)");
        if (outgoingPipe != null) {
            pipeQueue.add(outgoingPipe);
        }
    }

    public void updateFlowTotals(int storedWaterTotal, int leakedWaterTotal) {
        System.out.println("[Game] updateFlowTotals(storedWaterTotal, leakedWaterTotal)");
    }
}
