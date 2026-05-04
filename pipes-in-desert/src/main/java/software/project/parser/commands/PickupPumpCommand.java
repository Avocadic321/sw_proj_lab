package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Cistern;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

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
        if (args == null || args.length < 1 || args.length > 3) {
            System.out.println("[ERROR] PICKUP_PUMP INVALID_ARGS");
            return;
        }

        boolean documentedForm = args.length >= 2 && CommandUtils.findPlayer(game, args[0]) != null;
        Player player = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        Cistern cistern = CommandUtils.findElement(game, args[documentedForm ? 1 : 0], Cistern.class);

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] PICKUP_PUMP NOT_A_PLUMBER");
            return;
        }
        if (cistern == null) {
            System.out.println("[ERROR] PICKUP_PUMP CISTERN_NOT_FOUND");
            return;
        }

        try {
            plumber.pickUpPump(cistern);
            System.out.println("[OK] PICKUP_PUMP " + player.getId() + " " + cistern.getId());
        } catch (Exception e) {
            System.out.println("[ERROR] PICKUP_PUMP " + e.getMessage());
        }
    }
}
