package software.project.parser.commands;

import software.project.core.Game;
import software.project.parser.ICommand;
import software.project.utils.GameState;

public class EndTurnCommand implements ICommand {
    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            return;
        }
        if (args.length != 0) {
            return;
        }

        game.getTurnManager().endTurn();       // just stops the timer
        game.onTurnEnded();                    // triggers events + win check
        if (game.getState() == GameState.RUNNING) {
            game.getTurnManager().startNextTurn();
        }
        System.out.println("[OK] END_TURN");
    }
}
