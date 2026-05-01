package software.project.menus;

import software.project.App;
import software.project.core.GameConfig;

import java.util.Scanner;

public class ConfigMenu implements IMenu {
    private final App app;

    public ConfigMenu(App app) {
        this.app = app;
    }
    @Override
    public IMenu run() {
        Scanner sc = app.getScanner();
        while (true) {
            GameConfig config = app.getGameConfig();
            System.out.println("\n======= Configure Game =======");
            System.out.printf(
                "Current: Goal=%d Turn=%ds Players=%d Random=%s%n",
                config.getGoalScore(),
                config.getTurnDurationSeconds(),
                config.getNumberOfPlayers(),
                config.areRandomEventsEnabled() ? "ON" : "OFF"
            );
            System.out.println("------------------------------");
            System.out.println("SET_GOAL <value>");
            System.out.println("SET_TURN_DURATION <seconds>");
            System.out.println("SET_PLAYERS <count>");
            System.out.println("SET_RANDOM ON|OFF");
            System.out.println("BACK");

            System.out.println("Enter: ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("BACK")) {
                return new MainMenu(app);
            }

            app.getParser().parseAndExecute(line);
        }
    }
}
