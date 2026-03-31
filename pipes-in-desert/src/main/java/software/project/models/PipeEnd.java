package software.project.models;

/**
 * Endpoint of a pipe that can connect to an active element.
 */
public class PipeEnd {
    /** Owning pipe for this end. */
    public Pipe pipe;
    /** Active element currently connected to this end. */
    public ActiveElement connectedTo;

    /**
     * Connects this end to an active element and registers it.
     *
     * @param element element to connect to
     */
    public void connectsTo(ActiveElement element) {
        System.out.println("[PipeEnd] connectsTo()");
        connectedTo = element;
        element.connect(this);
        System.out.println("    [PipeEnd] connectedTo = " + element.id);
    }

    /**
     * Disconnects this end from its current element.
     */
    public void disconnect() {
        System.out.println("[PipeEnd] disconnect()");
        if (connectedTo != null) {
            connectedTo.disconnect(this);
            connectedTo = null;
        }
        System.out.println("    [PipeEnd] connectedTo = null");
    }

    /**
     * Indicates whether this end is free.
     *
     * @return true if not connected
     */
    public boolean isFree() {
        System.out.println("[PipeEnd] isFree()");
        return connectedTo == null;
    }
}
