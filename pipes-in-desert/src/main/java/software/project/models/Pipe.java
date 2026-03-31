package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

/**
 * Pipe segment that connects two pipe ends and can carry water.
 */
public class Pipe extends Element implements IBreakable, IRepairable, ICarriable {
    /** First pipe end. */
    public PipeEnd end1;
    /** Second pipe end. */
    public PipeEnd end2;

    /** Maximum water capacity. */
    public int capacity;
    /** Current water amount. */
    public int currentWater;

    /** Whether the pipe is broken. */
    public boolean isBroken;

    /** Creates a new pipe with default values. */
    public Pipe() {
        super();
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
        this.end1.pipe = this;
        this.end2.pipe = this;
    }

    /**
     * Creates a new pipe with the given identifier.
     *
     * @param id pipe id
     */
    public Pipe(String id) {
        super(id);
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
        this.end1.pipe = this;
        this.end2.pipe = this;
    }

    /**
     * Transfers water through this pipe.
     *
     * @param amount incoming water amount
     * @return forwarded water amount
     */
    public int transferWater(int amount) {
        System.out.println("[Pipe] transferWater(" + amount + ")");
        return amount;
    }

    /** Breaks the pipe, causing leakage. */
    @Override
    public void breakElement() {
        System.out.println("[Pipe] breakElement()");
        this.isBroken = true;
    }

    /**
     * Indicates whether the pipe is broken.
     *
     * @return true if broken
     */
    @Override
    public boolean isBroken() {
        System.out.println("[Pipe] isBroken()");
        return this.isBroken;
    }

    /** Repairs the pipe. */
    @Override
    public void repair() {
        System.out.println("[Pipe] repair()");
        this.isBroken = false;
    }

    /**
     * Checks whether either end is free.
     *
     * @return true if any end is free
     */
    public boolean hasFreeEnd() {
        System.out.println("[Pipe] checking if ends are free...");
        return end1.isFree() || end2.isFree();
    }

    /**
     * Splits the pipe into two segments around a pump.
     *
     * @param carriedPump pump inserted into the split
     * @return array containing left and right pipe segments
     */
    public Pipe[] splitForPump(Pump carriedPump) {
        System.out.println("[Pipe] splitForPump(carriedPump)");

        Pipe leftPipe = new Pipe(id == null ? "PIPE_LEFT" : id + "_LEFT");
        Pipe rightPipe = new Pipe(id == null ? "PIPE_RIGHT" : id + "_RIGHT");

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
        System.out.println("[Pipe] getNextPump()");
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
        System.out.println("[Pipe] canOccupy() -> " + can);
        return can;
    }
}
