package software.project.core;

import java.util.ArrayList;
import java.util.List;

import software.project.models.ActiveElement;
import software.project.models.Cistern;
import software.project.models.Element;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.models.Spring;
import software.project.models.Team;
import software.project.utils.GameState;

public class Game {
    // Facade class delegating responsibilities to subsystem components.
    public TurnManager turnManager;
    public List<Element> elements;
    public Team saboteur;
    public Team plumber;
    public GameState state;
    public GameConfig config;

    final Renderer renderer;
    final AudioSystem audioSystem;
    final InputSystem inputSystem;
    final GameLogic gameLogic;

    private void log(String message) {
        System.out.println("[Game] " + message);
    }

    public Game(GameConfig config) {
        this.elements = new ArrayList<>();
        this.turnManager = new TurnManager();
        this.state = GameState.INITIALIZING;
        this.config = config;

        this.renderer = new Renderer();
        this.audioSystem = new AudioSystem();
        this.inputSystem = new InputSystem();
        this.gameLogic = new GameLogic();

        log("Game() created");
    }

    public Game() {
        this(new GameConfig());
    }

    public void startNewGameCommand() {
        inputSystem.startNewGameCommand(this);
    }

    public void startGame() {
        gameLogic.startGame(this);
    }

    public void prepareGameSession() {
        gameLogic.prepareGameSession(this);
    }

    public void createTeams() {
        gameLogic.createTeams(this);
    }

    public void setGoalScore(int targetScore) {
        gameLogic.setGoalScore(this, targetScore);
    }

    public void initiateConfiguration() {
        inputSystem.initiateConfiguration();
    }

    public void enterTargetScore(int goalScore) {
        gameLogic.enterTargetScore(this, goalScore);
    }

    public void enterTurnDuration(int duration) {
        gameLogic.enterTurnDuration(this, duration);
    }

    public void setNumberOfPlayers(int playerCount) {
        gameLogic.setNumberOfPlayers(this, playerCount);
    }

    public void setRealtimeScoring(boolean enabled) {
        gameLogic.setRealtimeScoring(this, enabled);
    }

    public void storeRealtimeScoringSetting(boolean enabled) {
        gameLogic.storeRealtimeScoringSetting(this, enabled);
    }

    public boolean validateConfiguration() {
        return gameLogic.validateConfiguration(this.config);
    }

    public void saveConfiguration() {
        gameLogic.saveConfiguration(this);
    }

    public void pauseCommand() {
        inputSystem.pauseCommand(this);
    }

    public void resumeCommand() {
        inputSystem.resumeCommand(this);
    }

    public void pauseGame() {
        gameLogic.pauseGame(this);
    }

    public void resumeGame() {
        gameLogic.resumeGame(this);
    }

    public void blockGameplayInteractions() {
        gameLogic.blockGameplayInteractions(this);
    }

    public void endGame() {
        gameLogic.endGame(this);
    }

    public void selectTargetAdjacentElement(Element targetElement) {
        inputSystem.selectTargetAdjacentElement(targetElement);
    }

    public boolean moveTo(Player player, Element targetElement) {
        if (!(player instanceof Plumber activePlumber)) {
            log("moveTo rejected: player is not a Plumber");
            return false;
        }
        return gameLogic.moveTo(this, activePlumber, targetElement);
    }

    public boolean isDirectlyConnected(Element currentElement, Element targetElement) {
        return gameLogic.isDirectlyConnected(currentElement, targetElement);
    }

    public Element getElementById(String id) {
        return gameLogic.getElementById(this, id);
    }

    public void addElement(Element element) {
        gameLogic.addElement(this, element);
    }

    public void performRandomEvents() {
        gameLogic.performRandomEvents(this);
    }

    public void processRandomEvent() {
        log("processRandomEvent()");
        performRandomEvents();
    }

    public List<Pump> selectRandomWorkingPumps() {
        return gameLogic.selectRandomWorkingPumps(this);
    }

    public List<Cistern> getCisterns() {
        return gameLogic.getCisterns(this);
    }

    public void updateGameState() {
        gameLogic.updateGameState(this);
    }

    public void selectPipe(Pipe targetPipe) {
        inputSystem.selectPipe(targetPipe);
    }

    public void selectFreePipeEnd(PipeEnd freeEnd) {
        inputSystem.selectFreePipeEnd(freeEnd);
    }

    public void selectTargetElement(ActiveElement targetElement) {
        inputSystem.selectTargetElement(targetElement);
    }

    public boolean disconnect(Pipe selectedPipe, PipeEnd selectedEnd) {
        return gameLogic.disconnect(this, selectedPipe, selectedEnd);
    }

    public boolean connect(Pipe selectedPipe, PipeEnd freeEnd, ActiveElement targetElement) {
        return gameLogic.connect(this, selectedPipe, freeEnd, targetElement);
    }

    public void updatePipeNetworkStructure() {
        gameLogic.updatePipeNetworkStructure(this);
    }

    public boolean insertPumpIntoPipe(Plumber activePlumber, Pipe targetPipe) {
        return gameLogic.insertPumpIntoPipe(this, activePlumber, targetPipe);
    }

    public void updateConnections() {
        gameLogic.updateConnections(this);
    }

    public void selectPump(Pump targetPump) {
        inputSystem.selectPump(targetPump);
    }

    public void selectInputPipe(Pipe inputPipe) {
        inputSystem.selectInputPipe(inputPipe);
    }

    public void selectOutputPipe(Pipe outputPipe) {
        inputSystem.selectOutputPipe(outputPipe);
    }

    public boolean setPumpDirection(Player player, Pump targetPump, Pipe inputPipe, Pipe outputPipe) {
        if (!(player instanceof Plumber activePlumber)) {
            log("setPumpDirection rejected: player is not a Plumber");
            return false;
        }
        return gameLogic.setPumpDirection(this, activePlumber, targetPump, inputPipe, outputPipe);
    }

    public void selectDamagedPipe(Pipe targetPipe) {
        inputSystem.selectDamagedPipe(targetPipe);
    }

    public boolean repairPipe(Plumber plumberPlayer, Pipe targetPipe) {
        return gameLogic.repairPipe(this, targetPipe);
    }

    public void selectCistern(Cistern sourceCistern) {
        inputSystem.selectCistern(sourceCistern);
    }

    public boolean requestComponent(Cistern sourceCistern, boolean requestPump) {
        return gameLogic.requestComponent(this, sourceCistern, requestPump);
    }

    public Player getActivePlayer() {
        return gameLogic.getActivePlumber(turnManager);
    }

    public void selectBrokenPump(Pump targetPump) {
        inputSystem.selectBrokenPump(targetPump);
    }

    public boolean repairPump(Plumber plumberPlayer, Pump targetPump) {
        return gameLogic.repairPump(this, targetPump);
    }

    public void sabotagePipe(Pipe targetPipe) {
        gameLogic.sabotagePipe(this, targetPipe);
    }

    public void calculateScore() {
        gameLogic.calculateScore(this);
    }

    public int getTotalStoredWater(Object flowReport) {
        return gameLogic.getTotalStoredWater(this, flowReport);
    }

    public int getTotalLeakedWater(Object flowReport) {
        return gameLogic.getTotalLeakedWater(this, flowReport);
    }

    public void updateDisplayedScores() {
        gameLogic.updateDisplayedScores(this);
    }

    public void checkWinner() {
        gameLogic.checkWinner(this);
    }

    public void compareScores(int plumberScore, int saboteurScore) {
        gameLogic.compareScores(this, plumberScore, saboteurScore);
    }

    public void determineWinner() {
        gameLogic.determineWinner(this);
    }

    public void ensureNoDrawCondition() {
        gameLogic.ensureNoDrawCondition(this);
    }

    public void displayFinalResult(Team winner) {
        gameLogic.displayFinalResult(this, winner);
    }

    public void simulateWaterFlow() {
        gameLogic.simulateWaterFlow(this);
    }

    public List<Spring> getSprings() {
        return gameLogic.getSprings(this);
    }

    public void registerLeak(Pipe activePipe) {
        gameLogic.registerLeak(this, activePipe);
    }

    public void enqueuePipe(List<Pipe> pipeQueue, Pipe outgoingPipe) {
        gameLogic.enqueuePipe(this, pipeQueue, outgoingPipe);
    }

    public void updateFlowTotals(int storedWaterTotal, int leakedWaterTotal) {
        gameLogic.updateFlowTotals(this, storedWaterTotal, leakedWaterTotal);
    }
}
