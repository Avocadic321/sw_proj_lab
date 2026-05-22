package software.project.parser.commands;

import software.project.App;
import software.project.core.GameModel;
import software.project.parser.ICommand;

/**
 * Initializes and starts a new game.
 */
public class NewGameCommand implements ICommand {
    private final App app;

    public NewGameCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (args.length != 0) {
            System.out.println("[ERROR] NEW_GAME INVALID_ARGS");
            return;
        }

        if (gameModel != null) {
            System.out.println("[WARNING] NEW_GAME GAME_INSTANCE_REPLACE");
        }

        GameModel newGameModel = new GameModel(app.getGameConfig());
        newGameModel.startGame();

        app.setGame(newGameModel);
        System.out.println("[OK] NEW_GAME");
    }
}
