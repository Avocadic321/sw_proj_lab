package software.project.parser.commands;

import software.project.App;
import software.project.core.GameModel;
import software.project.parser.ICommand;

/**
 * Enables or disables random events in the game.
 */
public class SetRandomCommand implements ICommand {
    private final App app;

    public SetRandomCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (args.length != 1) {
            System.out.println("[ERROR] SET_RANDOM INVALID_ARGS");
            return;
        }

        boolean flag;
        if (args[0].equalsIgnoreCase("ON")) {
            flag = true;
        } else if (args[0].equalsIgnoreCase("OFF")) {
            flag = false;
        } else {
            System.out.println("[ERROR] SET_RANDOM INVALID_STATE");
            return;
        }

        app.getGameConfig().setRandomEventsEnabled(flag);
        System.out.println("[OK] SET_RANDOM " + (flag ? "ON" : "OFF"));
    }
}
