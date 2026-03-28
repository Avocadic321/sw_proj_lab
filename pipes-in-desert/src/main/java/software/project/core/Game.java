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
    public int goalScore;

    public Game() {
        this.elements = new ArrayList<>();
        this.turnManager = new TurnManager();
        this.state = GameState.START;
        this.goalScore = 0;
        System.out.println("[Game] Game object created");
    }

    public void startGame() {
        System.out.println("[Game] startGame()\n");
    }

    public void pauseGame() {
        System.out.println("[Game] pauseGame()\n");
    }

    public void resumeGame() {
        System.out.println("[Game] resumeGame()\n");
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

    public boolean setGoalScore(int score) {
        System.out.printf("[Game] setGoalScore(%d)%n", score);

        if (score > 0) {
            this.goalScore = score;
            return true;
        } else {
            return false;
        }
    }
}
