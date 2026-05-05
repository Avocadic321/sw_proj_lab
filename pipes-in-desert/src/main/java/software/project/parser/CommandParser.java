package software.project.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import software.project.App;
import software.project.parser.commands.*;

public class CommandParser {
    private final App app;
    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandParser(App app) {
        this.app = app;
        registerCommands(app);
    }

    private void registerCommands(App app) {
        /* Game Configuration Commands */
        commands.put("SET_GOAL", new SetGoalCommand(app));
        commands.put("SET_TURN_DURATION", new SetTurnDurationCommand(app));
        commands.put("SET_PLAYERS", new SetPlayersCommand(app));
        commands.put("SET_RANDOM", new SetRandomCommand(app));

        /* Game Control Commands */
        commands.put("NEW_GAME", new NewGameCommand(app));
        commands.put("END_TURN", new EndTurnCommand());
        commands.put("PAUSE", new PauseGameCommand());
        commands.put("RESUME", new ResumeGameCommand());
        commands.put("END_GAME", new EndGameCommand());
        commands.put("SCORE", new ScoreCommand());
        commands.put("SHOW_STATE", new ShowStateCommand());
        commands.put("FLOW", new FlowCommand());

        /* Random event commands */
        commands.put("RANDOM_BREAK", new RandomBreakCommand());
        commands.put("RANDOM_PRODUCE", new RandomProduceCommand());

        /* Player action commands */
        commands.put("MOVE", new MovePlayerCommand());
        commands.put("CONNECT", new ConnectCommand());
        commands.put("DISCONNECT", new DisconnectCommand());
        commands.put("SET_DIRECTION", new SetDirectionCommand());
        commands.put("REPAIR_PUMP", new RepairPumpCommand());
        commands.put("REPAIR_PIPE", new RepairPipeCommand());
        commands.put("SABOTAGE_PUMP", new SabotagePumpCommand());
        commands.put("SABOTAGE_PIPE", new SabotagePipeCommand());
        commands.put("PICK_UP", new PickUpCommand());
        commands.put("PICKUP_PIPE", new PickupPipeCommand());
        commands.put("PICKUP_PUMP", new PickupPumpCommand());
        commands.put("EXTEND_PIPE_SYSTEM", new ExtendPipeCommand());
        commands.put("INSERT_PUMP", new InsertPumpCommand());
    }

    public void parseAndExecute(String line) {
        if (line == null || line.trim().isEmpty())
            return;

        String[] tokens = line.trim().split("\\s+");
        String commandKey = tokens[0].toUpperCase();
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        ICommand command = commands.get(commandKey);
        if (command != null) {
            command.execute(app.getGame(), args);
        } else {
            System.out.println("[ERROR] Unknown command: " + commandKey);
        }
    }
}
