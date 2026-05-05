package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Inserts a pump that a plumber is carrying into a pipe.
 * Usage: INSERT_PUMP <pipeId>   or   INSERT_PUMP <playerId> <pipeId>
 */
public class InsertPumpCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] INSERT_PUMP GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] INSERT_PUMP GAME_NOT_RUNNING");
            return;
        }
        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println("[ERROR] INSERT_PUMP INVALID_ARGS. Usage: INSERT_PUMP <pipeId> [playerId]");
            return;
        }

        Player player;
        String pipeId;

        if (args.length == 2) {
            player = findPlayer(game, args[0]);
            pipeId = args[1].trim();
            if (player == null) {
                System.out.println("[ERROR] INSERT_PUMP PLAYER_NOT_FOUND " + args[0]);
                return;
            }
        } else {
            player = game.getTurnManager().getCurrentPlayer();
            pipeId = args[0].trim();
        }

        if (player == null) {
            System.out.println("[ERROR] INSERT_PUMP NO_CURRENT_PLAYER");
            return;
        }

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] INSERT_PUMP NOT_A_PLUMBER");
            return;
        }

        Pipe pipe = game.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] INSERT_PUMP PIPE_NOT_FOUND " + pipeId);
            return;
        }

        if (!(plumber.getCarriedItem() instanceof Pump pump)) {
            System.out.println("[ERROR] INSERT_PUMP NOT_CARRYING_PUMP");
            return;
        }

        try {
            plumber.insertPumpIntoPipe(pump, pipe);
        } catch (Exception e) {
            System.out.println("[ERROR] INSERT_PUMP " + e.getMessage());
        }
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