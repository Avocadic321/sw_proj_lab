package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.models.ActiveElement;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Connects a pipe end to an active element (pump or cistern).
 */
public class ConnectCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] CONNECT GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] CONNECT GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 3) {
            System.out.println("[ERROR] CONNECT INVALID_ARGS. Usage: CONNECT <pipeId> <end(1|2)> <elementId>");
            return;
        }

        String pipeId = args[0].trim();
        String endStr = args[1].trim();
        String elementId = args[2].trim();

        Pipe pipe = gameModel.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] CONNECT PIPE_NOT_FOUND " + pipeId);
            return;
        }

        int endIndex;
        try {
            endIndex = Integer.parseInt(endStr);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] CONNECT INVALID_END_INDEX");
            return;
        }

        ActiveElement target = gameModel.getGameMap().getElement(elementId, ActiveElement.class);
        if (target == null) {
            System.out.println("[ERROR] CONNECT TARGET_NOT_FOUND " + elementId);
            return;
        }

        PipeEnd end = (endIndex == 1) ? pipe.getEnd1() : (endIndex == 2) ? pipe.getEnd2() : null;
        if (end == null) {
            System.out.println("[ERROR] CONNECT INVALID_END_INDEX");
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] CONNECT NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] CONNECT NOT_A_PLUMBER");
            return;
        }

        try {
            ((Plumber) p).connect(end, target);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("[ERROR] CONNECT " + ex.getMessage());
        }
    }
}
