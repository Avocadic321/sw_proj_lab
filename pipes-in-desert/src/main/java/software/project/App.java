package software.project;

import software.project.core.Game;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            switch (choice) {
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
}
