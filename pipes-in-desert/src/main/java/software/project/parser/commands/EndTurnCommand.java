package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Ends the current player's turn and advances to the next player.
 */
public class EndTurnCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] END_TURN GAME_NOT_INITIALIZED");
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] END_TURN GAME_NOT_RUNNING");
            return;
        }
        if (args.length != 0) {
            System.out.println("[ERROR] END_TURN INVALID_ARGUMENTS");
            return;
        }

        game.getTurnManager().endTurn(); // just stops the timer
        game.onTurnEnded(); // triggers events + win check
        if (game.getState() == GameState.RUNNING) {
            game.getTurnManager().startNextTurn();
        }
        System.out.println("[OK] END_TURN");
    }
}
