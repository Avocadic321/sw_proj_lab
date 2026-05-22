package software.project.parser.commands;

import software.project.App;
import software.project.core.GameModel;
import software.project.core.GameConfig;
import software.project.parser.ICommand;

/**
 * Sets the number of players in the game (before it starts).
 */
public class SetPlayersCommand implements ICommand {
    private final App app;

    public SetPlayersCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel != null) {
            System.out.println("[ERROR] SET_PLAYERS GAME_ALREADY_STARTED");
            return;
        }

        if (args.length != 1) {
            System.out.println("[ERROR] SET_PLAYERS INVALID_ARGS");
            return;
        }

        int value;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] SET_PLAYERS NOT_NUMERIC");
            return;
        }

        if (value < GameConfig.MIN_PLAYERS || value > GameConfig.MAX_PLAYERS) {
            System.out.printf(
                "[ERROR] SET_PLAYERS OUT_OF_RANGE [%d, %d]%n",
                GameConfig.MIN_PLAYERS,
                GameConfig.MAX_PLAYERS
            );
            return;
        }

        app.getGameConfig().setNumberOfPlayers(value);
        System.out.println("[OK] SET_PLAYERS " + value);
    }
}
