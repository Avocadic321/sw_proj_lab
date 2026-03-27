package software.project.core;

import software.project.models.Element;
import software.project.models.Team;
import software.project.utils.GameState;

import java.util.List;

public class Game {
    public TurnManager turnManager;
    public List<Element> elements;
    public Team saboteur;
    public Team plumber;
    public GameState state;
    public int goalScore;
    public void startGame(){}
    public void pauseGame(){}
    public void resumeGame() {}
    public void endGame() {}
    public void checkWinner(){}
    public void nextTurn(){}
    public void performRandomEvents() {}
    public void simulateWaterFlow() {}
    public void addElement(Element element){}
    public void setGoalScore() {}
}
