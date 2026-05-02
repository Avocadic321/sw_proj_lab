package software.project.utils;

public final class Debug {
    private Debug() {}

    public static final boolean ENABLED = true;

    public static void log(String fmt, Object... args) {
        if (Debug.ENABLED) {
            System.out.printf("[DEBUG] " + fmt + "%n", args);
        }
    }

    public static void log(String str) {
        if (Debug.ENABLED) {
            System.out.println("[DEBUG]" + str);
        }
    }
}
