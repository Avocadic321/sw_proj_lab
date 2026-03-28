package software.project.models;

import software.project.utils.Teams;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public Teams team;
    public List<Player> players;
    public int score;

    public Team(Teams team) {
        System.out.printf("[Team] Team(%s)%n",  team.toString());
        this.players = new ArrayList<>();
        this.team = team;

        System.out.println("[Team] initializeScore(0)");
        this.score = 0;
    }

    public void addPlayer(Player player) {
        System.out.printf("[Team] addPlayer() - Team %s%n", team.toString());
        players.add(player);
    }

    public void removePlayer(Player player) {
        System.out.println("[Team] removePlayer()");
        players.remove(player);
    }

    public int getScore() {
        System.out.println("[Team] getScore()");
        return score;
    }
    public void addScore(int score) {
        System.out.printf("[Team] addScore(%d)%n", score);
        this.score += score;
    }

}
