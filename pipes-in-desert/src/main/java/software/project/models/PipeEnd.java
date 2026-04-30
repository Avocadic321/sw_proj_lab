package software.project.models;

/**
 * Endpoint of a pipe that can connect to an active element.
 */
public class PipeEnd {
    /** Owning pipe for this end. */
    public Pipe pipe;
    /** Active element currently connected to this end. */
    public ActiveElement connectedTo;

    private int receivedWater;

    private boolean isInput;

    public boolean isInput(){
        return isInput;
    }
    public void setInput(int water) {
        isInput = true;
        receivedWater = water;
    }
    public void clearInput() {
        isInput = false;
        receivedWater = 0;
    }

    public int getReceivedWater() {
        return receivedWater;
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
}
