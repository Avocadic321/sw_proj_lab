package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class DisconnectCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] DISCONNECT GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] DISCONNECT GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 2) {
            System.out.println("[ERROR] DISCONNECT INVALID_ARGS. Usage: DISCONNECT <pipeId> <end(1|2)>");
            return;
        }

        String pipeId = args[0].trim();
        String endStr = args[1].trim();

        Pipe pipe = game.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] DISCONNECT PIPE_NOT_FOUND " + pipeId);
            return;
        }

        int endIndex;
        try {
            endIndex = Integer.parseInt(endStr);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] DISCONNECT INVALID_END_INDEX");
            return;
        }

        PipeEnd end = (endIndex == 1) ? pipe.getEnd1() : (endIndex == 2) ? pipe.getEnd2() : null;
        if (end == null) {
            System.out.println("[ERROR] DISCONNECT INVALID_END_INDEX");
            return;
        }

        Player p = game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] DISCONNECT NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] DISCONNECT NOT_A_PLUMBER");
            return;
        }

        try {
            ((Plumber) p).disconnect(end);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("[ERROR] DISCONNECT " + ex.getMessage());
        }
    }
}
