package software.project.models;

import java.util.ArrayList;
import java.util.List;

import software.project.interfaces.IConnectable;

/**
 * Water source that can connect to pipe ends and produce water each turn.
 */
public class Spring extends ActiveElement implements IConnectable {
    /**
     * Base water production rate used by {@link #generateWater()}.
     */
    private int waterProductionRate;
    /**
     * Pipes currently attached to this spring.
     */
    private List<Pipe> attachedPipes = new ArrayList<>();

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
        if (waterProductionRate < 1 ||  waterProductionRate > MAX_PRODUCTION_RATE) {
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
        System.out.println("[Spring] connect()");
        if (!connections.contains(end)) {
            connections.add(end);
        }
        if (end.pipe != null && !attachedPipes.contains(end.pipe)) {
            attachedPipes.add(end.pipe);
        }
    }

    @Override
    /**
     * Disconnects a pipe end from this spring and updates tracked pipes.
     *
     * @param end pipe end to detach
     */
    public void disconnect(PipeEnd end) {
        System.out.println("[Spring] disconnect()");
        connections.remove(end);
        if (end.pipe != null) {
            attachedPipes.remove(end.pipe);
        }
    }

    @Override
    /**
     * Returns the pipe ends that are currently connected to this spring.
     *
     * @return list of connected pipe ends
     */
    public List<PipeEnd> getConnections() {
        System.out.println("[Spring] getConnections()");
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
        System.out.println("[Spring] getConnectedPipes()");
        return this.attachedPipes;
    }

    /**
     * Generates water based on the configured production rate.
     *
     * @return amount of water generated
     */
    public int generateWater() {
        System.out.println("[Spring] generateWater()");
        return waterProductionRate * 100;
    }
    @Override
    public void receiveAndTransferWater() {
     int produced = generateWater();

     List<PipeEnd> ends = getConnections();

     if(ends.isEmpty()) return;
     int perEnd = produced / ends.size();

     for(PipeEnd end: ends) {
         end.addPendingWater(perEnd);
     }
      }

    /**
     * Adds a pipe to the attached list without changing connection state.
     *
     * @param p pipe to track
     */
    public void addPipe(Pipe p) {
        attachedPipes.add(p);
    }


}
