package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Simulates fluid flow through the pipe system for a specified number of ticks.
 */
public class FlowCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] FLOW GAME_NOT_INITIALIZED");
            return;
        }
        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] FLOW GAME_NOT_RUNNING");
            return;
        }

        int ticks = 1;
        if (args.length > 0) {
            if (args.length != 1) {
                System.out.println("[ERROR] FLOW INVALID_ARGS");
                return;
            }
            try {
                ticks = Integer.parseInt(args[0]);
                if (ticks < 1) {
                    System.out.println("[ERROR] FLOW INVALID_ARG_NEGATIVE");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] FLOW INVALID_ARGS");
                return;
            }
        }
        gameModel.flow(ticks);
        System.out.println("[OK] FLOW " + ticks);
    }
}
