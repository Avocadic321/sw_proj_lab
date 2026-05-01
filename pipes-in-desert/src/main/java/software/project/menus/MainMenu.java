package software.project.menus;

import software.project.App;

import java.util.Scanner;

public class MainMenu implements IMenu {
    private final App app;

    public MainMenu(App app) {
        this.app = app;
    }

    @Override
    public IMenu run() {
        Scanner sc = app.getScanner();
        while (true) {
            System.out.println("======= Pipes in the Desert =======");
            System.out.println("1. Start New Game");
            System.out.println("2. Configure Game");
            System.out.println("3. Exit");
            System.out.println("Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    app.getParser().parseAndExecute("NEW_GAME");
                    if (app.getGame() != null) {return new GameMenu(app);}
                    break;
                case "2":
                    return new ConfigMenu(app);
                case "3":
                    return null;
                default:
                    System.out.println("[ERROR] INVALID_CHOICE");
            }
        }
    }
}
