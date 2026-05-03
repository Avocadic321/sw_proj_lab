package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class PauseGameCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] PAUSE GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] PAUSE GAME_NOT_RUNNING");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] PAUSE INVALID_ARGUMENTS");
            return;
        }

        game.pauseGame();
        System.out.println("[OK] PAUSE");
    }
}
