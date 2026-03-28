package software.project.core;

import software.project.models.*;
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
    public Pipe selectedPipe;
    public PipeEnd selectedFreePipeEnd;
    public Element selectedTargetElement;
    public Pump selectedPump;
    public Pipe selectedInputPipe;
    public Pipe selectedOutputPipe;
    public Cistern selectedCistern;

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

    public void checkWinner() {
        System.out.println("[Game] checkWinner()\n");
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
        System.out.println("[Game] addElement() - " + element.getClass().getSimpleName());
        elements.add(element);
    }

    public Pipe selectPipe(Pipe pipe) {
        System.out.println("[Game] selectPipe(selectedPipe)");
        this.selectedPipe = pipe;
        return this.selectedPipe;
    }

    public Pipe selectDamagedPipe(Pipe targetPipe) {
        System.out.println("[Game] selectDamagedPipe(targetPipe)");
        this.selectedPipe = targetPipe;
        return this.selectedPipe;
    }

    public Pump selectPump(Pump targetPump) {
        System.out.println("[Game] selectPump(targetPump)");
        this.selectedPump = targetPump;
        return this.selectedPump;
    }

    public Pump selectBrokenPump(Pump targetPump) {
        System.out.println("[Game] selectBrokenPump(targetPump)");
        this.selectedPump = targetPump;
        return this.selectedPump;
    }

    public PipeEnd selectFreePipeEnd(PipeEnd freeEnd) {
        System.out.println("[Game] selectFreePipeEnd(freeEnd)");
        this.selectedFreePipeEnd = freeEnd;
        return this.selectedFreePipeEnd;
    }

    public Element selectTargetElement(Element targetElement) {
        System.out.println("[Game] selectTargetElement(targetElement)");
        this.selectedTargetElement = targetElement;
        return this.selectedTargetElement;
    }

    public Pipe selectInputPipe(Pipe inputPipe) {
        System.out.println("[Game] selectInputPipe(inputPipe)");
        this.selectedInputPipe = inputPipe;
        return this.selectedInputPipe;
    }

    public Pipe selectOutputPipe(Pipe outputPipe) {
        System.out.println("[Game] selectOutputPipe(outputPipe)");
        this.selectedOutputPipe = outputPipe;
        return this.selectedOutputPipe;
    }

    public Cistern selectCistern(Cistern sourceCistern) {
        System.out.println("[Game] selectCistern(sourceCistern)");
        this.selectedCistern = sourceCistern;
        return this.selectedCistern;
    }

    public boolean disconnect(Pipe selectedPipe, PipeEnd selectedEnd) {
        System.out.println("[Game] disconnect(selectedPipe, selectedEnd)");
        if (selectedPipe == null || selectedEnd == null) {
            System.out.println("[Game] disconnect() - Invalid target");
            return false;
        }

        boolean endpointDisconnected = selectedPipe.disconnect(selectedEnd);
        if (!endpointDisconnected) {
            System.out.println("[Game] disconnect() - endpointDisconnected = false");
            return false;
        }

        updatePipeNetworkStructure();
        System.out.println("[Game] disconnectApplied");
        return true;
    }

    public void updatePipeNetworkStructure() {
        System.out.println("[Game] updatePipeNetworkStructure()");
    }

    public boolean connect(Pipe selectedPipe, PipeEnd freeEnd, Element targetElement) {
        System.out.println("[Game] connect(selectedPipe, freeEnd, targetElement)");
        if (selectedPipe == null || freeEnd == null || targetElement == null) {
            System.out.println("[Game] connect() - rejected");
            return false;
        }

        boolean isFree = freeEnd.isFree();
        if (!isFree) {
            System.out.println("[Game] connect() - rejected");
            return false;
        }

        boolean isValidConnection = targetElement.validateConnection(selectedPipe, freeEnd);
        if (!isValidConnection) {
            System.out.println("[Game] connect() - rejected");
            return false;
        }

        boolean connected = freeEnd.connectTo(targetElement);
        if (!connected) {
            System.out.println("[Game] connect() - rejected");
            return false;
        }

        updatePipeNetworkStructure();
        System.out.println("[Game] connect() - success");
        return true;
    }

    public boolean insertPumpIntoPipe(Plumber activePlumber, Pipe targetPipe) {
        System.out.println("[Game] insertPumpIntoPipe(targetPipe)");
        if (activePlumber == null || targetPipe == null) {
            System.out.println("[Game] insertPumpIntoPipe() - rejected");
            return false;
        }

        Pump carriedPump = activePlumber.getCarriedItem();
        if (carriedPump == null) {
            System.out.println("[Game] insertPumpIntoPipe() - rejected");
            return false;
        }

        Pipe[] splitPipes = targetPipe.splitForPump(carriedPump);
        if (splitPipes == null || splitPipes.length < 2) {
            System.out.println("[Game] insertPumpIntoPipe() - rejected");
            return false;
        }

        Pipe leftPipe = splitPipes[0];
        Pipe rightPipe = splitPipes[1];
        if (leftPipe.end2 != null) {
            leftPipe.end2.connectsTo(carriedPump);
            carriedPump.connect(leftPipe.end2);
        }
        if (rightPipe.end1 != null) {
            rightPipe.end1.connectsTo(carriedPump);
            carriedPump.connect(rightPipe.end1);
        }

        carriedPump.setDirection(leftPipe, rightPipe);
        activePlumber.clearCarriedItem();

        elements.remove(targetPipe);
        addElement(leftPipe);
        addElement(rightPipe);
        addElement(carriedPump);

        updateConnections();
        System.out.println("[Game] insertionComplete");
        return true;
    }

    public void updateConnections() {
        System.out.println("[Game] updateConnections()");
    }

    public boolean setDirection(Pipe inputPipe, Pipe outputPipe) {
        System.out.println("[Game] setDirection(inputPipe,outputPipe)");
        if (selectedPump == null || inputPipe == null || outputPipe == null) {
            System.out.println("[Game] setDirection() - rejected");
            return false;
        }

        selectedPump.setDirection(inputPipe, outputPipe);
        boolean isValid = selectedPump.validateSingleInputOutput(inputPipe, outputPipe);
        if (!isValid) {
            System.out.println("[Game] setDirection() - rejected");
            return false;
        }

        selectedPump.storeDirectionConfiguration();
        System.out.println("[Game] setDirection() - success");
        return true;
    }

    public boolean repairPipe(Pipe targetPipe) {
        System.out.println("[Game] repairPipe(targetPipe)");
        if (targetPipe == null) {
            System.out.println("[Game] repairPipe() - noChange");
            return false;
        }

        boolean broken = targetPipe.isBroken();
        if (!broken) {
            System.out.println("[Game] repairPipe() - noChange");
            return false;
        }

        targetPipe.repair();
        System.out.println("[Game] repairPipe() - repaired");
        return true;
    }

    public Plumber getActivePlayer() {
        System.out.println("[Game] getActivePlayer()");
        if (turnManager.currentPlayer instanceof Plumber activePlumber) {
            return activePlumber;
        }

        if (plumber != null && plumber.players != null && !plumber.players.isEmpty() && plumber.players.get(0) instanceof Plumber activePlumber) {
            turnManager.currentPlayer = activePlumber;
            return activePlumber;
        }

        return null;
    }

    public boolean requestComponent() {
        System.out.println("[Game] requestComponent()");
        if (selectedCistern == null) {
            System.out.println("[Game] requestComponent() - rejected");
            return false;
        }

        Plumber activePlumber = getActivePlayer();
        if (activePlumber == null) {
            System.out.println("[Game] requestComponent() - rejected");
            return false;
        }

        if (activePlumber.getCarriedComponent() != null) {
            System.out.println("[Game] requestComponent() - rejected");
            return false;
        }

        if (selectedCistern.canProducePipe()) {
            Pipe newPipe = selectedCistern.producePipe();
            if (newPipe == null) {
                System.out.println("[Game] requestComponent() - rejected");
                return false;
            }
            activePlumber.setCarriedItem(newPipe);
        } else if (selectedCistern.canProducePump()) {
            Pump newPump = selectedCistern.producePump();
            if (newPump == null) {
                System.out.println("[Game] requestComponent() - rejected");
                return false;
            }
            activePlumber.setCarriedItem(newPump);
        } else {
            System.out.println("[Game] requestComponent() - rejected");
            return false;
        }

        System.out.println("[Game] componentReceived");
        return true;
    }

    public boolean repairPump(Pump targetPump) {
        System.out.println("[Game] repairPump(targetPump)");
        if (targetPump == null) {
            System.out.println("[Game] repairPump() - noChange");
            return false;
        }

        boolean broken = targetPump.isBroken();
        if (!broken) {
            System.out.println("[Game] repairPump() - noChange");
            return false;
        }

        targetPump.repair();
        System.out.println("[Game] repairPump() - repaired");
        return true;
    }
}
