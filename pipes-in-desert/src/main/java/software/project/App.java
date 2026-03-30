package software.project;

import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.models.*;
import software.project.utils.GameState;
import software.project.utils.Teams;

import java.util.Scanner;

public class App {
    public static final Scanner scanner = new Scanner(System.in);
    private static final GameConfig config = new GameConfig();

    public static void main(String[] args) {

        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> startNewGameScenario();
                case 2 -> configureGameScenario();
                case 3 -> pauseResumeGameScenario();
                case 4 -> movePlayerScenario();
                case 5 -> endTurnScenario();
                case 6 -> disconnectPipeEndScenario();
                case 7 -> connectPipeEndScenario();
                case 8 -> insertPumpIntoPipeScenario();
                case 9 -> setPumpDirectionScenario();
                case 10 -> repairPipeScenario();
                case 11 -> pickUpComponentScenario();
                case 12 -> repairPumpScenario();
                case 13 -> sabotagePipeScenario();
                case 14 -> calculateScoreScenario();
                case 15 -> processRandomEventScenario();
                case 16 -> endGameScenario();
                case 17 -> manageWaterflowScenario();
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
        System.out.println("15. Process Random Events");
        System.out.println("16. End Game");
        System.out.println("17. Manage Waterflow");
        System.out.println("0. Exit");
    }

    private static void startNewGameScenario() {
        System.out.println("\n===== 1. Start New Game =====");
        Game game = new Game(config);
        game.startGame();
        System.out.println("Game Initialized Successfully");
    }

    private static void configureGameScenario() {
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

    private static void pauseResumeGameScenario() {
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

    private static void movePlayerScenario() {
        System.out.println("\n===== 4. Move Player =====");

        // Create a player (the one who will move)
        Player player = new Plumber();

        // Create elements: one origin, two targets
        Element origin = new Pipe("PIPE_ORIGIN");
        Element freeTarget = new Pipe("PIPE_FREE");
        Element occupiedTarget = new Pipe("PIPE_OCCUPIED");

        // Occupied target already has an occupant
        Player occupant = new Plumber();
        occupiedTarget.addOccupant(occupant);
        occupant.currentPosition = occupiedTarget;

        // Place player at origin
        player.currentPosition = origin;
        origin.addOccupant(player);

        // Show available targets
        System.out.println("Current position: " + origin.id);
        System.out.println("Available targets:");
        System.out.println("  - " + freeTarget.id + " (FREE)");
        System.out.println("  - " + occupiedTarget.id + " (OCCUPIED)");
        System.out.print("Enter target element ID: ");
        String targetId = scanner.next();

        // Find the selected target
        Element selected = null;
        if (targetId.equals(freeTarget.id)) {
            selected = freeTarget;
        } else if (targetId.equals(occupiedTarget.id)) {
            selected = occupiedTarget;
        } else if (targetId.equals(origin.id)) {
            selected = origin; // can stay, but we treat as moving to itself (invalid)
        }

        if (selected == null) {
            System.out.println("Target not found.");
            return;
        }

        // Perform the move – the method will ask about adjacency and handle occupancy
        player.moveTo(selected);
    }

    private static void endTurnScenario() {
        System.out.println("\n===== 5. End Turn =====");
        Game game = new Game(config);

        game.turnManager.startTurn();

        System.out.println("How does the turn end?");
        System.out.println("1. Player performs action (completes turn)");
        System.out.println("2. Time expires");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("[Player] action()");
            game.turnManager.endTurn();
        } else if (choice == 2) {
            game.turnManager.timeExpired();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void disconnectPipeEndScenario() {
        System.out.println("\n===== Disconnect Pipe End =====");

        // Create a plumber
        Plumber plumber = new Plumber();

        // Create a pipe end (normally part of a pipe)
        Pipe pipe = new Pipe("PIPE1");
        PipeEnd end = pipe.end1;

        // Optionally, connect it to something to show disconnection
        ActiveElement dummy = new Pump("PUMP1");
        end.connectsTo(dummy);

        // Call disconnect
        plumber.disconnect(end);
    }

    private static void connectPipeEndScenario() {
        System.out.println("\n===== Connect Pipe End =====");

        Game game = new Game(config);
        Plumber plumber = new Plumber();

        Pipe selectedPipe = new Pipe("PIPE1");
        ActiveElement targetPump = new Pump("PUMP1");
        ActiveElement targetCistern = new Cistern();
        targetCistern.id = "CISTERN1";
        ActiveElement targetSpring = new Spring();
        targetSpring.id = "SPRING1";
        ((Pump) targetPump).maxConnections = 2;

        game.addElement(selectedPipe);
        game.addElement(targetPump);
        game.addElement(targetCistern);
        game.addElement(targetSpring);

        System.out.println("Select free pipe end:");
        System.out.println("1. end1");
        System.out.println("2. end2");
        int endChoice = scanner.nextInt();
        PipeEnd freeEnd = endChoice == 2 ? selectedPipe.end2 : selectedPipe.end1;

        System.out.println("Select target element:");
        System.out.println("1. PUMP1");
        System.out.println("2. CISTERN1");
        System.out.println("3. SPRING1");
        int targetChoice = scanner.nextInt();

        ActiveElement targetElement = switch (targetChoice) {
            case 1 -> targetPump;
            case 2 -> targetCistern;
            case 3 -> targetSpring;
            default -> null;
        };

        if (targetElement == null) {
            System.out.println("Invalid target selection.");
            return;
        }

        boolean success = game.connect(selectedPipe, freeEnd, targetElement);
        if (success) {
            System.out.println("Pipe end connected successfully.");
        } else {
            System.out.println("Connection rejected.");
        }
    }

    private static void insertPumpIntoPipeScenario() {
        System.out.println("\n===== Insert Pump Into Pipe =====");

        Game game = new Game(config);
        Plumber activePlumber = new Plumber();
        Pipe targetPipe = new Pipe("PIPE1");
        Pump carriedPump = new Pump("PUMP1");

        ActiveElement leftElement = new Spring();
        leftElement.id = "SPRING1";
        ActiveElement rightElement = new Cistern();
        rightElement.id = "CISTERN1";

        targetPipe.end1.connectsTo(leftElement);
        targetPipe.end2.connectsTo(rightElement);
        activePlumber.carriedItem = carriedPump;

        game.addElement(leftElement);
        game.addElement(rightElement);
        game.addElement(targetPipe);

        System.out.println("[Plumber] selecting a pipe");
        game.selectPipe(targetPipe);

        boolean success = game.insertPumpIntoPipe(activePlumber, targetPipe);
        if (success) {
            System.out.println("The pump was inserted into the pipe.");
        } else {
            System.out.println("Pump insertion rejected.");
        }
    }

    private static void setPumpDirectionScenario() {
        System.out.println("\n===== Set Pump Direction =====");

        Game game = new Game(config);
        Player player = new Plumber();
        Pump targetPump = new Pump("PUMP1");
        Pipe inputPipe = new Pipe("PIPE_IN");
        Pipe outputPipe = new Pipe("PIPE_OUT");

        inputPipe.end2.connectsTo(targetPump);
        outputPipe.end1.connectsTo(targetPump);

        game.addElement(targetPump);
        game.addElement(inputPipe);
        game.addElement(outputPipe);

        System.out.println("[Player] selectPump(targetPump)");
        System.out.println("Available pipes:");
        System.out.println("1. PIPE_IN");
        System.out.println("2. PIPE_OUT");

        System.out.print("Select input pipe (1/2): ");
        int inputChoice = scanner.nextInt();
        Pipe selectedInput = inputChoice == 2 ? outputPipe : inputPipe;

        System.out.print("Select output pipe (1/2): ");
        int outputChoice = scanner.nextInt();
        Pipe selectedOutput = outputChoice == 1 ? inputPipe : outputPipe;

        boolean success = game.setPumpDirection(player, targetPump, selectedInput, selectedOutput);
        if (success) {
            System.out.println("Pump direction updated.");
        } else {
            System.out.println("Invalid input/output selection.");
        }
    }

    private static void repairPipeScenario() {
        System.out.println("\n===== Repair Pipe =====");

        Game game = new Game(config);
        Plumber plumber = new Plumber();
        Pipe targetPipe = new Pipe("PIPE1");

        System.out.print("Is the selected pipe broken? (y/n): ");
        boolean broken = scanner.next().equalsIgnoreCase("y");
        if (broken) {
            targetPipe.breakElement();
        }

        System.out.println("[Plumber] selectDamagedPipe(targetPipe)");
        game.selectPipe(targetPipe);

        boolean repaired = game.repairPipe(plumber, targetPipe);
        if (repaired) {
            System.out.println("Pipe repaired successfully.");
        } else {
            System.out.println("Pipe is not broken.");
        }
    }

    private static void pickUpComponentScenario() {
        System.out.println("\n===== Pick Up Component =====");

        Game game = new Game(config);
        Cistern sourceCistern = new Cistern();
        sourceCistern.id = "CISTERN1";
        Plumber activePlumber = new Plumber();
        game.turnManager.currentPlayer = activePlumber;

        game.addElement(sourceCistern);

        System.out.println("[Plumber] selectCistern(sourceCistern)");
        game.selectCistern(sourceCistern);

        System.out.println("What component do you want to pick up?");
        System.out.println("1. Pipe");
        System.out.println("2. Pump");
        int choice = scanner.nextInt();

        boolean requestPump = choice == 2;
        boolean received = game.requestComponent(sourceCistern, requestPump);
        if (received) {
            System.out.println("Component received.");
        } else {
            System.out.println("No component received.");
        }
    }

    private static void repairPumpScenario() {
        System.out.println("\n===== Repair Pump =====");

        Game game = new Game(config);
        Plumber plumber = new Plumber();
        Pump targetPump = new Pump("PUMP1");

        System.out.print("Is the selected pump broken? (y/n): ");
        boolean broken = scanner.next().equalsIgnoreCase("y");
        if (broken) {
            targetPump.breakElement();
        }

        System.out.println("[Plumber] selectBrokenPump(targetPump)");
        game.selectPump(targetPump);

        boolean repaired = game.repairPump(plumber, targetPump);
        if (repaired) {
            System.out.println("Pump repaired successfully.");
        } else {
            System.out.println("Pump is not broken.");
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
