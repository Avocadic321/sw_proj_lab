package software.project;

import software.project.core.Game;
import software.project.parser.CommandParser;

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

    public App() {
        this.parser = new CommandParser(this);
        scanner = new Scanner(System.in);
    }

    public void setGame(Game game) {
        this.game = game;

    }

    public void runInteractive() {
        System.out.println("[INFO] Interactive Mode");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("EXIT")) {
                break;
            }
            parser.parseAndExecute(line, game);
        }
    }

    public void runTest(String inputFile, String outputFile) {
        // TODO:
    }

}
