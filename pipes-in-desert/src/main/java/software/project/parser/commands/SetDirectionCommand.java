package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.map.Pipe;
import software.project.models.Player;
import software.project.map.Pump;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Sets the flow direction of a pump by specifying its input and output pipes.
 */
public class SetDirectionCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] SET_DIRECTION GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] SET_DIRECTION GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 3) {
            System.out.println("[ERROR] SET_DIRECTION INVALID_ARGS. Usage: SET_DIRECTION <pumpId> <inPipeId> <outPipeId>");
            return;
        }

        String pumpId = args[0].trim();
        String inPipeId = args[1].trim();
        String outPipeId = args[2].trim();

        Pump pump = gameModel.getGameMap().getElement(pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] SET_DIRECTION PUMP_NOT_FOUND " + pumpId);
            return;
        }

        Pipe inPipe = gameModel.getGameMap().getElement(inPipeId, Pipe.class);
        Pipe outPipe = gameModel.getGameMap().getElement(outPipeId, Pipe.class);
        if (inPipe == null || outPipe == null) {
            System.out.println("[ERROR] SET_DIRECTION PIPE_NOT_FOUND");
            return;
        }

        Player player = gameModel.getTurnManager().getCurrentPlayer();
        if (player == null) {
            System.out.println("[ERROR] SET_DIRECTION NO_CURRENT_PLAYER");
            return;
        }

        boolean ok = player.changePumpDirection(pump, inPipe, outPipe);
        if (ok) {
            System.out.println("[OK] SET_DIRECTION " + pumpId + " IN=" + inPipeId + " OUT=" + outPipeId);
        } else {
            System.out.println("[ERROR] SET_DIRECTION FAILED");
        }
    }
}
