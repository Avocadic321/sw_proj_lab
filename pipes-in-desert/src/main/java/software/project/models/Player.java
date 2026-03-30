package software.project.models;

import java.util.Scanner;

/**
 * An abstract base class representing a participant in the game.
 * <p>
 * A player can move between connected elements in the pipe network and perform
 * actions depending on their role. Players belong to either the plumber team
 * or the saboteur team, as defined by the {@link Team} class.
 * </p>
 * <p>
 * Movement is restricted to adjacent elements that are directly connected
 * in the pipe network. Occupancy rules are enforced by each element's
 * {@link Element#canOccupy()} method: pipes allow at most one player at a time,
 * while pumps and other active elements may hold multiple players simultaneously.
 * </p>
 *
 * @see Element
 * @see Team
 * @see Plumber
 * @see Saboteur
 * @since 1.0
 */
public abstract class Player {
    public Element currentPosition;

    /**
     * Attempts to move the player from their current position to the specified target element.
     * <p>
     * The move is valid only if the following conditions are met:
     * <ul>
     *   <li>The target is directly connected to the current position</li>
     *   <li>The target element's {@link Element#canOccupy()} method returns {@code true}</li>
     * </ul>
     * </p>
     * <p>
     * This implementation includes interactive user input to simulate adjacency checking
     * for skeleton/verification purposes. In the final implementation, adjacency should
     * be determined automatically based on the pipe network topology.
     * </p>
     *
     * @param target the element to move to; must be directly connected to the current position
     * @return {@code true} if the move succeeded, {@code false} otherwise
     */
    public boolean moveTo(Element target) {
        System.out.println("[Player] moveTo(" + target.getClass().getSimpleName() + ")");

        // Simulate adjacency check via user input
        System.out.print("Is the target element adjacent and connected? (Y/N): ");
        boolean adjacent = new Scanner(System.in).next().equalsIgnoreCase("Y");

        if (!adjacent) {
            System.out.println("Returned: false");
            System.out.println("Cannot move: target is not adjacent or not connected.");
            return false;
        }

        // Check occupancy
        boolean canOccupy = target.canOccupy();

        if (!canOccupy) {
            System.out.println("Returned: false");
            System.out.println("Cannot move: target element is occupied (only one player per pipe).");
            return false;
        }

        // Perform the move
        if (currentPosition != null) {
            currentPosition.removeOccupant(this);
        }

        target.addOccupant(this);
        currentPosition = target;

        System.out.println("[Player] currentPosition = " + target.getClass().getSimpleName());
        System.out.println("Returned: true");
        System.out.println("Move successful.");
        return true;
    }

    /**
     * Sets or changes the direction of the specified pump.
     * <p>
     * This method allows a player to configure a pump by selecting one input pipe
     * and one output pipe. The operation succeeds only if both pipes are connected
     * to the pump and are distinct.
     * </p>
     * <p>
     * For plumbers, this is used to optimize water flow toward cisterns.
     * For saboteurs, this is used to reroute water toward disconnected pipes
     * or loops away from the destination.
     * </p>
     *
     * @param pump the pump whose direction is to be changed
     * @param in   the pipe to be designated as the water input
     * @param out  the pipe to be designated as the water output
     * @return {@code true} if the direction was successfully changed,
     *         {@code false} otherwise (e.g., if pipes are not connected
     *         to the pump or input and output are the same)
     */
    public boolean changePumpDirection(Pump pump, Pipe in, Pipe out) {
        System.out.println("[Player] changePumpDirection(pump, in, out)");
        if (pump == null || in == null || out == null) {
            return false;
        }

        PipeEnd inputEnd = in.end1 != null && in.end1.connectedTo == pump ? in.end1 : in.end2;
        PipeEnd outputEnd = out.end1 != null && out.end1.connectedTo == pump ? out.end1 : out.end2;

        return pump.setDirection(inputEnd, outputEnd);
    }
}
