package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Disconnects a pipe end from an active element.
 */
public class DisconnectCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] DISCONNECT GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] DISCONNECT GAME_NOT_RUNNING");
            return;
        }

        if (args == null || (args.length != 2 && args.length != 1)) {
            System.out.println("[ERROR] DISCONNECT INVALID_ARGS. Usage: DISCONNECT <pipeId> <end(1|2)>");
            return;
        }

        Player p;
        PipeEnd end;
        if (args.length == 2 && CommandUtils.findPlayer(game, args[0]) != null) {
            p = CommandUtils.findPlayer(game, args[0]);
            end = CommandUtils.findPipeEnd(game, args[1]);
        } else {
            p = game.getTurnManager().getCurrentPlayer();
            end = args.length == 1
                    ? CommandUtils.findPipeEnd(game, args[0])
                    : CommandUtils.findPipeEnd(game, args[0], args[1]);
        }

        if (p == null) {
            System.out.println("[ERROR] DISCONNECT NO_CURRENT_PLAYER");
            return;
        }

        if (end == null) {
            System.out.println("[ERROR] DISCONNECT INVALID_END_INDEX");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] DISCONNECT NOT_A_PLUMBER");
            return;
        }

        try {
            ((Plumber) p).disconnect(end);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("[ERROR] DISCONNECT " + ex.getMessage());
        }
    }
}
