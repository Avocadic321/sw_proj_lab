package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Resumes a paused game.
 */
public class ResumeGameCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] RESUME GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.PAUSED) {
            System.out.println("[ERROR] RESUME GAME_NOT_PAUSED");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] RESUME INVALID_ARGUMENTS");
            return;
        }

        game.resumeGame();
        System.out.println("[OK] RESUME");
    }
}
