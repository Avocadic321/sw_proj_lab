package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.core.GameState;
import software.project.map.Cistern;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;

/**
 * Picks up a pipe from a cistern.
 * Usage: PICKUP_PIPE <cisternId>   or   PICKUP_PIPE <playerId> <cisternId>
 */
public class PickupPipeCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] PICKUP_PIPE GAME_NOT_INITIALIZED");
            return;
        }
        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] PICKUP_PIPE GAME_NOT_RUNNING");
            return;
        }
        if (args == null || args.length < 1 || args.length > 2) {
            System.out.println("[ERROR] PICKUP_PIPE INVALID_ARGS. Usage: PICKUP_PIPE <cisternId> [playerId]");
            return;
        }

        Player player;
        String cisternId;

        if (args.length == 2) {
            // PICKUP_PIPE <playerId> <cisternId>
            player = findPlayer(gameModel, args[0]);
            cisternId = args[1].trim();
            if (player == null) {
                System.out.println("[ERROR] PICKUP_PIPE PLAYER_NOT_FOUND " + args[0]);
                return;
            }
        } else {
            // PICKUP_PIPE <cisternId>
            player = gameModel.getTurnManager().getCurrentPlayer();
            cisternId = args[0].trim();
        }

        if (player == null) {
            System.out.println("[ERROR] PICKUP_PIPE NO_CURRENT_PLAYER");
            return;
        }

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] PICKUP_PIPE NOT_A_PLUMBER");
            return;
        }

        if (!gameModel.getTurnManager().canUseBigAction()) {
            System.out.println("[ERROR] PICKUP_PIPE NO_BIG_ACTIONS_LEFT");
            return;
        }

        Cistern cistern = gameModel.getGameMap().getElement(cisternId, Cistern.class);
        if (cistern == null) {
            System.out.println("[ERROR] PICKUP_PIPE CISTERN_NOT_FOUND " + cisternId);
            return;
        }

        try {
            plumber.pickUpPipe(cistern);
            gameModel.getTurnManager().useBigAction();
            System.out.println("[OK] PICKUP_PIPE " + player.getId() + " " + cistern.getId());
        } catch (Exception e) {
            System.out.println("[ERROR] PICKUP_PIPE " + e.getMessage());
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