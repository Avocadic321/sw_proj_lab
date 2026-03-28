package software.project.models;

import software.project.utils.Teams;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public Teams team;
    public List<Player> players;
    public int score;

    public void addPlayer(Player player) {
        System.out.println("[Team] addPlayer()");
    }

    public void removePlayer(Player player) {
        System.out.println("[Team] removePlayer()");
    }

    public int getScore() {
        System.out.println("[Team] getScore()");
        return score;
    }
    public void addScore(int score) {
        System.out.printf("[Team] addScore(%d)\n", score);
        this.score += score;
    }

}
