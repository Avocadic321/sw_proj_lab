package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Ends the current game.
 */
public class EndGameCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] END_GAME GAME_NOT_INITIALIZED");
            return;
        }
        GameState state = gameModel.getState();
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            System.out.println("[ERROR] END_GAME GAME_NOT_ACTIVE");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] END_GAME INVALID_ARGS");
            return;
        }

        gameModel.endGame();
        System.out.println("[OK] END_GAME");
    }
}
