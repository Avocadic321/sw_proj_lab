package software.project.map;

import software.project.core.GameConfig;
import software.project.map.interfaces.IConnectable;
import software.project.utils.Debug;

import java.util.List;

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
    private final int capacity;

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
        this.storedPump = new Pump();
        this.storedPipe = new Pipe();
    }

    public int getStoredWater() {
        return storedWater;
    }

    private int pendingFlowingWater;
    @Override
    public int moveWater() {
        return 0; // sink, never forwards
    }

    @Override
    public void receiveWater(int water) {
        int accepted = Math.min(water, capacity - storedWater);
        storedWater += accepted;
        pendingFlowingWater = water - accepted; // overflow staged
    }

    @Override
    public int commit() {
        int lost = pendingFlowingWater; // overflow is lost
        pendingFlowingWater = 0;
        return lost;
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
        if (storedPipe != null)
            return;
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

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        String pipeId = storedPipe == null ? "NONE" : storedPipe.getId();
        String pumpId = storedPump == null ? "NONE" : storedPump.getId();

        return String.format(
            "[STATE] CISTERN %s storedWater=%d capacity=%d storedPipe=%s storedPump=%s connections=%d",
            getId(),
            storedWater,
            capacity,
            pipeId,
            pumpId,
            getConnections().size());
    }
}
