package software.project.parser.commands;

import software.project.App;
import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class SetGoalCommand implements ICommand {
    private final App app;

    public SetGoalCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(Game game, String[] args) {
        if (game != null) {
            System.out.println("[ERROR] SET_GOAL GAME_ALREADY_STARTED");
            return;
        }

        if (args.length != 1) {
            System.out.println("[ERROR] SET_GOAL INVALID_ARGS");
            return;
        }

        int value;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] SET_GOAL NOT_NUMERIC");
            return;
        }

        if (value < GameConfig.MIN_GOAL_SCORE || value > GameConfig.MAX_GOAL_SCORE) {
            System.out.printf(
                "[ERROR] SET_SCORE OUT_OF_RANGE [%d, %d]%n",
                GameConfig.MIN_GOAL_SCORE,
                GameConfig.MAX_GOAL_SCORE
            );
            return;
        }

        app.getGameConfig().setGoalScore(value);
    }
}
