package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Pump;
import software.project.parser.CommandUtils;
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

        if (args == null || (args.length != 3 && args.length != 4)) {
            System.out.println("[ERROR] SET_DIRECTION INVALID_ARGS. Usage: SET_DIRECTION <pumpId> <inPipeId> <outPipeId>");
            return;
        }

        boolean documentedForm = args.length == 4 && CommandUtils.findPlayer(game, args[0]) != null;
        String pumpId = args[documentedForm ? 1 : 0].trim();
        String inPipeId = args[documentedForm ? 2 : 1].trim();
        String outPipeId = args[documentedForm ? 3 : 2].trim();

        Pump pump = CommandUtils.findElement(game, pumpId, Pump.class);
        if (pump == null) {
            System.out.println("[ERROR] SET_DIRECTION PUMP_NOT_FOUND " + pumpId);
            return;
        }

        Player player = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();
        if (player == null) {
            System.out.println("[ERROR] SET_DIRECTION NO_CURRENT_PLAYER");
            return;
        }

        PipeEnd inputEnd = CommandUtils.findPipeEnd(game, inPipeId);
        PipeEnd outputEnd = CommandUtils.findPipeEnd(game, outPipeId);
        if (inputEnd != null && !pump.getConnections().contains(inputEnd) && inputEnd.pipe != null) {
            inputEnd = inputEnd.pipe.getEnd1().connectedTo == pump
                    ? inputEnd.pipe.getEnd1()
                    : inputEnd.pipe.getEnd2().connectedTo == pump ? inputEnd.pipe.getEnd2() : inputEnd;
        }
        if (outputEnd != null && !pump.getConnections().contains(outputEnd) && outputEnd.pipe != null) {
            outputEnd = outputEnd.pipe.getEnd1().connectedTo == pump
                    ? outputEnd.pipe.getEnd1()
                    : outputEnd.pipe.getEnd2().connectedTo == pump ? outputEnd.pipe.getEnd2() : outputEnd;
        }
        boolean ok;
        if (inputEnd != null || outputEnd != null) {
            ok = pump.setDirection(inputEnd, outputEnd);
        } else {
            Pipe inPipe = CommandUtils.findElement(game, inPipeId, Pipe.class);
            Pipe outPipe = CommandUtils.findElement(game, outPipeId, Pipe.class);
            if (inPipe == null || outPipe == null) {
                System.out.println("[ERROR] SET_DIRECTION PIPE_NOT_FOUND");
                return;
            }
            ok = player.changePumpDirection(pump, inPipe, outPipe);
        }
        if (ok) {
            System.out.println("[OK] SET_DIRECTION " + pumpId + " IN=" + inPipeId + " OUT=" + outPipeId);
        } else {
            System.out.println("[ERROR] SET_DIRECTION FAILED");
        }
    }
}
