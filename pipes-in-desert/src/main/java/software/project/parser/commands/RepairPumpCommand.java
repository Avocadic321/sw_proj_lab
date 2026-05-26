package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.core.GameState;
import software.project.map.Pump;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;

/**
 * Repairs a broken pump.
 */
public class RepairPumpCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] REPAIR_PUMP GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] REPAIR_PUMP GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 1) {
            System.out.println("[ERROR] REPAIR_PUMP INVALID_ARGS. Usage: REPAIR_PUMP <pumpId>");
            return;
        }

        String pumpId = args[0].trim();
        Pump pump = gameModel.getGameMap().getElement(pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] REPAIR_PUMP PUMP_NOT_FOUND " + pumpId);
            return;
        }

        if (!pump.isBroken()) {
            System.out.println("[ERROR] REPAIR_PUMP NOT_BROKEN " + pumpId);
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] REPAIR_PUMP NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] REPAIR_PUMP NOT_A_PLUMBER");
            return;
        }

        if (!gameModel.getTurnManager().canUseBigAction()) {
            System.out.println("[ERROR] REPAIR_PUMP NO_BIG_ACTIONS_LEFT");
            return;
        }

        if (p.getCurrentPosition() != pump) {
            System.out.println("[ERROR] REPAIR_PUMP NOT_AT_PUMP");
            return;
        }

        ((Plumber) p).repair(pump);
        gameModel.getTurnManager().useBigAction();
        System.out.println("[OK] REPAIR_PUMP " + pumpId);
    }
}
