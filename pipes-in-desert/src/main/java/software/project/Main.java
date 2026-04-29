package software.project;

public final class Main {

    public static void main(String[] args) {
        App app = new App();
        if (args.length == 0) {
            app.runInteractive();
        } else if (args.length == 2) {
            String inputFile = args[0];
            String outputFile = args[1];
            app.runTest(inputFile, outputFile);
        } else {
            System.out.println("[ERROR] Invalid number of arguments");
        }
    }
}
