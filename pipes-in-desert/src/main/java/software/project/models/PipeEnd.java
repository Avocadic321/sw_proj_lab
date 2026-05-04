package software.project.models;

/**
 * Endpoint of a pipe that can connect to an active element.
 */
public class PipeEnd {
    /** Owning pipe for this end. */
    public Pipe pipe;
    /** Active element currently connected to this end. */
    public ActiveElement connectedTo;

    /** Water available in CURRENT tick (read phase) */
    private int currentWater = 0;

    /** Water written during compute phase (next tick) */
    private int pendingWater = 0;

    /**
     * Read and consume current water (used by elements during compute phase)
     */
    public int consumeWater() {
        int val = currentWater;
        currentWater = 0;
        return val;
    }

    /**
     * Write water into this end for NEXT tick (compute phase only)
     */
    public void addPendingWater(int amount) {
        if (amount <= 0)
            return;
        pendingWater += amount;
    }

    /**
     * Commit all pending water into current state (commit phase)
     */
    public void commit() {
        currentWater = pendingWater;
        pendingWater = 0;
    }

    public int getCurrentWater() {
        return currentWater;
    }

    /**
     * Connects this end to an active element and registers it.
     *
     * @param element element to connect to
     */

    public void connectsTo(ActiveElement element) {
        connectedTo = element;
        element.connect(this);
    }

    /**
     * Disconnects this end from its current element.
     */
    public void disconnect() {
        if (connectedTo != null) {
            connectedTo.disconnect(this);
            connectedTo = null;
        }
    }

    /**
     * Indicates whether this end is free.
     *
     * @return true if not connected
     */
    public boolean isFree() {
        return connectedTo == null;
    }

    @Override
    public String toString() {
        String id = "PIPE_END";
        if (pipe != null) {
            if (pipe.getEnd1() == this) {
                id = pipe.getId() + "_END1";
            } else if (pipe.getEnd2() == this) {
                id = pipe.getId() + "_END2";
            } else {
                id = pipe.getId();
            }
        }

        String pipeId = pipe == null ? "NONE" : pipe.getId();
        String connectedId = connectedTo == null ? "FREE" : connectedTo.getId();

        return String.format(
                "[STATE] PIPE_END %s pipe=%s connectedTo=%s currentWater=%d pendingWater=%d",
                id,
                pipeId,
                connectedId,
                currentWater,
                pendingWater);
    }
}
