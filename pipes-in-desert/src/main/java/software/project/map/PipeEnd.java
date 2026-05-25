package software.project.map;

/**
 * Endpoint of a pipe that can connect to an active element.
 */
public class PipeEnd {
    /**
     * Owning pipe for this end.
     */
    public Pipe pipe;
    /**
     * Active element currently connected to this end.
     */
    public ActiveElement connectedTo;
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
            "[STATE] PIPE_END %s pipe=%s connectedTo=%s",
            id,
            pipeId,
            connectedId);
    }
}
