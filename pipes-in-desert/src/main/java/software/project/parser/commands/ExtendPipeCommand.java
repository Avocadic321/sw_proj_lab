package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Extends the pipe system by the current plumber player.
 */
public class ExtendPipeCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] EXTEND_PIPE GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] EXTEND_PIPE GAME_NOT_RUNNING");
            return;
        }

        if (args != null && args.length > 0) {
            System.out.println("[ERROR] EXTEND_PIPE INVALID_ARGS. Usage: EXTEND_PIPE");
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] EXTEND_PIPE NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] EXTEND_PIPE NOT_A_PLUMBER");
            return;
        }

        try {
            ((Plumber) p).extendPipeSystem();
            System.out.println("[OK] EXTEND_PIPE");
        } catch (Exception e) {
            System.out.println("[ERROR] EXTEND_PIPE " + e.getMessage());
        }
    }
}
