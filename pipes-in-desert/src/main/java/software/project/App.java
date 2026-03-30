package software.project;

import java.util.Scanner;

import software.project.core.Game;
import software.project.core.GameConfig;
import software.project.models.ActiveElement;
import software.project.models.Cistern;
import software.project.models.Element;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.models.Spring;
import software.project.models.Team;
import software.project.utils.GameState;
import software.project.utils.Teams;

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
        System.out.println("\n===== UC1 Start New Game =====");
        Game game = new Game(config);
        game.startNewGameCommand();
    }

    private static void configureGameScenario() {
        System.out.println("\n===== UC2 Configure Game =====");
        Game game = new Game(config);

        game.initiateConfiguration();
        game.enterTargetScore(100);
        game.enterTurnDuration(120);
        game.setNumberOfPlayers(4);
        game.createTeams();
        game.setRealtimeScoring(true);

        if (game.validateConfiguration()) {
            game.saveConfiguration();
        }
    }

    private static void pauseResumeGameScenario() {
        System.out.println("\n===== UC3 Pause/Resume =====");
        Game game = new Game(config);
        game.state = GameState.RUNNING;

        game.pauseCommand();
        game.resumeCommand();
    }

    private static void movePlayerScenario() {
        System.out.println("\n===== UC4 Move Player =====");
        Game game = new Game(config);
        Player player = new Plumber();
        Element currentElement = new Pipe("PIPE_CURRENT");
        Element targetElement = new Pipe("PIPE_TARGET");

        player.currentPosition = currentElement;
        currentElement.addOccupant(player);

        game.selectTargetAdjacentElement(targetElement);
        game.moveTo(player, targetElement);
    }

    private static void endTurnScenario() {
        System.out.println("\n===== UC5 End Turn =====");
        Game game = new Game(config);

        game.turnManager.signalEndTurn();
        game.turnManager.timerExpired();
    }

    private static void disconnectPipeEndScenario() {
        System.out.println("\n===== UC7 Disconnect Pipe End =====");
        Game game = new Game(config);
        Pipe selectedPipe = new Pipe("PIPE1");
        PipeEnd selectedEnd = selectedPipe.end1;

        game.selectPipe(selectedPipe);
        game.disconnect(selectedPipe, selectedEnd);
    }

    private static void connectPipeEndScenario() {
        System.out.println("\n===== UC8 Connect Pipe End =====");
        Game game = new Game(config);
        Pipe selectedPipe = new Pipe("PIPE1");
        PipeEnd freeEnd = selectedPipe.end1;
        ActiveElement targetElement = new Pump("PUMP1");

        game.selectFreePipeEnd(freeEnd);
        game.selectTargetElement(targetElement);
        game.connect(selectedPipe, freeEnd, targetElement);
    }

    private static void insertPumpIntoPipeScenario() {
        System.out.println("\n===== UC9 Insert Pump Into Pipe =====");
        Game game = new Game(config);
        Plumber activePlumber = new Plumber();
        Pipe targetPipe = new Pipe("PIPE1");
        Pump carriedPump = new Pump("PUMP1");
        activePlumber.setCarriedItem(carriedPump);

        game.selectPipe(targetPipe);
        game.insertPumpIntoPipe(activePlumber, targetPipe);
    }

    private static void setPumpDirectionScenario() {
        System.out.println("\n===== UC10 Set Pump Direction =====");
        Game game = new Game(config);
        Player player = new Plumber();
        Pump targetPump = new Pump("PUMP1");
        Pipe inputPipe = new Pipe("PIPE_IN");
        Pipe outputPipe = new Pipe("PIPE_OUT");

        game.selectPump(targetPump);
        game.selectInputPipe(inputPipe);
        game.selectOutputPipe(outputPipe);
        game.setPumpDirection(player, targetPump, inputPipe, outputPipe);
    }

    private static void repairPipeScenario() {
        System.out.println("\n===== UC11 Repair Pipe =====");
        Game game = new Game(config);
        Plumber plumber = new Plumber();
        Pipe targetPipe = new Pipe("PIPE1");

        game.selectDamagedPipe(targetPipe);
        game.repairPipe(plumber, targetPipe);
    }

    private static void pickUpComponentScenario() {
        System.out.println("\n===== UC12 Pick Up Component =====");
        Game game = new Game(config);
        Cistern sourceCistern = new Cistern();
        sourceCistern.id = "CISTERN1";
        Plumber activePlumber = new Plumber();
        game.turnManager.currentPlayer = activePlumber;

        game.selectCistern(sourceCistern);
        game.requestComponent(sourceCistern, false);
        game.requestComponent(sourceCistern, true);
    }

    private static void repairPumpScenario() {
        System.out.println("\n===== UC13 Repair Pump =====");
        Game game = new Game(config);
        Plumber plumber = new Plumber();
        Pump targetPump = new Pump("PUMP1");

        game.selectBrokenPump(targetPump);
        game.repairPump(plumber, targetPump);
    }

    private static void sabotagePipeScenario() {
        System.out.println("\n===== UC14 Sabotage Pipe =====");
        Game game = new Game(config);
        Pipe targetPipe = new Pipe("PIPE1");

        game.selectPipe(targetPipe);
        game.sabotagePipe(targetPipe);
    }

    private static void calculateScoreScenario() {
        System.out.println("\n===== UC15 Calculate Score =====");
        Game game = new Game(config);

        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);
        game.addElement(new Cistern());

        game.calculateScore();
    }

    private static void processRandomEventScenario() {
        System.out.println("\n===== UC16 Process Random Event =====");
        Game game = new Game(config);

        game.addElement(new Pump("P1"));
        game.addElement(new Cistern());

        game.performRandomEvents();
    }

    private static void endGameScenario() {
        System.out.println("\n===== UC17 End Game =====");
        Game game = new Game(config);

        game.plumber = new Team(Teams.PLUMBERS);
        game.saboteur = new Team(Teams.SABOTEURS);

        game.plumber.addScore(200);
        game.saboteur.addScore(100);

        for (int i = 0; i < 2; i++) {
            game.checkWinner();
        }
    }

    private static void manageWaterflowScenario() {
        System.out.println("\n===== UC18 Manage Waterflow =====");
        Game game = new Game(config);

        Spring sourceSpring = new Spring();
        Pipe activePipe = new Pipe("PIPE1");
        Pump activePump = new Pump("PUMP1");
        Cistern targetCistern = new Cistern();

        sourceSpring.addPipe(activePipe);
        activePipe.end2.connectTo(activePump);
        activePump.outputPipe.connectTo(targetCistern);

        game.addElement(sourceSpring);
        game.addElement(activePipe);
        game.addElement(activePump);
        game.addElement(targetCistern);

        game.simulateWaterFlow();
    }
}
