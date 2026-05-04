package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class RandomBreakCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] RANDOM_BREAK GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] RANDOM_BREAK GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length < 1 || args[0].isBlank()) {
            System.out.println("[ERROR] RANDOM_BREAK requires a pump ID. Usage: RANDOM_BREAK <pumpId>");
            return;
        }
        game.breakSpecificPump(CommandUtils.normalizeId(args[0].trim()));
    }
}
