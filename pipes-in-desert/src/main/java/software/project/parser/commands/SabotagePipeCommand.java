package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Saboteur;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Sabotages (breaks) a pipe as a saboteur action.
 */
public class SabotagePipeCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] SABOTAGE_PIPE GAME_NOT_RUNNING");
            return;
        }

        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println("[ERROR] SABOTAGE_PIPE INVALID_ARGS. Usage: SABOTAGE_PIPE <pipeId>");
            return;
        }

        boolean documentedForm = args.length == 2 && CommandUtils.findPlayer(game, args[0]) != null;
        String pipeId = args[documentedForm ? 1 : 0].trim();
        Pipe pipe = CommandUtils.findElement(game, pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE PIPE_NOT_FOUND " + pipeId);
            return;
        }

        Player p = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Saboteur)) {
            System.out.println("[ERROR] SABOTAGE_PIPE NOT_A_SABOTEUR");
            return;
        }

        try {
            ((Saboteur) p).sabotagePipe(pipe);
        } catch (Exception e) {
            System.out.println("[ERROR] SABOTAGE_PIPE " + e.getMessage());
            return;
        }
        System.out.println("[OK] SABOTAGE_PIPE " + pipeId);
    }
}
