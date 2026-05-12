package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Cistern;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Picks up a pump from a cistern.
 * Usage: PICKUP_PUMP <cisternId>   or   PICKUP_PUMP <playerId> <cisternId>
 */
public class PickupPumpCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] PICKUP_PUMP GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] PICKUP_PUMP GAME_NOT_RUNNING");
            return;
        }
        if (args == null || args.length < 1 || args.length > 2) {
            System.out.println("[ERROR] PICKUP_PUMP INVALID_ARGS. Usage: PICKUP_PUMP <cisternId> [playerId]");
            return;
        }

        Player player;
        String cisternId;

        if (args.length == 2) {
            // PICKUP_PUMP <playerId> <cisternId>
            player = findPlayer(game, args[0]);
            cisternId = args[1].trim();
            if (player == null) {
                System.out.println("[ERROR] PICKUP_PUMP PLAYER_NOT_FOUND " + args[0]);
                return;
            }
        } else {
            // PICKUP_PUMP <cisternId>
            player = game.getTurnManager().getCurrentPlayer();
            cisternId = args[0].trim();
        }

        if (player == null) {
            System.out.println("[ERROR] PICKUP_PUMP NO_CURRENT_PLAYER");
            return;
        }

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] PICKUP_PUMP NOT_A_PLUMBER");
            return;
        }

        Cistern cistern = game.getGameMap().getElement(cisternId, Cistern.class);
        if (cistern == null) {
            System.out.println("[ERROR] PICKUP_PUMP CISTERN_NOT_FOUND " + cisternId);
            return;
        }

        try {
            plumber.pickUpPump(cistern);
            System.out.println("[OK] PICKUP_PUMP " + player.getId() + " " + cistern.getId());
        } catch (Exception e) {
            System.out.println("[ERROR] PICKUP_PUMP " + e.getMessage());
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