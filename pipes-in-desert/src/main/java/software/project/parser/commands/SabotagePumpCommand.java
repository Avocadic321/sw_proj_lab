package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.models.Player;
import software.project.map.Pump;
import software.project.models.Saboteur;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Sabotages (breaks) a pump as a saboteur action.
 */
public class SabotagePumpCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] SABOTAGE_PUMP GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] SABOTAGE_PUMP GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 1) {
            System.out.println("[ERROR] SABOTAGE_PUMP INVALID_ARGS. Usage: SABOTAGE_PUMP <pumpId>");
            return;
        }

        String pumpId = args[0].trim();
        Pump pump = gameModel.getGameMap().getElement(pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] SABOTAGE_PUMP PUMP_NOT_FOUND " + pumpId);
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
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
