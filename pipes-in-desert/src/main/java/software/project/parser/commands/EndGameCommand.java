package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Ends the current game.
 */
public class EndGameCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] END_GAME GAME_NOT_INITIALIZED");
            return;
        }
        GameState state = game.getState();
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            System.out.println("[ERROR] END_GAME GAME_NOT_ACTIVE");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] END_GAME INVALID_ARGS");
            return;
        }

        game.endGame();
        System.out.println("[OK] END_GAME");
    }
}
