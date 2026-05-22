package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Produces a component (pipe or pump) at a specified cistern.
 */
public class RandomProduceCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] RANDOM_PRODUCE GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] RANDOM_PRODUCE GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length < 2 || args[0].isBlank() || args[1].isBlank()) {
            System.out.println(
                    "[ERROR] RANDOM_PRODUCE requires cistern ID and type (PIPE or PUMP). Usage: RANDOM_PRODUCE <cisternId> <PIPE|PUMP>");
            return;
        }
        gameModel.produceComponentAt(args[0].trim(), args[1].trim().toUpperCase());
    }
}
