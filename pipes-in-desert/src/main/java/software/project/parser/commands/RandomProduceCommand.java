package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;

public class RandomProduceCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (args == null || args.length < 2 || args[0].isBlank() || args[1].isBlank()) {
            System.out.println("[ERROR] RANDOM_PRODUCE requires cistern ID and type (PIPE or PUMP). Usage: RANDOM_PRODUCE <cisternId> <PIPE|PUMP>");
            return;
        }
        if (game == null) {
            System.out.println("[ERROR] RANDOM_PRODUCE GAME_NOT_INITIALIZED");
            return;
        }
        game.produceComponentAt(args[0].trim(), args[1].trim().toUpperCase());
    }
}