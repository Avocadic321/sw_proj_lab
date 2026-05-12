package software.project.utils;

import java.io.PrintStream;

public final class Debug {
    private Debug() {
    }

    public static final boolean ENABLED = false;

    private static PrintStream output = System.out;

    public static void setOutput(PrintStream stream) {
        if (stream != null) {
            output = stream;
        }
    }

    public static void useStdout() {
        output = System.out;
    }

    public static void useStderr() {
        output = System.err;
    }

    public static void log(String fmt, Object... args) {
        if (ENABLED) {
            output.printf("[DEBUG] " + fmt + "%n", args);
        }
    }

    public static void log(String str) {
        if (ENABLED) {
            output.println("[DEBUG] " + str);
        }
    }
}
