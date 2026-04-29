package software.project.menus;

import software.project.App;

import java.util.Scanner;

public class GameMenu implements IMenu {
    private final App app;

    public GameMenu(App app) {
        this.app = app;
    }

    @Override
    public IMenu run() {
        Scanner sc = app.getScanner();
        while (true) {
            System.out.println("\n===== AVAILABLE ACTIONS =====");
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
            System.out.println("SHOW_STATE  – show current game state");
            System.out.println("EXIT – quit to main menu");
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("EXIT")) {
                return new MainMenu(app);
            }
            app.getParser().parseAndExecute(line, app.getGame());
        }
    }
}
