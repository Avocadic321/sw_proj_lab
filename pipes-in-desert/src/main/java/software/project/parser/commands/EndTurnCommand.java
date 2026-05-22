package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Ends the current player's turn and advances to the next player.
 */
public class EndTurnCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] END_TURN GAME_NOT_INITIALIZED");
            return;
        }
        if (gameModel.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] END_TURN GAME_NOT_RUNNING");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] END_TURN INVALID_ARGUMENTS");
            return;
        }

        gameModel.getTurnManager().endTurn(); // just stops the timer
        gameModel.onTurnEnded(); // triggers events + win check
        if (gameModel.getState() == GameState.RUNNING) {
            gameModel.getTurnManager().startNextTurn();
        }
        System.out.println("[OK] END_TURN");
    }
}
