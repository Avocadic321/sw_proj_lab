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
    private static final int DEFAULT_CAPACITY = 50;
    public static final int MAX_CAPACITY = 500;

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

    private int waterPerTurn = 20; // magic number

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


    /**
     * Transfers water through this pipe.
     *
 //    * @param amount incoming water amount
     * @return forwarded water amount
     */

    // can be modified to a better thing
    private List<PipeEnd> resolveInputAndOutputEnds(PipeEnd end1, PipeEnd end2) {
        PipeEnd inputEnd = end1.isInput() ? end1
                : end2.isInput() ? end2
                : null;
        if(inputEnd == null) throw new IllegalStateException("No Input pipe");
        PipeEnd outputEnd = inputEnd == end1 ? end2 : end1;
        return new ArrayList<>(List.of(inputEnd,outputEnd));
    }
    public void receiveAndTransferWater() {
        if(end1.isFree() && end2.isFree()) return;
        List<PipeEnd> pipeEnds = resolveInputAndOutputEnds(end1,end2);
        PipeEnd inputEnd = pipeEnds.getFirst();
        PipeEnd outputEnd = pipeEnds.getLast();


        ElementWaterState state = Helper.waterToBePumpedOut(inputEnd.getReceivedWater(),waterPerTurn,currentWater,capacity, this::breakElement);
        currentWater = state.currentlyStoredWater();
        int waterAmount = state.pumpedWater();
        inputEnd.clearInput();
        if(isBroken || outputEnd.isFree()) {
            currentWater = 0; // lose all water we hold
            System.out.printf("[EVENT] WATER_LEAK %s amount=%d", this.getId(), waterAmount);
            return;
        }
        outputEnd.setInput(waterAmount);

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
