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

    public Game() {
        this.elements = new ArrayList<>();
        this.turnManager = new TurnManager();
        this.state = GameState.INITIALIZING;
        this.config = new GameConfig();
        System.out.println("[Game] Game object created");
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
    }
}
