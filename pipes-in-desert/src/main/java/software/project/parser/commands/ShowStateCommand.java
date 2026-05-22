package software.project.parser.commands;

import software.project.core.GameModel;
import software.project.map.*;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.models.Team;
import software.project.parser.ICommand;

/**
 * Displays the current state of the game or a specific game element.
 */
public class ShowStateCommand implements ICommand {
    @Override
    public void execute(GameModel gameModel, String[] args) {
        if (gameModel == null) {
            System.out.println("[ERROR] SHOW_STATE GAME_NOT_INITIALIZED");
            return;
        }

        if (args == null) {
            System.out.println("[ERROR] SHOW_STATE INVALID_ARGS");
            return;
        }

        if (args.length == 0) {
            printAllStates(gameModel);
            return;
        }

        if (args.length != 1 && args.length != 2) {
            System.out.println("[ERROR] SHOW_STATE INVALID_ARGS");
            return;
        }

        String expectedType = null;
        String objectId;
        if (args.length == 2) {
            expectedType = normalizeType(args[0]);
            objectId = args[1];
        } else {
            objectId = args[0];
        }

        Object target = resolveById(gameModel, objectId);
        if (target == null) {
            System.out.println("[ERROR] SHOW_STATE OBJECT_NOT_FOUND " + objectId);
            return;
        }

        if (expectedType != null && !isTypeMatch(target, expectedType)) {
            System.out.printf("[ERROR] SHOW_STATE TYPE_MISMATCH expected=%s actual=%s%n",
                    expectedType,
                    getObjectType(target));
            return;
        }

        System.out.println(target.toString());
    }

    private void printAllStates(GameModel gameModel) {
        System.out.println(gameModel.toString());

        Team plumbers = gameModel.getPlumbersTeam();
        if (plumbers != null) {
            System.out.println(plumbers.toString());
            for (Player player : plumbers.getPlayers()) {
                System.out.println(player.toString());
            }
        }

        Team saboteurs = gameModel.getSaboteursTeam();
        if (saboteurs != null) {
            System.out.println(saboteurs.toString());
            for (Player player : saboteurs.getPlayers()) {
                System.out.println(player.toString());
            }
        }

        for (Element element : gameModel.getGameMap().getElements()) {
            System.out.println(element.toString());
            if (element instanceof Pipe pipe) {
                System.out.println(pipe.getEnd1().toString());
                System.out.println(pipe.getEnd2().toString());
            }
        }
    }

    private Object resolveById(GameModel gameModel, String objectId) {
        if (objectId == null || objectId.isBlank()) {
            return null;
        }

        String normalizedId = objectId.trim();
        if ("GAME".equalsIgnoreCase(normalizedId)) {
            return gameModel;
        }

        Team plumbers = gameModel.getPlumbersTeam();
        Team saboteurs = gameModel.getSaboteursTeam();

        if (plumbers != null && "PLUMBERS".equalsIgnoreCase(normalizedId)) {
            return plumbers;
        }
        if (saboteurs != null && "SABOTEURS".equalsIgnoreCase(normalizedId)) {
            return saboteurs;
        }

        Player player = findPlayerById(plumbers, normalizedId);
        if (player != null) {
            return player;
        }

        player = findPlayerById(saboteurs, normalizedId);
        if (player != null) {
            return player;
        }

        Element element = gameModel.getGameMap().getElement(normalizedId);
        if (element != null) {
            return element;
        }

        return findPipeEndById(gameModel, normalizedId);
    }

    private Player findPlayerById(Team team, String playerId) {
        if (team == null) {
            return null;
        }

        for (Player player : team.getPlayers()) {
            if (player.getId().equalsIgnoreCase(playerId)) {
                return player;
            }
        }

        return null;
    }

    private PipeEnd findPipeEndById(GameModel gameModel, String pipeEndId) {
        for (Pipe pipe : gameModel.getGameMap().getAllPipes()) {
            String end1Id = pipe.getId() + "_END1";
            if (end1Id.equalsIgnoreCase(pipeEndId)) {
                return pipe.getEnd1();
            }

            String end2Id = pipe.getId() + "_END2";
            if (end2Id.equalsIgnoreCase(pipeEndId)) {
                return pipe.getEnd2();
            }
        }

        return null;
    }

    private String normalizeType(String type) {
        return type == null ? "" : type.trim().toUpperCase();
    }

    private boolean isTypeMatch(Object object, String expectedType) {
        return switch (expectedType) {
            case "GAME" -> object instanceof GameModel;
            case "TEAM" -> object instanceof Team;
            case "PLAYER" -> object instanceof Player;
            case "PLUMBER" -> object instanceof Plumber;
            case "SABOTEUR" -> object instanceof Saboteur;
            case "PIPE" -> object instanceof Pipe;
            case "PUMP" -> object instanceof Pump;
            case "CISTERN" -> object instanceof Cistern;
            case "SPRING" -> object instanceof Spring;
            case "PIPE_END", "PIPEEND" -> object instanceof PipeEnd;
            default -> false;
        };
    }

    private String getObjectType(Object object) {
        if (object instanceof PipeEnd) {
            return "PIPE_END";
        }

        if (object == null) {
            return "NULL";
        }

        return object.getClass().getSimpleName().toUpperCase();
    }
}
