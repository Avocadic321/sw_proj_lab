package software.project.parser.commands;

import software.project.core.Game;
import software.project.models.Cistern;
import software.project.models.Pipe;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Pump;
import software.project.parser.CommandUtils;
import software.project.parser.ICommand;
import software.project.utils.GameState;

/**
 * Picks up a component (pipe or pump) from the ground or from a cistern.
 */
public class PickUpCommand implements ICommand {

    @Override
    public void execute(Game game, String[] args) {
        if (game == null) {
            System.out.println("[ERROR] PICK_UP GAME_NOT_INITIALIZED");
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            System.out.println("[ERROR] PICK_UP GAME_NOT_RUNNING");
            return;
        }

        if (args == null || (args.length != 1 && args.length != 2)) {
            System.out.println(
                    "[ERROR] PICK_UP INVALID_ARGS. Usage: PICK_UP <elementId> or PICK_UP <cisternId> <PIPE|PUMP>");
            return;
        }

        String id = CommandUtils.normalizeId(args[0].trim());
        Player p = game.getTurnManager().getCurrentPlayer();
        if (p == null) {
            System.out.println("[ERROR] PICK_UP NO_CURRENT_PLAYER");
            return;
        }

        if (!(p instanceof Plumber)) {
            System.out.println("[ERROR] PICK_UP NOT_A_PLUMBER");
            return;
        }

        Plumber plumber = (Plumber) p;

        // If target is a pipe or pump in the world
        Pipe pipe = CommandUtils.findElement(game, id, Pipe.class);
        if (pipe != null) {
            try {
                plumber.pickUpPipe(pipe);
                System.out.println("[OK] PICK_UP PIPE " + id);
            } catch (Exception e) {
                System.out.println("[ERROR] PICK_UP " + e.getMessage());
            }
            return;
        }

        Pump pump = CommandUtils.findElement(game, id, Pump.class);
        if (pump != null) {
            try {
                plumber.pickUpPump(pump);
                System.out.println("[OK] PICK_UP PUMP " + id);
            } catch (Exception e) {
                System.out.println("[ERROR] PICK_UP " + e.getMessage());
            }
            return;
        }

        // If target is a cistern (pickup from produced components)
        Cistern cistern = CommandUtils.findElement(game, id, Cistern.class);
        if (cistern != null) {
            if (args.length == 2) {
                String type = args[1].trim().toUpperCase();
                try {
                    if (type.equals("PIPE")) {
                        plumber.pickUpPipe(cistern);
                        System.out.println("[OK] PICK_UP PIPE_FROM " + id);
                        return;
                    }

                    if (type.equals("PUMP")) {
                        plumber.pickUpPump(cistern);
                        System.out.println("[OK] PICK_UP PUMP_FROM " + id);
                        return;
                    }

                    System.out.println("[ERROR] PICK_UP UNKNOWN_TYPE " + type);
                    return;
                } catch (Exception e) {
                    System.out.println("[ERROR] PICK_UP " + e.getMessage());
                    return;
                }
            }

            // choose whichever exists if unambiguous
            if (cistern.getStoredPipe() != null && cistern.getStoredPump() == null) {
                try {
                    plumber.pickUpPipe(cistern);
                    System.out.println("[OK] PICK_UP PIPE_FROM " + id);
                } catch (Exception e) {
                    System.out.println("[ERROR] PICK_UP " + e.getMessage());
                }
                return;
            }

            if (cistern.getStoredPump() != null && cistern.getStoredPipe() == null) {
                try {
                    plumber.pickUpPump(cistern);
                    System.out.println("[OK] PICK_UP PUMP_FROM " + id);
                } catch (Exception e) {
                    System.out.println("[ERROR] PICK_UP " + e.getMessage());
                }
                return;
            }

            System.out.println("[ERROR] PICK_UP AMBIGUOUS_OR_EMPTY_CISTERN");
            return;
        }

        System.out.println("[ERROR] PICK_UP ELEMENT_NOT_FOUND " + id);
    }
}
