package software.project.parser.commands;

import software.project.App;
import software.project.core.Game;
import software.project.parser.ICommand;

public class NewGameCommand implements ICommand {
    private final App app;

    public NewGameCommand(App app) {
        this.app = app;
    }

    @Override
    public void execute(Game game, String[] args) {
        if (args.length != 0) {
            System.out.println("[ERROR] NEW_GAME INVALID_ARGS");
            return;
        }

        if (game != null) {
            System.out.println("[WARNING] NEW_GAME: Replacing existing Game instance");
        }

        Game newGame = new Game(app.getGameConfig());
        newGame.startGame();

        app.setGame(newGame);
        System.out.println("[OK] NEW_GAME");
    }
}
