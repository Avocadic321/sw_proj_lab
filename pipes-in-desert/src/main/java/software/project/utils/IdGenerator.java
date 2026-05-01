package software.project.utils;

import software.project.models.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class IdGenerator {
    private static final Map<Class<?>, Integer> counters = new HashMap<>();
    private static final Set<String> usedIds = new HashSet<>();

    private IdGenerator() {}

    public static String generateId(Class<?> type) {
        int next = counters.getOrDefault(type, 0) + 1;
        counters.put(type, next);
        String prefix = getPrefix(type);
        String id = prefix + next;
        usedIds.add(id);
        return id;
    }

    public static boolean isIdAvailable(String id) {
        return !usedIds.contains(id);
    }

    public static void markIdUsed(String id) {
        usedIds.add(id);
    }

    public static void reset() {
        counters.clear();
        usedIds.clear();
    }

    private static String getPrefix(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "Pipe"     -> "PIPE";
            case "Pump"     -> "PUMP";
            case "Cistern"  -> "CISTERN";
            case "Spring"   -> "SPRING";
            case "Plumber"  -> "PLUMBER";
            case "Saboteur" -> "SABOTEUR";
            default         -> "ENTITY";
        };
    }
}
