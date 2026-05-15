package software.project.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for elements that can accept pipe-end connections.
 */
public abstract class ActiveElement extends Element {
    /**
     * Current pipe ends connected to this element.
     */
    protected List<PipeEnd> connections;

    /**
     * Creates an active element with default values.
     */
    protected ActiveElement() {
        super();
        this.connections = new ArrayList<>();
    }

    /**
     * Creates an active element with a given id.
     *
     * @param id element identifier
     */
    protected ActiveElement(String id) {
        super(id);
        this.connections = new ArrayList<>();
    }

    /**
     * Creates an active element with id and position.
     *
     * @param id element identifier
     * @param x  x-coordinate
     * @param y  y-coordinate
     */
    protected ActiveElement(String id, int x, int y) {
        super(id, x, y);
        this.connections = new ArrayList<>();
    }

    /**
     * Registers a pipe end as connected to this element.
     *
     * @param end pipe end to connect
     */
    public void connect(PipeEnd end) {
        if (!connections.contains(end)) {
            connections.add(end);
            end.connectsTo(this);
        }
    }

    /**
     * Removes a pipe end connection from this element.
     *
     * @param end pipe end to disconnect
     */
    public void disconnect(PipeEnd end) {
        connections.remove(end);
        if (end != null && end.connectedTo == this) {
            end.connectedTo = null;
        }
    }

    /**
     * Returns the connected pipe ends for this element.
     *
     * @return list of connected pipe ends
     */
    public List<PipeEnd> getConnections() {
        return connections;
    }

    /**
     * Active elements allow multiple players to occupy them.
     *
     * @return true always
     */
    @Override
    public boolean canOccupy() {
        return true;
    }
}
