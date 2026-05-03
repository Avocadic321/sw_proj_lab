package software.project.menus;

import java.util.Scanner;

import software.project.App;

public class GameMenu implements IMenu {
    private final App app;

    private static final String MAP_DIAGRAM = """
              S1        S2
              ||
              B1
              ||
    FE===B2===P1===B3===P3===B9===FE
              ||        ||
              B4        B8
              ||        ||
    C1===B5===P2===B6===P4===B7===C2
    """;

    public GameMenu(App app) {
        this.app = app;
        System.out.println("============= Map ==============\n");
        System.out.println(MAP_DIAGRAM);
        System.out.println("================================");
    }

    @Override
    public IMenu run() {
        Scanner sc = app.getScanner();
        while (true) {
            System.out.println("===== Game Control Commands =====");
            System.out.println("END_TURN");
            System.out.println("PAUSE");
            System.out.println("RESUME");
            System.out.println("END_GAME");
            System.out.println("SCORE");
            System.out.println("===== Random Event Commands =====");
            System.out.println("RANDOM_BREAK <pumpId>");
            System.out.println("RANDOM_PRODUCE <cisternId> <PIPE|PUMP>");
            System.out.println("===== Player Action Commands =====");
            System.out.println("MOVE <elementId>");
            System.out.println("CONNECT <pipeId> <end(1|2)> <elementId>");
            System.out.println("DISCONNECT <pipeId> <end(1|2)>");
            System.out.println("SET_DIRECTION <pumpId> <inPipeId> <outPipeId>");
            System.out.println("REPAIR_PUMP <pumpId>");
            System.out.println("SABOTAGE_PUMP <pumpId>");
            System.out.println("SABOTAGE_PIPE <pipeId>");
            System.out.println("PICK_UP <elementId>");
            System.out.println("PICK_UP <cisternId> <PIPE|PUMP>");
            System.out.println("EXTEND_PIPE_SYSTEM");
            System.out.println("===== Other Commands =====");
            System.out.println("EXIT");
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("EXIT")) {
                if (app.getGame() != null) {
                    app.getGame().endGame();
                }
                app.setGame(null);
                return new MainMenu(app);
            }
            app.getParser().parseAndExecute(line);
        }
    }
}
