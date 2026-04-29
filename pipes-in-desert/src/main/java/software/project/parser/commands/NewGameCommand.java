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
        System.out.println("[OK] NEW_GAME");
    }
}
