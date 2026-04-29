package software.project.models;

/**
 * Represents a participant in the game.
 * <p>
 * A player can move between connected elements in the pipe network and perform
 * actions depending on their role. Players belong to either the plumber team
 * or the saboteur team, as defined by the {@link Team} class.
 * </p>
 * <p>
 * Movement is restricted to adjacent elements that are directly connected
 * in the pipe network. Occupancy rules are enforced by each element's
 * {@link Element#canOccupy()} method: pipes allow at most one player at a time,
 * while pumps and other active elements may hold multiple players
 * simultaneously.
 * </p>
 *
 * @see Element
 * @see Team
 * @see Plumber
 * @see Saboteur
 * @since 1.0
 */
public class Player {
    protected String id;
    protected Element currentPosition;

    public Player(String id, Element startPosition) {
        this.id = id;
        this.currentPosition = startPosition;
    }

    /**
     * Attempts to move the player from their current position to the specified
     * target element.
     * <p>
     * The move is valid only if the following conditions are met:
     * <ul>
     * <li>The target is directly connected to the current position</li>
     * <li>The target element's {@link Element#canOccupy()} method returns
     * {@code true}</li>
     * </ul>
     * </p>
     * 
     * @param target the element to move to; must be directly connected to the
     *               current position
     * @return {@code true} if the move succeeded, {@code false} otherwise
     */
    public boolean moveTo(Element target) {
        if (!isDirectlyConnected(currentPosition, target)) {
            System.out.println("[ERROR] MOVE INVALID_TARGET");
            return false;
        }

        if (target instanceof Pipe && !target.canOccupy()) {
            System.out.println("[ERROR] MOVE OCCUPIED");
            return false;
        }

        // Perform the move
        if (currentPosition != null) {
            currentPosition.removeOccupant(this);
        }

        target.addOccupant(this);
        currentPosition = target;

        System.out.println("[OK] MOVE " + id + " " + target.getId());
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
        if (pump == null || in == null || out == null) {
            return false;
        }

        if (in == out) {
            return false;
        }

        PipeEnd inputEnd = getEndConnectedToPump(in, pump);
        PipeEnd outputEnd = getEndConnectedToPump(out, pump);

        if (inputEnd == null || outputEnd == null) {
            return false;
        }

        if (inputEnd == outputEnd) {
            return false;
        }

        return pump.setDirection(inputEnd, outputEnd);
    }

    private boolean isDirectlyConnected(Element from, Element to) {
        if (from == null || to == null) {
            return false;
        }

        if (from instanceof Pipe pipe) {
            PipeEnd end1 = pipe.getEnd1();
            PipeEnd end2 = pipe.getEnd2();
            ActiveElement conn1 = end1 != null ? end1.connectedTo : null;
            ActiveElement conn2 = end2 != null ? end2.connectedTo : null;
            return to == conn1 || to == conn2;
        }

        if (from instanceof ActiveElement active) {
            if (!(to instanceof Pipe targetPipe)) {
                return false;
            }

            for (PipeEnd end : active.getConnections()) {
                if (end != null && end.pipe == targetPipe) {
                    return true;
                }
            }
        }

        return false;
    }

    private PipeEnd getEndConnectedToPump(Pipe pipe, Pump pump) {
        PipeEnd end1 = pipe.getEnd1();
        if (end1 != null && end1.connectedTo == pump) {
            return end1;
        }

        PipeEnd end2 = pipe.getEnd2();
        if (end2 != null && end2.connectedTo == pump) {
            return end2;
        }

        return null;
    }
}
