package software.project;

import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.utils.GameState;
import software.project.utils.Teams;
import software.project.models.Cistern;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.models.Saboteur;
import software.project.models.Team;
import software.project.models.Spring;

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
                case 15 -> processRandomEventScenario();
                case 17 -> endGameScenario();
                case 18 -> manageWaterflowScenario();
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

        Game game = new Game();
        
        Cistern cistern1 = new Cistern();
        Cistern cistern2 = new Cistern();
        game.addElement(cistern1);
        game.addElement(cistern2);

        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        game.plumber.addPlayer(new Plumber());
        game.saboteur.addPlayer(new Saboteur());

        System.out.println("\n[Game] Starting score calculation...");
        game.calculateScore();

        System.out.println("\n===== Score Calculation Scenario Finished =====");
    }

    private static void processRandomEventScenario() {
        System.out.println("\n===== 15. Process Random Event =====");

        Game game = new Game();

        Pump pump1 = new Pump();
        Pump pump2 = new Pump();
        Cistern cistern1 = new Cistern();
        Cistern cistern2 = new Cistern();

        game.addElement(pump1);
        game.addElement(pump2);
        game.addElement(cistern1);
        game.addElement(cistern2);

        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        game.plumber.addPlayer(new Plumber());
        game.saboteur.addPlayer(new Saboteur());

        System.out.println("\n[Game] Starting random event processing...");
        game.processRandomEvent();

        System.out.println("\n===== Process Random Event Scenario Finished =====");
    }

    private static void endGameScenario() {
        System.out.println("\n===== 17. End Game =====");

        Game game = new Game();
        
        Cistern cistern1 = new Cistern();
        Cistern cistern2 = new Cistern();
        game.addElement(cistern1);
        game.addElement(cistern2);

        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        game.plumber.addPlayer(new Plumber());
        game.saboteur.addPlayer(new Saboteur());

        game.plumber.addScore(50);
        game.saboteur.addScore(30);

        System.out.println("\n[Game] Starting periodic winner check...");

        for (int i = 0; i < 3; i++) {
            System.out.println("\n[Game] Periodic check #" + (i+1));
            game.checkWinner();

            game.plumber.addScore(5);
            game.saboteur.addScore(10);

            if (game.state == GameState.FINALIZED) {
                System.out.println("\n[Game] Game has ended, stopping periodic checks.");
                break;
            }
        }

        System.out.println("\n===== End Game Scenario Finished =====");
    }

    private static void manageWaterflowScenario() {
        System.out.println("\n===== 18. Manage Waterflow =====");
        Game game = new Game();

        // 1. Create components
        Spring spring = new Spring();
        spring.waterProductionRate = 5; // Set rate so generation > 0
        
        Pipe pipe = new Pipe();
        Pump pump = new Pump();
        Cistern cistern = new Cistern();

        // 2. THE WIRING (Crucial step!)
        spring.addPipe(pipe); // Connect Spring -> Pipe
        
        pipe.end1 = new PipeEnd();
        pipe.end1.connectedTo = spring; 
        
        pipe.end2 = new PipeEnd();
        pipe.end2.connectedTo = pump; // Connect Pipe -> Pump

        pump.inputPipe = pipe.end2;
        pump.outputPipe = new PipeEnd();
        pump.outputPipe.connectedTo = cistern; // Connect Pump -> Cistern

        // 3. Add to game
        game.addElement(spring);
        game.addElement(pipe);
        game.addElement(pump);
        game.addElement(cistern);

        System.out.println("\n[Game] Starting waterflow simulation...");
        game.simulateWaterFlow();
    }
}
