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
import software.project.models.Saboteur;
import software.project.models.Spring;
import software.project.models.Team;
import software.project.utils.GameState;
import software.project.utils.Teams;

public class GameLogic {
    // Domain-oriented logic extracted from the Game facade.
    private void log(String message) {
        System.out.println("[GameLogic] " + message);
    }

    public void startGame(Game game) {
        log("startGame()");
        game.state = GameState.INITIALIZING;
        game.renderer.renderState(game.state);

        prepareGameSession(game);
        addElement(game, new Spring());
        addElement(game, new Cistern());
        addElement(game, new Pump());

        createTeams(game);
        game.config.setGoalScore(game.config.getGoalScore());
        game.turnManager.initialize();

        game.state = GameState.RUNNING;
        game.renderer.renderState(game.state);
        game.audioSystem.playGameStart();
    }

    public void prepareGameSession(Game game) {
        log("prepareGameSession()");
    }

    public void createTeams(Game game) {
        log("createTeams(plumberTeam,saboteurTeam)");
        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        for (int i = 0; i < game.config.getNumberOfPlayers(); i++) {
            if (i % 2 == 0) {
                game.plumber.addPlayer(new Plumber());
            } else {
                game.saboteur.addPlayer(new Saboteur());
            }
        }
    }

    public void setGoalScore(Game game, int targetScore) {
        log("setGoalScore(" + targetScore + ")");
        game.config.setGoalScore(targetScore);
    }

    public void enterTargetScore(Game game, int goalScore) {
        log("enterTargetScore(" + goalScore + ")");
        setGoalScore(game, goalScore);
    }

    public void enterTurnDuration(Game game, int duration) {
        log("enterTurnDuration(" + duration + ")");
        game.turnManager.setTimerDuration(duration);
    }

    public void setNumberOfPlayers(Game game, int playerCount) {
        log("setNumberOfPlayers(" + playerCount + ")");
        game.config.setNumberOfPlayers(playerCount);
    }

    public void setRealtimeScoring(Game game, boolean enabled) {
        log("setRealtimeScoring(" + enabled + ")");
        storeRealtimeScoringSetting(game, enabled);
    }

    public void storeRealtimeScoringSetting(Game game, boolean enabled) {
        log("storeRealtimeScoringSetting(" + enabled + ")");
        game.config.setRealTimeScoring(enabled);
    }

    public void saveConfiguration(Game game) {
        log("saveConfiguration()");
    }

    public void pauseGame(Game game) {
        game.state = GameState.PAUSED;
        game.renderer.renderState(game.state);
        game.turnManager.suspendCurrentTurn();
        blockGameplayInteractions(game);
        game.audioSystem.playPause();
    }

    public void resumeGame(Game game) {
        game.state = GameState.RUNNING;
        game.renderer.renderState(game.state);
        game.turnManager.continueCurrentTurn();
        game.audioSystem.playResume();
    }

    public void blockGameplayInteractions(Game game) {
        log("blockGameplayInteractions()");
    }

    public void endGame(Game game) {
        log("endGame()");
        game.audioSystem.playEndGame();
    }

    public boolean moveTo(Game game, Plumber activePlumber, Element targetElement) {
        log("moveTo(targetElement)");
        if (activePlumber == null) {
            return false;
        }

        Element currentElement = activePlumber.currentPosition;
        boolean connected = isDirectlyConnected(currentElement, targetElement);
        boolean canMove = canMoveTo(targetElement);
        if (!connected || !canMove) {
            return false;
        }

        if (currentElement != null) {
            currentElement.removeOccupant(activePlumber);
        }
        targetElement.addOccupant(activePlumber);
        activePlumber.currentPosition = targetElement;
        return true;
    }

    public Element getElementById(Game game, String id) {
        log("getElementById(" + id + ")");
        for (Element element : game.elements) {
            if (element.id != null && element.id.equals(id)) {
                return element;
            }
        }
        return null;
    }

    public void addElement(Game game, Element element) {
        log("addElement(" + element.getClass().getSimpleName() + ")");
        game.elements.add(element);
    }

    public void performRandomEvents(Game game) {
        log("performRandomEvents()");
        List<Pump> pumpsToBreak = selectRandomWorkingPumps(game);
        for (Pump selectedPump : pumpsToBreak) {
            selectedPump.breakElement();
        }

        List<Cistern> cisternList = getCisterns(game);
        for (int i = 0; i < cisternList.size(); i++) {
            Cistern targetCistern = cisternList.get(i);
            if (i % 2 == 0) {
                addElement(game, targetCistern.producePipe());
            } else {
                addElement(game, targetCistern.producePump());
            }
        }

        updateGameState(game);
        game.audioSystem.playRandomEvent();
    }

    public List<Pump> selectRandomWorkingPumps(Game game) {
        log("selectRandomWorkingPumps()");
        List<Pump> result = new ArrayList<>();
        for (Element element : game.elements) {
            if (element instanceof Pump pump && !pump.isBroken()) {
                result.add(pump);
            }
        }
        return result;
    }

    public List<Cistern> getCisterns(Game game) {
        log("getCisterns()");
        List<Cistern> cisterns = new ArrayList<>();
        for (Element element : game.elements) {
            if (element instanceof Cistern cistern) {
                cisterns.add(cistern);
            }
        }
        return cisterns;
    }

    public void updateGameState(Game game) {
        log("updateGameState()");
    }

    public boolean disconnect(Game game, Pipe selectedPipe, PipeEnd selectedEnd) {
        log("disconnect(selectedPipe, selectedEnd)");
        if (selectedPipe != null && selectedEnd != null) {
            selectedPipe.disconnect(selectedEnd);
        }
        updatePipeNetworkStructure(game);
        return true;
    }

    public boolean connect(Game game, Pipe selectedPipe, PipeEnd freeEnd, ActiveElement targetElement) {
        log("connect(selectedPipe, freeEnd, targetElement)");
        if (freeEnd != null) {
            freeEnd.isFree();
            if (targetElement != null) {
                targetElement.validateConnection(selectedPipe, freeEnd);
                freeEnd.connectTo(targetElement);
            }
        }
        updatePipeNetworkStructure(game);
        return true;
    }

    public void updatePipeNetworkStructure(Game game) {
        log("updatePipeNetworkStructure()");
    }

    public boolean insertPumpIntoPipe(Game game, Plumber activePlumber, Pipe targetPipe) {
        log("insertPumpIntoPipe(targetPipe)");
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

        addElement(game, splitPipes[0]);
        addElement(game, splitPipes[1]);
        addElement(game, carriedPump);
        updateConnections(game);
        return true;
    }

    public void updateConnections(Game game) {
        log("updateConnections()");
    }

    public boolean setPumpDirection(Game game, Plumber player, Pump targetPump, Pipe inputPipe, Pipe outputPipe) {
        log("setDirection(inputPipe,outputPipe)");
        if (!isValidPumpDirection(player)) {
            return false;
        }
        if (targetPump == null || inputPipe == null || outputPipe == null) {
            return false;
        }
        return targetPump.setDirection(inputPipe, outputPipe);
    }

    public boolean repairPipe(Game game, Pipe targetPipe) {
        log("repairPipe(targetPipe)");
        if (targetPipe == null) {
            return false;
        }
        if (targetPipe.isBroken()) {
            targetPipe.repair();
            game.audioSystem.playRepair();
            return true;
        }
        return false;
    }

    public boolean requestComponent(Game game, Cistern sourceCistern, boolean requestPump) {
        log("requestComponent()");
        if (sourceCistern == null) {
            return false;
        }

        Plumber activePlumber = getActivePlumber(game.turnManager);
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

    public boolean repairPump(Game game, Pump targetPump) {
        log("repairPump(targetPump)");
        if (targetPump == null) {
            return false;
        }
        if (targetPump.isBroken()) {
            targetPump.repair();
            game.audioSystem.playRepair();
            return true;
        }
        return false;
    }

    public void sabotagePipe(Game game, Pipe targetPipe) {
        log("sabotagePipe(targetPipe)");
        if (targetPipe != null && !targetPipe.isBroken()) {
            targetPipe.breakElement();
            game.audioSystem.playSabotage();
        }
    }

    public void calculateScore(Game game) {
        log("calculateScore()");
        simulateWaterFlow(game);
        Object flowReport = new Object();

        for (Element element : game.elements) {
            if (element instanceof Cistern cistern) {
                cistern.receiveWater(1);
            }
        }

        int storedWaterTotal = getTotalStoredWater(game, flowReport);
        int leakedWaterTotal = getTotalLeakedWater(game, flowReport);

        if (game.plumber != null) {
            game.plumber.addScore(storedWaterTotal);
        }
        if (game.saboteur != null) {
            game.saboteur.addScore(leakedWaterTotal);
        }

        updateDisplayedScores(game);
        game.audioSystem.playScoreUpdate();
    }

    public int getTotalStoredWater(Game game, Object flowReport) {
        log("getTotalStoredWater(flowReport)");
        return 1;
    }

    public int getTotalLeakedWater(Game game, Object flowReport) {
        log("getTotalLeakedWater(flowReport)");
        return 1;
    }

    public void updateDisplayedScores(Game game) {
        game.renderer.renderDisplayedScores(game.plumber, game.saboteur);
    }

    public void checkWinner(Game game) {
        log("checkWinner()");
        int plumberScore = game.plumber == null ? 0 : game.plumber.getScore();
        int saboteurScore = game.saboteur == null ? 0 : game.saboteur.getScore();
        compareScores(game, plumberScore, saboteurScore);
    }

    public void compareScores(Game game, int plumberScore, int saboteurScore) {
        log("compareScores(plumberScore, saboteurScore)");
        if (plumberScore >= game.config.getGoalScore() || saboteurScore >= game.config.getGoalScore()) {
            determineWinner(game);
            ensureNoDrawCondition(game);
            game.state = GameState.FINALIZED;
            game.renderer.renderState(game.state);
            displayFinalResult(game, plumberScore >= saboteurScore ? game.plumber : game.saboteur);
            game.audioSystem.playEndGame();
        }
    }

    public void determineWinner(Game game) {
        log("determineWinner()");
    }

    public void ensureNoDrawCondition(Game game) {
        log("ensureNoDrawCondition()");
    }

    public void displayFinalResult(Game game, Team winner) {
        game.renderer.renderFinalResult(winner);
    }

    public void simulateWaterFlow(Game game) {
        log("simulateWaterFlow()");

        List<Spring> springList = getSprings(game);
        for (Spring sourceSpring : springList) {
            int waterAmount = sourceSpring.generateWater();
            List<Pipe> pipeQueue = new ArrayList<>(sourceSpring.getConnectedPipes());

            while (!pipeQueue.isEmpty()) {
                Pipe activePipe = pipeQueue.remove(0);
                if (activePipe.isBroken() || activePipe.hasFreeEnd()) {
                    registerLeak(game, activePipe);
                    continue;
                }

                int forwardedAmount = activePipe.transferWater(waterAmount);
                Pump activePump = activePipe.getNextPump();
                if (activePump == null) {
                    registerLeak(game, activePipe);
                    continue;
                }

                if (activePump.isBroken()) {
                    log("activePump is broken");
                    continue;
                }

                if (activePump.isTankFull()) {
                    log("activePump tank is full");
                    continue;
                }

                int pumpedAmount = activePump.transferWater(forwardedAmount);
                Pipe outgoingPipe = activePump.getOutgoingPipe();
                enqueuePipe(game, pipeQueue, outgoingPipe);

                if (activePump.isConnectedToCistern()) {
                    Cistern targetCistern = activePump.getTargetCistern();
                    if (targetCistern != null) {
                        targetCistern.receiveWater(pumpedAmount);
                    }
                }
            }
        }

        updateFlowTotals(game, getTotalStoredWater(game, null), getTotalLeakedWater(game, null));
    }

    public List<Spring> getSprings(Game game) {
        log("getSprings()");
        List<Spring> springs = new ArrayList<>();
        for (Element element : game.elements) {
            if (element instanceof Spring spring) {
                springs.add(spring);
            }
        }
        return springs;
    }

    public void registerLeak(Game game, Pipe activePipe) {
        log("registerLeak(activePipe)");
    }

    public void enqueuePipe(Game game, List<Pipe> pipeQueue, Pipe outgoingPipe) {
        log("enqueuePipe(outgoingPipe)");
        if (outgoingPipe != null) {
            pipeQueue.add(outgoingPipe);
        }
    }

    public void updateFlowTotals(Game game, int storedWaterTotal, int leakedWaterTotal) {
        log("updateFlowTotals(storedWaterTotal, leakedWaterTotal)");
    }

    public boolean validateConfiguration(GameConfig config) {
        log("validateConfiguration()");
        return config != null;
    }

    public boolean isDirectlyConnected(Element currentElement, Element targetElement) {
        log("isDirectlyConnected(currentElement,targetElement)");
        return currentElement != null || targetElement != null;
    }

    public boolean canMoveTo(Element targetElement) {
        log("canMoveTo(targetElement)");
        return targetElement != null && targetElement.canOccupy();
    }

    public Plumber getActivePlumber(TurnManager turnManager) {
        log("getActivePlumber()");
        if (turnManager != null && turnManager.currentPlayer instanceof Plumber activePlumber) {
            return activePlumber;
        }
        return null;
    }

    public Player getActivePlayer(TurnManager turnManager) {
        log("getActivePlayer()");
        if (turnManager != null && turnManager.currentPlayer != null) {
            return turnManager.currentPlayer;
        }
        return null;
    }

    public boolean isValidPumpDirection(Player player) {
        log("isValidPumpDirection(player)");
        return player != null;
    }
}
