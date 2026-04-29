package software.project.parser;

import software.project.App;
import software.project.core.Game;
import software.project.parser.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandParser(App app) {
        registerCommands(app);
    }

    private void registerCommands(App app) {
        commands.put("NEW_GAME", new NewGameCommand(app));
        commands.put("CONFIGURE", null);
        commands.put("REPAIR", null);
        commands.put("SABOTAGE", null);
        commands.put("CONNECT", null);
        commands.put("DISCONNECT", null);
        commands.put("PAUSE", null);
        commands.put("RESUME", null);
        commands.put("EXIT", null);
        // Add others, add aliases, etc
    }

    public void parseAndExecute(String line, Game game) {
        if (line == null || line.trim().isEmpty()) return;

        String[] tokens = line.trim().split("\\s+");
        String commandKey = tokens[0].toUpperCase();
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        ICommand command = commands.get(commandKey);
        if (command != null) {
            command.execute(game, args);
        } else {
            System.out.println("[ERROR] Unknown command: " + commandKey);
        }
    }
}
