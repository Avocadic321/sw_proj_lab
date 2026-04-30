package software.project.utils;

import software.project.models.Element;

import java.util.HashMap;
import java.util.Map;

public class IdGenerator {
    private static final Map<Class<? extends Element>, Integer> counters = new HashMap<>();

    private IdGenerator() {}

    public static String generateId(Class<? extends Element> type) {
        int next = counters.getOrDefault(type, 0) + 1;
        counters.put(type, next);
        String prefix = getPrefix(type);
        return prefix + next;
    }

    public static void reset() {
        counters.clear();
    }

    private static String getPrefix(Class<? extends Element> type) {
        return switch (type.getSimpleName()) {
            case "Pipe"     -> "PIPE";
            case "Pump"     -> "PUMP";
            case "Cistern"  -> "CISTERN";
            case "Spring"   -> "SPRING";
            default         -> "ELEM";
        };
    }
}
