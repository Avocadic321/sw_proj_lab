package software.project;

import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.utils.GameState;
import software.project.utils.Teams;
import software.project.models.Cistern;
import software.project.models.Pipe;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.models.Team;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GameConfig config = new GameConfig();

    public static void main(String[] args) {

        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> startNewGame();
                case 2 -> configureGame();
                case 3 -> pauseResumeGame();
                case 13 -> sabotagePipeScenario();
                case 14 -> calculateScoreScenario();
                // Add corresponding scenarios...
                case 0 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n===== Game Skeleton Menu =====");
        System.out.println("1. Start New Game");
        System.out.println("2. Configure Game");
        System.out.println("3. Pause/Resume Game");
        System.out.println("4. Move Player");
        System.out.println("5. End Turn");
        System.out.println("6. Disconnect Pipe End");
        System.out.println("7. Connect Pipe End");
        System.out.println("8. Insert Pump into Pipe");
        System.out.println("9. Set Pump Direction");
        System.out.println("10. Repair Pipe");
        System.out.println("11. Pick Up Component");
        System.out.println("12. Repair Pump");
        System.out.println("13. Sabotage Pipe");
        System.out.println("14. Calculate Score");
        System.out.println("15. Break Random Pumps");
        System.out.println("16. Generate New Components");
        System.out.println("17. End Game");
        System.out.println("18. Manage Waterflow");
        System.out.println("0. Exit");
    }

    private static void startNewGame() {
        System.out.println("\n===== 1. Start New Game =====");
        Game game = new Game(config);
        game.startGame();
        System.out.println("Game Initialized Successfully");
    }

    private static void configureGame() {
        System.out.println("\n===== Configure Game =====");

        System.out.print("Input new goal score: ");
        int goal = scanner.nextInt();
        config.setGoalScore(goal);

        System.out.print("Input turn duration in seconds: ");
        int duration = scanner.nextInt();
        config.setTurnDurationSeconds(duration);

        System.out.print("Enable real-time scoring? (y/n): ");
        String input = scanner.next();
        boolean realTime = input.equalsIgnoreCase("y");
        config.setRealTimeScoring(realTime);

        System.out.print("How many players will participate? ");
        int players = scanner.nextInt();
        config.setNumberOfPlayers(players);

        System.out.println(config);

        System.out.println("===== Game Configuration Finalized =====");
    }

    private static void pauseResumeGame() {
        System.out.println("\n===== 3. Pause/Resume Game =====");
        Game game = new Game();
        game.state = GameState.RUNNING;

        while (true) {
            System.out.println("\n1. Pause Game\n2. Resume Game\n3. Exit Scenario\n");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> game.pauseGame();
                case 2 -> game.resumeGame();
                case 3 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }

    }

    private static void sabotagePipeScenario() {
        System.out.println("\n===== 13. Sabotage Pipe =====");

        Game game = new Game(config);
        Pipe pipe = new Pipe();

        System.out.println("[Saboteur] selecting a pipe");
        game.selectPipe(pipe);

        System.out.println("[Saboteur] sabotaging the pipe");
        game.sabotagePipe(pipe);

        System.out.println("===== Sabotage Scenario Finished =====");
    }

    private static void calculateScoreScenario() {
        System.out.println("\n===== 14. Calculate Score =====");

        // Create a Game with default config
        Game game = new Game();
        
        // Add some elements for demo
        Cistern cistern1 = new Cistern();
        Cistern cistern2 = new Cistern();
        game.addElement(cistern1);
        game.addElement(cistern2);

        // Initialize teams
        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        // Add dummy players
        game.plumber.addPlayer(new Plumber());
        game.saboteur.addPlayer(new Saboteur());

        System.out.println("\n[Game] Starting score calculation...");
        game.calculateScore();  // this will print all steps

        System.out.println("\n===== Score Calculation Scenario Finished =====");
    }
}
