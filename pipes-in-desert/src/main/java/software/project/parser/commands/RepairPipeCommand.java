package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class RepairPipeCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] REPAIR_PIPE GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] REPAIR_PIPE GAME_NOT_RUNNING");
            return;
        }
        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println("[ERROR] REPAIR_PIPE INVALID_ARGS. Usage: REPAIR_PIPE <pipeId>");
            return;
        }

        boolean documentedForm = args.length == 2 && CommandUtils.findPlayer(game, args[0]) != null;
        Player player = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        String pipeId = args[documentedForm ? 1 : 0].trim();
        Pipe pipe = CommandUtils.findElement(game, pipeId, Pipe.class);

        if (pipe == null) {
            System.out.println("[ERROR] REPAIR_PIPE PIPE_NOT_FOUND " + pipeId);
            return;
        }
        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] REPAIR_PIPE NOT_A_PLUMBER");
            return;
        }
        if (player.getCurrentPosition() != pipe) {
            System.out.println("[ERROR] REPAIR_PIPE NOT_AT_PIPE");
            return;
        }
        if (!pipe.isBroken()) {
            System.out.println("[ERROR] REPAIR_PIPE NOT_BROKEN");
            return;
        }

        plumber.repair(pipe);
        System.out.println("[OK] REPAIR_PIPE " + pipeId);
    }
}
