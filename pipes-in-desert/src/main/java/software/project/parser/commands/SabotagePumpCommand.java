package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Player;
import software.project.models.Pump;
import software.project.models.Saboteur;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Sabotages (breaks) a pump as a saboteur action.
 */
public class SabotagePumpCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] SABOTAGE_PUMP GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] SABOTAGE_PUMP GAME_NOT_RUNNING");
            return;
        }

        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println("[ERROR] SABOTAGE_PUMP INVALID_ARGS. Usage: SABOTAGE_PUMP <pumpId>");
            return;
        }

        boolean documentedForm = args.length == 2 && CommandUtils.findPlayer(game, args[0]) != null;
        String pumpId = args[documentedForm ? 1 : 0].trim();
        Pump pump = CommandUtils.findElement(game, pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] SABOTAGE_PUMP PUMP_NOT_FOUND " + pumpId);
            return;
        }

        Player p = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] SABOTAGE_PUMP NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Saboteur)) {
            System.out.println("[ERROR] SABOTAGE_PUMP NOT_A_SABOTEUR");
            return;
        }

        if (p.getCurrentPosition() != pump) {
            System.out.println("[ERROR] SABOTAGE_PUMP NOT_AT_PUMP");
            return;
        }

        if (pump.isBroken()) {
            System.out.println("[ERROR] SABOTAGE_PUMP ALREADY_BROKEN " + pumpId);
            return;
        }

        pump.breakElement();
        System.out.println("[OK] SABOTAGE_PUMP " + pumpId);
    }
}
