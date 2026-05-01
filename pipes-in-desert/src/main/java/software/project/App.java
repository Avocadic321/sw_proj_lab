package software.project;

import software.project.core.Game;
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
    private Game game;
    private final CommandParser parser;
    private Scanner scanner;
    private final GameConfig gameConfig;

    public App() {
        this.parser = new CommandParser(this);
        this.gameConfig = new GameConfig();
    }

    public void setGame(Game game) { this.game = game; }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
    public CommandParser getParser() { return parser; }
    public Game getGame() { return game; }
    public Scanner getScanner() { return scanner; }

    public void runInteractive() {
        scanner = new Scanner(System.in);

        IMenu currentMenu = new MainMenu(this);
        while (currentMenu != null) {
            currentMenu = currentMenu.run();
        }
    }

    public void runTest(String inputFile, String outputFile) {
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            System.setOut(new PrintStream(buffer, true)); // auto-flush

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
                writer.print(buffer.toString());
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Could not run test: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

}
