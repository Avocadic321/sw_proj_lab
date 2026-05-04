package software.project.parser;

import software.project.core.Game;
import software.project.models.Element;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Player;
import software.project.models.Team;

public final class CommandUtils {
    private CommandUtils() {}

    public static String normalizeId(String id) {
        if (id == null) {
            return null;
        }

        String value = id.trim();
        String upper = value.toUpperCase();

        if (upper.matches("PL\\d+")) {
            return "PLUMBER" + (Integer.parseInt(upper.substring(2)) - 1);
        }
        if (upper.matches("SB\\d+")) {
            return "SABOTEUR" + (Integer.parseInt(upper.substring(2)) - 1);
        }
        if (upper.matches("CIS\\d+")) {
            return "CISTERN" + upper.substring(3);
        }
        if (upper.matches("SP\\d+")) {
            return "SPRING" + upper.substring(2);
        }

        return value;
    }

    public static Player findPlayer(Game game, String id) {
        if (game == null || id == null) {
            return null;
        }

        String normalized = normalizeId(id);
        Player player = findPlayer(game.getPlumbersTeam(), normalized);
        if (player != null) {
            return player;
        }
        return findPlayer(game.getSaboteursTeam(), normalized);
    }

    private static Player findPlayer(Team team, String id) {
        if (team == null) {
            return null;
        }

        for (Player player : team.getPlayers()) {
            if (player.getId().equalsIgnoreCase(id)) {
                return player;
            }
        }
        return null;
    }

    public static Element findElement(Game game, String id) {
        if (game == null || id == null) {
            return null;
        }
        return game.getGameMap().getElement(normalizeId(id));
    }

    public static <T extends Element> T findElement(Game game, String id, Class<T> type) {
        Element element = findElement(game, id);
        if (type.isInstance(element)) {
            return type.cast(element);
        }
        return null;
    }

    public static PipeEnd findPipeEnd(Game game, String id) {
        if (game == null || id == null) {
            return null;
        }

        String normalized = normalizeId(id).toUpperCase();
        int marker = normalized.lastIndexOf("_END");
        if (marker < 0) {
            return null;
        }

        String pipeId = normalized.substring(0, marker);
        String endText = normalized.substring(marker + 4);
        Pipe pipe = findElement(game, pipeId, Pipe.class);
        if (pipe == null) {
            return null;
        }

        return switch (endText) {
            case "1" -> pipe.getEnd1();
            case "2" -> pipe.getEnd2();
            default -> null;
        };
    }

    public static PipeEnd findPipeEnd(Game game, String pipeId, String endIndex) {
        Pipe pipe = findElement(game, pipeId, Pipe.class);
        if (pipe == null || endIndex == null) {
            return null;
        }

        return switch (endIndex.trim()) {
            case "1" -> pipe.getEnd1();
            case "2" -> pipe.getEnd2();
            default -> null;
        };
    }
}
