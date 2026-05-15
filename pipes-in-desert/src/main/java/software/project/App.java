package software.project;

import software.project.core.GameModel;
import software.project.core.GameConfig;
import software.project.menus.IMenu;
import software.project.menus.MainMenu;
import software.project.parser.CommandParser;

import java.io.*;
import java.util.Scanner;

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
    private GameModel gameModel;
    private final CommandParser parser;
    private final GameConfig gameConfig;

    public App() {
        this.parser = new CommandParser(this);
        this.gameConfig = new GameConfig();
    }

    public void setGame(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public CommandParser getParser() {
        return parser;
    }

    public GameModel getGame() {
        return gameModel;
    }

    public void runTest(String inputFile, String outputFile) {
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            System.setOut(new PrintStream(buffer, true)); // auto-flush

            gameConfig.setTestMode(true);

            try (Scanner fileScanner = new Scanner(new File(inputFile))) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine().trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    parser.parseAndExecute(line);
                }
            }

            System.out.flush();
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.print(buffer);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Could not run test: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

}
