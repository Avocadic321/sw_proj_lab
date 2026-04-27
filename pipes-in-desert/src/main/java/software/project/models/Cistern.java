package software.project.models;

import java.util.List;

import software.project.interfaces.IConnectable;

/**
 * Storage element that receives water and can produce new components.
 */
public class Cistern extends ActiveElement implements IConnectable {
    /** Current amount of water stored. */
    private int storedWater;
    /** Maximum water capacity. */
    private int capacity;

    /**
     * Accepts incoming water.
     *
     * @param amount amount of water received
     */
    public void receiveWater(int amount) {
        System.out.println("[Cistern] receiveWater()");
    }

    /**
     * Indicates whether the cistern is full.
     *
     * @return true if full
     */
    public boolean isFull() {
        System.out.println("[Cistern] isFull()");
        return false;
    }

    /**
     * Produces a new pipe component.
     *
     * @return new pipe instance
     */
    public Pipe producePipe() {
        System.out.println("[Cistern] producePipe()");
        return new Pipe();
    }

    /**
     * Produces a new pump component.
     *
     * @return new pump instance
     */
    public Pump producePump() {
        System.out.println("[Cistern] producePump()");
        return new Pump();
    }

    /**
     * Connects a pipe end to this cistern.
     *
     * @param end pipe end to connect
     */
    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Cistern] connect()");
        if (!connections.contains(end)) {
            connections.add(end);
        }
    }

    /**
     * Disconnects a pipe end from this cistern.
     *
     * @param end pipe end to disconnect
     */
    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Cistern] disconnect()");
        connections.remove(end);
    }

    /**
     * Returns currently connected pipe ends.
     *
     * @return list of connected pipe ends
     */
    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Cistern] getConnections()");
        return this.connections;
    }
}
