package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Saboteur;
import software.project.parser.ICommand;

public class SabotagePipeCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE GAME_NOT_INITIALIZED");
            return;
        }

        if (args == null || args.length != 1) {
            System.out.println("[ERROR] SABOTAGE_PIPE INVALID_ARGS. Usage: SABOTAGE_PIPE <pipeId>");
            return;
        }

        String pipeId = args[0].trim();
        Pipe pipe = game.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE PIPE_NOT_FOUND " + pipeId);
            return;
        }

        Player p = game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Saboteur)) {
            System.out.println("[ERROR] SABOTAGE_PIPE NOT_A_SABOTEUR");
            return;
        }

        try {
            ((Saboteur) p).sabotagePipe(pipe);
        } catch (Exception e) {
            System.out.println("[ERROR] SABOTAGE_PIPE " + e.getMessage());
            return;
        }
        System.out.println("[OK] SABOTAGE_PIPE " + pipeId);
    }
}
