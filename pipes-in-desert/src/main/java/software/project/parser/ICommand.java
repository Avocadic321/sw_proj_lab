package software.project.parser;

import software.project.core.Game;

public interface ICommand {
    void execute(Game game, String[] args);
}
