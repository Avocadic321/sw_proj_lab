package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.models.Element;
import software.project.models.Player;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Moves a player to an adjacent element on the map.
 */
public class MovePlayerCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] MOVE GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] MOVE GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 1) {
            System.out.println("[ERROR] MOVE INVALID_ARGS");
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] MOVE NO_CURRENT_PLAYER");
            return;
        }

        String targetId = args[0].trim();
        Element target = gameModel.getGameMap().getElement(targetId);
        if (target == null) {
            System.out.println("[ERROR] MOVE TARGET_NOT_FOUND " + targetId);
            return;
        }

        p.moveTo(target);
    }
}
