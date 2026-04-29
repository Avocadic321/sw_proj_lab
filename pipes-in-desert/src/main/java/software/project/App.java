package software.project;

import software.project.core.Game;
import software.project.parser.CommandParser;

/**
 * TODO: Update for Prototype
 *
 * @author Artem Monakhov
 * @author Diego Gomes
 * @author Dimitrija Krstev
 * @author Hussein Serageldin
 * @author Ruba Alkhaldi
 * @version 1.1
 * @since 2026-03-30
 */
public class App {
    private Game game;
    private final CommandParser parser;

    public App() {
        this.parser = new CommandParser(this);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void runInteractive() {

    }

    public void runTest(String inputFile, String outputFile) {
        // TODO:
    }
}
