package software.project.parser;

import software.project.core.Game;
import software.project.parser.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private final Map<String, ICommand> commands = new HashMap<>();
    private final Game game;

    public CommandParser(Game game) {
        this.game = game;
        registerCommands();
    }

    public void registerCommands() {
        commands.put("NEW_GAME", new NewGameCommand());
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

    public void parseAndExecute(String line) {
        if (line == null || line.trim().isEmpty()) return;

        String[] tokens = line.trim().split("\\s+");
        String commandKey = tokens[0].toUpperCase();
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        ICommand command = commands.get(commandKey);
        if (command != null) {
            command.execute(args);
        } else {
            System.out.println("[ERROR] Unknown command: " + commandKey);
        }
    }
}
