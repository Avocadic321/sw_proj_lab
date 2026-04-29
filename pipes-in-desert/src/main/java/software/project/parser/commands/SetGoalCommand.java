package software.project.parser.commands;

import software.project.App;
import software.project.core.Game;
import software.project.parser.ICommand;

public class SetGoalCommand implements ICommand {
    private final App app;

    public SetGoalCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(Game game, String[] args) {
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

        app.getGameConfig().setGoalScore(value);
    }
}
