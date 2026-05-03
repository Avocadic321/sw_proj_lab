package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Pump;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class SetDirectionCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] SET_DIRECTION GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
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

        Pump pump = game.getGameMap().getElement(pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] SET_DIRECTION PUMP_NOT_FOUND " + pumpId);
            return;
        }

        Pipe inPipe = game.getGameMap().getElement(inPipeId, Pipe.class);
        Pipe outPipe = game.getGameMap().getElement(outPipeId, Pipe.class);
        if (inPipe == null || outPipe == null) {
            System.out.println("[ERROR] SET_DIRECTION PIPE_NOT_FOUND");
            return;
        }

        Player player = game.getTurnManager().getCurrentPlayer();
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
