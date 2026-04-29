package software.project.parser.commands;

import software.project.App;
import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class SetTurnDurationCommand implements ICommand {
    private final App app;

    public SetTurnDurationCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(Game game, String[] args) {
        if (game != null) {
            System.out.println("[ERROR] SET_TURN_DURATION GAME_ALREADY_STARTED");
            return;
        }

        if (args.length != 1) {
            System.out.println("[ERROR] SET_TURN_DURATION INVALID_ARGS");
            return;
        }

        int value;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] SET_TURN_DURATION NOT_NUMERIC");
            return;
        }

        if (value < GameConfig.MIN_TURN_DURATION || value > GameConfig.MAX_TURN_DURATION) {
            System.out.printf(
                "[ERROR] SET_TURN_DURATION OUT_OF_RANGE [%d, %d]%n",
                GameConfig.MIN_TURN_DURATION,
                GameConfig.MAX_TURN_DURATION
            );
            return;
        }

        app.getGameConfig().setTurnDurationSeconds(value);
        System.out.println("[OK] SET_TURN_DURATION " + value);
    }
}
