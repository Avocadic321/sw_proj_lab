package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.map.Pipe;
import software.project.models.Player;
import software.project.models.Saboteur;
import software.project.parser.ICommand;
import software.project.core.GameState;

/**
 * Sabotages (breaks) a pipe as a saboteur action.
 */
public class SabotagePipeCommand implements ICommand {

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE GAME_NOT_INITIALIZED");
            return;
        }

        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] SABOTAGE_PIPE GAME_NOT_RUNNING");
            return;
        }

        if (args == null || args.length != 1) {
            System.out.println("[ERROR] SABOTAGE_PIPE INVALID_ARGS. Usage: SABOTAGE_PIPE <pipeId>");
            return;
        }

        String pipeId = args[0].trim();
        Pipe pipe = gameModel.getGameMap().getElement(pipeId, Pipe.class);
        if (pipe == null) {
            System.out.println("[ERROR] SABOTAGE_PIPE PIPE_NOT_FOUND " + pipeId);
            return;
        }

        Player p = gameModel.getTurnManager().getCurrentPlayer();
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
