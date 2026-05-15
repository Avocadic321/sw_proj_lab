package software.project.parser;

import software.project.core.GameModel;

public interface ICommand {
    void execute(GameModel gameModel, String[] args);
}
