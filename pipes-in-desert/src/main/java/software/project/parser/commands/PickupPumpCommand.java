package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.map.Cistern;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Picks up a pump from a cistern.
 * Usage: PICKUP_PUMP <cisternId>   or   PICKUP_PUMP <playerId> <cisternId>
 */
public class PickupPumpCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] PICKUP_PUMP GAME_NOT_INITIALIZED");
            return;
        }
        if (gameModel.getState() != GameState.RUNNING) {
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
            player = findPlayer(gameModel, args[0]);
            cisternId = args[1].trim();
            if (player == null) {
                System.out.println("[ERROR] PICKUP_PUMP PLAYER_NOT_FOUND " + args[0]);
                return;
            }
        } else {
            // PICKUP_PUMP <cisternId>
            player = gameModel.getTurnManager().getCurrentPlayer();
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

        Cistern cistern = gameModel.getGameMap().getElement(cisternId, Cistern.class);
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

    private Player findPlayer(GameModel gameModel, String playerId) {
        for (Player p : gameModel.getPlumbersTeam().getPlayers()) {
            if (p.getId().equalsIgnoreCase(playerId)) return p;
        }
        for (Player p : gameModel.getSaboteursTeam().getPlayers()) {
            if (p.getId().equalsIgnoreCase(playerId)) return p;
        }
        return null;
    }
}