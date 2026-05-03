package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class FlowCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] FLOW GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
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
        game.flow(ticks);
        System.out.println("[OK] FLOW " + ticks);
    }
}
