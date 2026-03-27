package software.project.models;

import software.project.utils.Teams;

import java.util.List;

public class Team {
    public Teams team;
    public List<Player> players;
    public int score;

    public void addPlayer(Player player) {}
    public void removePlayer(Player player){}
    public int getScore() {
        return score;
    }
    public void addScore(int score) {
        this.score += score;
    }

}
