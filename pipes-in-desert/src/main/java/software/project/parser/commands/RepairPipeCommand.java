package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Repairs a broken pipe at the player's current location.
 */
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
            System.out.println("[ERROR] REPAIR_PIPE INVALID_ARGS. Usage: REPAIR_PIPE <pipeId> [playerId]");
            return;
        }

        Player player;
        String pipeId;

        if (args.length == 2) {
            player = findPlayer(game, args[0]);
            pipeId = args[1].trim();
            if (player == null) {
                System.out.println("[ERROR] REPAIR_PIPE PLAYER_NOT_FOUND " + args[0]);
                return;
            }
        } else {
            player = game.getTurnManager().getCurrentPlayer();
            pipeId = args[0].trim();
        }

        if (player == null) {
            System.out.println("[ERROR] REPAIR_PIPE NO_CURRENT_PLAYER");
            return;
        }

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] REPAIR_PIPE NOT_A_PLUMBER");
            return;
        }

        Pipe pipe = game.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] REPAIR_PIPE PIPE_NOT_FOUND " + pipeId);
            return;
        }

        if (plumber.getCurrentPosition() != pipe) {
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

    private Player findPlayer(Game game, String playerId) {
        for (Player p : game.getPlumbersTeam().getPlayers()) {
            if (p.getId().equalsIgnoreCase(playerId)) return p;
        }
        for (Player p : game.getSaboteursTeam().getPlayers()) {
            if (p.getId().equalsIgnoreCase(playerId)) return p;
        }
        return null;
    }
}
