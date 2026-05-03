package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;

public class ExtendPipeCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] EXTEND_PIPE GAME_NOT_INITIALIZED");
            return;
        }

        if (args != null && args.length > 0) {
            System.out.println("[ERROR] EXTEND_PIPE INVALID_ARGS. Usage: EXTEND_PIPE");
            return;
        }

        Player p = game.getTurnManager().getCurrentPlayer();
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
