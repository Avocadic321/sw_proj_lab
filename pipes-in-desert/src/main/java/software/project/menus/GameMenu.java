package software.project.menus;

import software.project.App;

import java.util.Scanner;

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
            System.out.println("\n===== Available Actions =====");
            System.out.println("MOVE <playerId> <elementId>");
            System.out.println("REPAIR_PIPE <playerId> <pipeId>");
            System.out.println("REPAIR_PUMP <playerId> <pumpId>");
            System.out.println("SET_DIRECTION <playerId> <pumpId> <in> <out>");
            System.out.println("CONNECT <playerId> <pipeEnd> <element>");
            System.out.println("DISCONNECT <playerId> <pipeEnd>");
            System.out.println("PICKUP_PIPE <playerId> <cistern>");
            System.out.println("PICKUP_PUMP <playerId> <cistern> <maxConn>");
            System.out.println("INSERT_PUMP <playerId> <pipe> <pump>");
            System.out.println("END_TURN");
            System.out.println("PAUSE");
            System.out.println("SHOW_STATE");
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
