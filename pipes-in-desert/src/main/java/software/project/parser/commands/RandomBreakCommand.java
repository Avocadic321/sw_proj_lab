package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Breaks a specific pump (for testing or random events).
 */
public class RandomBreakCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] RANDOM_BREAK GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] RANDOM_BREAK GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length < 1 || args[0].isBlank()) {
            System.out.println("[ERROR] RANDOM_BREAK requires a pump ID. Usage: RANDOM_BREAK <pumpId>");
            return;
        }
        gameModel.breakSpecificPump(args[0].trim());
    }
}
