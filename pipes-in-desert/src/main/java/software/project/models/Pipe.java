package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;
import software.project.utils.Helper;
import software.project.utils.ElementWaterState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pipe segment that connects two pipe ends and can carry water.
 */
public class Pipe extends Element implements IBreakable, IRepairable, ICarriable {
    private static final int DEFAULT_CAPACITY = 1000;
    public static final int MAX_CAPACITY = 1500;

    /** First pipe end. */
    private PipeEnd end1;
    /** Second pipe end. */
    private PipeEnd end2;

    /** Maximum water capacity. */
    private int capacity;
    /** Current water amount. */
    private int currentWater;

    /** Whether the pipe is broken. */
    private boolean isBroken;

    private int waterPerTurn = 1000; // magic number

    public Pipe() {
        this(null, -1, -1, DEFAULT_CAPACITY);
    }

    public Pipe(String id) {
        this(id, -1, -1, DEFAULT_CAPACITY);
    }

    public Pipe(int x, int y) {
        this(null, x, y, DEFAULT_CAPACITY);
    }

    public Pipe(String id, int x, int y) {
        this(id, x, y, DEFAULT_CAPACITY);
    }

    public Pipe(String id, int x, int y, int capacity) {
        super(id, x, y);
        if (capacity < 1 || capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("[ERROR] PIPE INVALID_CAPACITY");
        }
        this.capacity = capacity;
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
        this.end1.pipe = this;
        this.end2.pipe = this;
    }

    public PipeEnd getEnd1() {
        return end1;
    }

    public PipeEnd getEnd2() {
        return end2;
    }

    public void connectBothEnds(ActiveElement end1Target, ActiveElement end2Target) {
        // At least one target must be non‑null
        if (end1Target == null && end2Target == null) {
            return;
        }
        // Reject connecting the same element to both ends
        if (end1Target != null && end1Target == end2Target) {
            System.out.println("[ERROR] PIPE SAME_ENDS_TARGET");
            return;
        }
        if (end1Target != null) {
            end1.connectsTo(end1Target);
        }
        if (end2Target != null) {
            end2.connectsTo(end2Target);
        }
    }

    /**
     * Transfers water through this pipe.
     *
 //    * @param amount incoming water amount
     * @return forwarded water amount
     */

    // can be modified to a better thing

    public int receiveAndTransferWater() {
        if(end1.isFree() && end2.isFree()) return 0;
        int fromA = end1.consumeWater();
        int fromB = end2.consumeWater();
        if(fromA <= 0 && fromB <= 0) return 0;
        PipeEnd outputEnd = fromA > 0 ? end2 : end1;



        ElementWaterState state = Helper.waterToBePumpedOut(fromA > 0 ? fromA : fromB,waterPerTurn,currentWater,capacity, this::breakElement);
        currentWater = state.currentlyStoredWater();
        int waterAmount = state.pumpedWater();

        if(isBroken || outputEnd.isFree()) {
            int lost = waterAmount + currentWater;
            currentWater = 0; // lose all water we hold
            System.out.printf("[EVENT] WATER_LEAK %s amount=%d", this.getId(), lost);
            return lost;
        }
        outputEnd.addPendingWater(waterAmount);
        return 0;

    }

    /** Breaks the pipe, causing leakage. */
    @Override
    public void breakElement() {
        this.isBroken = true;
    }

    /**
     * Indicates whether the pipe is broken.
     *
     * @return true if broken
     */
    @Override
    public boolean isBroken() {
        return this.isBroken;
    }

    /** Repairs the pipe. */
    @Override
    public void repair() {
        this.isBroken = false;
    }

    /**
     * Checks whether either end is free.
     *
     * @return true if any end is free
     */
    public boolean hasFreeEnd() {
        return end1.isFree() || end2.isFree();
    }

    /**
     * Splits the pipe into two segments around a pump.
     *
     * @param carriedPump pump inserted into the split
     * @return array containing left and right pipe segments
     */
    public Pipe[] splitForPump(Pump carriedPump) {
        Pipe leftPipe = new Pipe(getId() == null ? "PIPE_LEFT" : getId() + "_LEFT");
        Pipe rightPipe = new Pipe(getId() == null ? "PIPE_RIGHT" : getId() + "_RIGHT");

        leftPipe.end1.connectedTo = this.end1.connectedTo;
        if (leftPipe.end1.connectedTo != null) {
            leftPipe.end1.connectedTo.connect(leftPipe.end1);
        }

        rightPipe.end2.connectedTo = this.end2.connectedTo;
        if (rightPipe.end2.connectedTo != null) {
            rightPipe.end2.connectedTo.connect(rightPipe.end2);
        }

        leftPipe.end2.connectsTo(carriedPump);
        rightPipe.end1.connectsTo(carriedPump);

        return new Pipe[] { leftPipe, rightPipe };
    }

    /**
     * Returns the pump connected to end2 if present.
     *
     * @return next pump or null
     */
    public Pump getNextPump() {
        if (end2.connectedTo instanceof Pump) {
            return (Pump) end2.connectedTo;
        }
        return null;
    }

    /**
     * Pipes allow only one occupant at a time.
     *
     * @return true if unoccupied
     */
    @Override
    public boolean canOccupy() {
        boolean can = occupants.isEmpty();
        return can;
    }
}
