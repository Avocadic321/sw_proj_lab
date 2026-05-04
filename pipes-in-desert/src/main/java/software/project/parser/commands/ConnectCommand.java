package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.ActiveElement;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class ConnectCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] CONNECT GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] CONNECT GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 3) {
            System.out.println("[ERROR] CONNECT INVALID_ARGS. Usage: CONNECT <pipeId> <end(1|2)> <elementId>");
            return;
        }

        boolean documentedForm = CommandUtils.findPlayer(game, args[0]) != null;
        Player p = documentedForm
                ? CommandUtils.findPlayer(game, args[0])
                : game.getTurnManager().getCurrentPlayer();

        String pipeId = documentedForm ? args[1].trim() : args[0].trim();
        String endStr = documentedForm ? "" : args[1].trim();
        String elementId = documentedForm ? args[2].trim() : args[2].trim();

        ActiveElement target = CommandUtils.findElement(game, elementId, ActiveElement.class);
        if (target == null) {
            System.out.println("[ERROR] CONNECT TARGET_NOT_FOUND " + elementId);
            return;
        }

        PipeEnd end = documentedForm
                ? CommandUtils.findPipeEnd(game, pipeId)
                : CommandUtils.findPipeEnd(game, pipeId, endStr);
        if (end == null) {
            System.out.println("[ERROR] CONNECT INVALID_END_INDEX");
            return;
        }

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
