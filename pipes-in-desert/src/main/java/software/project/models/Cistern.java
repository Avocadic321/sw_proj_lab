package software.project.models;

import java.util.List;

import software.project.core.GameConfig;
import software.project.interfaces.IConnectable;

/**
 * Storage element that receives water and can produce new components.
 */
public class Cistern extends ActiveElement implements IConnectable {
    /**
     * Current amount of water stored.
     */
    private int storedWater;
    /**
     * Maximum water capacity.
     */
    private int capacity;

    /**
     * Currently stored pipe
     */
    private Pipe storedPipe;

    /**
     * Currently stored pump
     */
    private Pump storedPump;


    public Cistern(int x, int y) {
        this(null, x, y, GameConfig.CISTERN_DEFAULT_CAPACITY);
    }

    public Cistern(String id, int x, int y) {
        this(id, x, y, GameConfig.CISTERN_DEFAULT_CAPACITY);
    }

    public Cistern(String id, int x, int y, int capacity) {
        super(id, x, y);
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("[ERROR] CISTERN INVALID_COORDINATES");
        }
        if (capacity < 1 || capacity > GameConfig.CISTERN_MAX_CAPACITY) {
            throw new IllegalArgumentException("[ERROR] CISTERN INVALID_CAPACITY");
        }
        this.capacity = capacity;
        this.storedWater = 0;
    }

    public int getStoredWater() {
        return storedWater;
    }

    /**
     * Accepts incoming water.
     * <p>
     * //  * @param amount amount of water received
     */
    @Override
    public int receiveAndTransferWater() {
        int leaked = 0;
        for (PipeEnd end : getConnections()) {
            int incoming = end.consumeWater();
            if (incoming <= 0) continue;
            int accepted = Math.min(incoming, capacity - storedWater);
            storedWater += accepted;
            int overflow = incoming - accepted;
            System.out.println("[CISTERN] OVERFLOW " + overflow);
            leaked += overflow;

        }
        return leaked;
    }

    /**
     * Indicates whether the cistern is full.
     *
     * @return true if full
     */
    public boolean isFull() {
        return storedWater >= capacity;
    }

    public Pipe getStoredPipe() {
        return storedPipe;
    }

    public Pump getStoredPump() {
        return storedPump;
    }

    /**
     * Produces a new pipe component.
     *
     * @return new pipe instance
     */
    public void producePipe() {
        if (storedPipe != null) return;
        System.out.println("[Cistern] producePipe()");
        Pipe pipe = new Pipe();
        this.connect(pipe.getEnd1());
        this.storedPipe = pipe;
    }

    public Pipe pickUpPipe() {
        Pipe pipe = storedPipe;
        pipe.getEnd1().disconnect();
        this.disconnect(pipe.getEnd1());
        storedPipe = null;
        return pipe;
    }

    /**
     * Produces a new pump component.
     *
     * @return new pump instance
     */
    public void producePump() {
        System.out.println("[Cistern] producePump()");
        this.storedPump = new Pump();
    }

    public Pump pickUpPump() {
        Pump pump = storedPump;
        storedPump = null;
        return pump;
    }


    /**
     * Connects a pipe end to this cistern.
     *
     * @param end pipe end to connect
     */
    @Override
    public void connect(PipeEnd end) {
        super.connect(end);
    }

    /**
     * Disconnects a pipe end from this cistern.
     *
     * @param end pipe end to disconnect
     */
    @Override
    public void disconnect(PipeEnd end) {
        super.disconnect(end);
    }

    /**
     * Returns currently connected pipe ends.
     *
     * @return list of connected pipe ends
     */
    @Override
    public List<PipeEnd> getConnections() {
        return super.getConnections();
    }
}
