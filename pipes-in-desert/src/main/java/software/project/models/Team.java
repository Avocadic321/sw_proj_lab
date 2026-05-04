package software.project.models;

import java.util.ArrayList;
import java.util.List;

import software.project.utils.Teams;

/**
 * Represents one of the two competing groups in the game: the plumbers or the
 * saboteurs.
 * <p>
 * A team aggregates all players belonging to that side and maintains the team's
 * collective
 * score. The class manages its members (adding or removing players) and tracks
 * the team's
 * progress toward victory by accumulating points from water successfully
 * delivered
 * (for plumbers) or leaked (for saboteurs).
 * </p>
 * <p>
 * Each team is identified by its {@link Teams} enumeration value, which
 * determines
 * the role-specific rules that apply to its members. The score is updated
 * incrementally
 * as water either reaches cisterns (plumber team) or leaks into the desert
 * (saboteur team).
 * </p>
 *
 * @see Player
 * @see Teams
 * @since 1.0
 */
public class Team {
    private final List<Player> players = new ArrayList<>();
    private final Teams team;
    private int score = 0;

    /**
     * Constructs a new team with the specified team type.
     * <p>
     * Initializes an empty player list and sets the initial score to zero.
     * The team is identified by the given {@link Teams} enumeration value.
     * </p>
     *
     * @param team the team type identifier
     */
    public Team(Teams team) {
        this.team = team;
    }

    /**
     * Adds a new player to the team.
     * <p>
     * This method ensures the player is added to the team's member list.
     * The caller is responsible for verifying that the player's role
     * matches the team type before invocation.
     * </p>
     *
     * @param player the player to add to this team
     */
    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (team == Teams.PLUMBERS && !(player instanceof Plumber)) {
            throw new IllegalArgumentException("Player does not match the team type");
        }

        if (team == Teams.SABOTEURS && !(player instanceof Saboteur)) {
            throw new IllegalArgumentException("Player does not match the team type");
        }

        players.add(player);
    }

    /**
     * Removes a player from the team.
     * <p>
     * If the specified player is not a member of this team,
     * the method has no effect.
     * </p>
     *
     * @param player the player to remove from this team
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Returns the current score of the team.
     * <p>
     * The returned value is used by the game to check victory conditions.
     * For plumbers, the score represents water successfully delivered to cisterns.
     * For saboteurs, the score represents water that leaked into the desert.
     * </p>
     *
     * @return the current total points accumulated by this team
     */
    public int getScore() {
        return score;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Teams getTeam() {
        return team;
    }

    /**
     * Increments the team's total score by the specified amount.
     * <p>
     * This method is called when water successfully reaches a cistern
     * (plumbers) or leaks from a pipe (saboteurs). The score accumulates
     * throughout the game session until one team reaches the goal score.
     * </p>
     *
     * @param score the amount to add to the team's current score
     */
    public void addScore(int score) {
        this.score += score;
    }

    @Override
    public String toString() {
        StringBuilder playerIds = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            if (i > 0) {
                playerIds.append(',');
            }
            playerIds.append(players.get(i).getId());
        }

        return String.format("[STATE] TEAM %s score=%d players=%s", team, score, playerIds);
    }

}
