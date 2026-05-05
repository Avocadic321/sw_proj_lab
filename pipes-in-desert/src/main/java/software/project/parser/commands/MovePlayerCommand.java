package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Element;
import software.project.models.Player;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Moves a player to an adjacent element on the map.
 */
public class MovePlayerCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] MOVE GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] MOVE GAME_NOT_RUNNING");
            return;
        }

        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println("[ERROR] MOVE INVALID_ARGS");
            return;
        }

        Player p = args.length == 2
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] MOVE NO_CURRENT_PLAYER");
            return;
        }

        String targetId = args[args.length - 1].trim();
        Element target = CommandUtils.findElement(game, targetId);
        if (target == null) {
            System.out.println("[ERROR] MOVE TARGET_NOT_FOUND " + targetId);
            return;
        }

        p.moveTo(target);
    }
}
