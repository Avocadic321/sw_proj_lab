package software.project.map;

import software.project.core.GameConfig;
import software.project.map.interfaces.IConnectable;
import software.project.utils.Debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Water source that can connect to pipe ends and produce water each turn.
 */
public class Spring extends ActiveElement implements IConnectable {
    /**
     * Base water production rate used by {@link #generateWater()}.
     */
    private final int waterProductionRate;
    /**
     * Pipes currently attached to this spring.
     */
    private final List<Pipe> attachedPipes = new ArrayList<>();

    private static final int DEFAULT_PRODUCTION_RATE = 1;
    public static final int MAX_PRODUCTION_RATE = 100;

    public Spring(int x, int y) {
        this(null, x, y, DEFAULT_PRODUCTION_RATE);
    }

    public Spring(String id, int x, int y) {
        this(id, x, y, DEFAULT_PRODUCTION_RATE);
    }

    public Spring(String id, int x, int y, int waterProductionRate) {
        super(id, x, y);
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("[ERROR] SPRING INVALID_COORDINATES");
        }
        if (waterProductionRate < 1 || waterProductionRate > MAX_PRODUCTION_RATE) {
            throw new IllegalArgumentException("[ERROR] SPRING INVALID_PRODUCTION_RATE");
        }
        this.waterProductionRate = waterProductionRate;
    }

    @Override
    /**
     * Connects a pipe end to this spring and tracks its pipe.
     *
     * @param end pipe end to attach
     */
    public void connect(PipeEnd end) {
        if (!connections.contains(end)) {
            connections.add(end);
        }
        if (end.pipe != null && !attachedPipes.contains(end.pipe)) {
            attachedPipes.add(end.pipe);
        }
        updateConnectedDirections();
    }

    @Override
    /**
     * Disconnects a pipe end from this spring and updates tracked pipes.
     *
     * @param end pipe end to detach
     */
    public void disconnect(PipeEnd end) {
        connections.remove(end);
        if (end.pipe != null) {
            attachedPipes.remove(end.pipe);
        }
        updateConnectedDirections();
    }

    @Override
    /**
     * Returns the pipe ends that are currently connected to this spring.
     *
     * @return list of connected pipe ends
     */
    public List<PipeEnd> getConnections() {
        List<PipeEnd> ends = new ArrayList<>();
        for (Pipe p : attachedPipes) {
            if (p.getEnd1() != null && p.getEnd1().connectedTo == this)
                ends.add(p.getEnd1());
            else if (p.getEnd2() != null && p.getEnd2().connectedTo == this)
                ends.add(p.getEnd2());
        }
        return ends;
    }

    /**
     * Returns the pipes tracked as attached to this spring.
     *
     * @return list of attached pipes
     */
    public List<Pipe> getConnectedPipes() {
        return this.attachedPipes;
    }

    /**
     * Generates water based on the configured production rate.
     *
     * @return amount of water generated
     */
    public int generateWater() {
        return GameConfig.SPRING_WATER_GENERATED_PER_TICK;
    }

    
    @Override
    public int moveWater() {
        List<PipeEnd> ends = getConnections();
        if (ends.isEmpty()) return 0;
        int produced = generateWater();
        Debug.log("[SPRING] %s AMOUNT GENERATED %d", this.getId(), produced);
        return produced;
    }

    @Override
    public void receiveWater(int water) {
        // spring doesn't receive
    }

    @Override
    public int commit() {
        return 0;
    }

    /**
     * Adds a pipe to the attached list without changing connection state.
     *
     * @param p pipe to track
     */
    public void addPipe(Pipe p) {
        attachedPipes.add(p);
    }

    @Override
    public String toString() {
        return String.format(
            "[STATE] SPRING %s productionRate=%d connections=%d",
            getId(),
            waterProductionRate,
            getConnections().size());
    }

    private Set<Directions> connectedDirections = EnumSet.noneOf(Directions.class);

    private Directions getDirectionFromPipe(Pipe pipe) {
        int dx = pipe.getX() - getX();
        int dy = pipe.getY() - getY();
        if (dx == 0 && dy == -1) return Directions.NORTH;
        if (dx == 1 && dy == 0)  return Directions.EAST;
        if (dx == 0 && dy == 1)  return Directions.SOUTH;
        if (dx == -1 && dy == 0) return Directions.WEST;
        return null;
    }

    public void updateConnectedDirections() {
        connectedDirections.clear();
        for (PipeEnd end : getConnections()) {
            if (end.pipe != null) {
                Directions dir = getDirectionFromPipe(end.pipe);
                if (dir != null) connectedDirections.add(dir);
            }
        }
    }

    public Set<Directions> getAvailableDirections() {
        return Collections.unmodifiableSet(connectedDirections);
    }
}
