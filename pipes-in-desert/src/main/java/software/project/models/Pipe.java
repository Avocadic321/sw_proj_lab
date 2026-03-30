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
        this.end1.pipe = this;
        this.end2.pipe = this;
    }

    public Pipe(String id) {
        super(id);
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
        this.end1.pipe = this;
        this.end2.pipe = this;
    }

    public int transferWater(int amount) {
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
        System.out.println("[Pipe] checking if ends are free...");
        return end1.isFree() || end2.isFree();
    }

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

        return new Pipe[]{leftPipe, rightPipe};
    }

    public Pump getNextPump() {
        System.out.println("[Pipe] getNextPump()");
        if (end2.connectedTo instanceof Pump) {
            return (Pump) end2.connectedTo;
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
