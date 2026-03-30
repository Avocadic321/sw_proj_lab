package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Pipe extends Element implements IBreakable, IRepairable, ICarriable {
    public PipeEnd end1;
    public PipeEnd end2;

    public int capacity;
    public int currentWater;

    public boolean isBroken;

    public Pipe() {
        super();
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
    }

    public Pipe(String id) {
        super(id);
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
    }

    private void ensureEndOwners() {
        if (end1.pipe == null) {
            end1.pipe = this;
        }
        if (end2.pipe == null) {
            end2.pipe = this;
        }
    }

    public int transferWater(int amount) {
        ensureEndOwners();
        System.out.println("[Pipe] transferWater(" + amount + ")");
        return amount;
    }

    @Override
    public void breakElement() {
        System.out.println("[Pipe] breakElement()");
        this.isBroken = true;
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pipe] isBroken()");
        return this.isBroken;
    }

    @Override
    public void repair() {
        System.out.println("[Pipe] repair()");
        this.isBroken = false;
    }

    public boolean hasFreeEnd() {
        ensureEndOwners();
        System.out.println("[Pipe] checking if ends are free...");
        return end1.isFree() || end2.isFree();
    }

    public void disconnect(PipeEnd selectedEnd) {
        ensureEndOwners();
        System.out.println("[Pipe] disconnect(selectedEnd)");
        removeConnection(selectedEnd);
    }

    public void removeConnection(PipeEnd selectedEnd) {
        ensureEndOwners();
        System.out.println("[Pipe] removeConnection(selectedEnd)");
        if (selectedEnd != null) {
            selectedEnd.disconnect();
        }
    }

    public Pipe[] splitForPump(Pump carriedPump) {
        ensureEndOwners();
        System.out.println("[Pipe] splitForPump(carriedPump)");

        Pipe leftPipe = new Pipe(id == null ? "PIPE_LEFT" : id + "_LEFT");
        Pipe rightPipe = new Pipe(id == null ? "PIPE_RIGHT" : id + "_RIGHT");

        leftPipe.end2.connectTo(carriedPump);
        rightPipe.end1.connectTo(carriedPump);

        return new Pipe[]{leftPipe, rightPipe};
    }

    public Pump getNextPump() {
        ensureEndOwners();
        System.out.println("[Pipe] getNextPump()");
        if (end2.connectedTo instanceof Pump connectedPump) {
            return connectedPump;
        }
        return null; 
    }

    @Override
    public boolean canOccupy() {
        boolean can = occupants.isEmpty();
        System.out.println("[Pipe] canOccupy() -> " + can);
        return can;
    }
}
