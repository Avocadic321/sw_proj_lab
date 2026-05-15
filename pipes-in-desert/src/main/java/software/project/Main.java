package software.project;

import software.project.ui.GameApplication;

public final class Main {

    public static void main(String[] args) {
        // App app = new App();
        if (args.length == 0) {
            new GameApplication();
        } else if (args.length == 2) {
            // Disable testing mode temporarily
            // String inputFile = args[0];
            // String outputFile = args[1];
            // app.runTest(inputFile, outputFile);
        } else {
            System.out.println("[ERROR] Invalid number of arguments");
            System.exit(1);
        }
    }
}
