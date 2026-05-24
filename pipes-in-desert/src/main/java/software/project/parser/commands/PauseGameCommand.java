package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Pauses the currently running game.
 */
public class PauseGameCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] PAUSE GAME_NOT_INITIALIZED");
            return;
        }
        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] PAUSE GAME_NOT_RUNNING");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] PAUSE INVALID_ARGUMENTS");
            return;
        }

        gameModel.pauseGame();
        System.out.println("[OK] PAUSE");
    }
}
