package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.models.Team;

/**
 * Displays the current scores of both teams.
 */
public class ScoreCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] SCORE GAME_NOT_INITIALIZED");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] SCORE INVALID_ARGUMENTS");
            return;
        }

        Team plumbers = gameModel.getPlumbersTeam();
        Team saboteurs = gameModel.getSaboteursTeam();

        int plumberScore = (plumbers != null) ? plumbers.getScore() : 0;
        int saboteurScore = (saboteurs != null) ? saboteurs.getScore() : 0;

        System.out.printf("[SCORE] Plumbers: %d, Saboteurs: %d%n", plumberScore, saboteurScore);
    }
}