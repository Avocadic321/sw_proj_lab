package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Inserts a pump that a plumber is carrying into a pipe.
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
        if (args == null || (args.length != 1 && args.length != 3)) {
            System.out.println("[ERROR] INSERT_PUMP INVALID_ARGS");
            return;
        }

        boolean documentedForm = args.length == 3 && CommandUtils.findPlayer(game, args[0]) != null;
        Player player = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        Pipe pipe = CommandUtils.findElement(game, args[documentedForm ? 1 : 0], Pipe.class);

        if (!(player instanceof Plumber plumber)) {
            System.out.println("[ERROR] INSERT_PUMP NOT_A_PLUMBER");
            return;
        }
        if (pipe == null) {
            System.out.println("[ERROR] INSERT_PUMP PIPE_NOT_FOUND");
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
}
