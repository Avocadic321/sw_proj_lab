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

    public void transferWater() {
        System.out.println("[Pipe] transferWater()");
    }

    public Pipe[] splitForPump(Pump carriedPump) {
        System.out.println("[Pipe] splitForPump(carriedPump)");
        if (carriedPump == null) {
            return null;
        }

        Pipe leftPipe = new Pipe();
        Pipe rightPipe = new Pipe();
        leftPipe.end1 = this.end1;
        leftPipe.end2 = newInternalEnd(leftPipe);
        rightPipe.end1 = newInternalEnd(rightPipe);
        rightPipe.end2 = this.end2;

        if (leftPipe.end1 != null) {
            leftPipe.end1.pipe = leftPipe;
        }
        if (rightPipe.end2 != null) {
            rightPipe.end2.pipe = rightPipe;
        }

        return new Pipe[]{leftPipe, rightPipe};
    }

    private PipeEnd newInternalEnd(Pipe ownerPipe) {
        PipeEnd end = new PipeEnd();
        end.pipe = ownerPipe;
        return end;
    }

    public boolean disconnect(PipeEnd selectedEnd) {
        System.out.println("[Pipe] disconnect(selectedEnd)");
        return removeConnection(selectedEnd);
    }

    public boolean removeConnection(PipeEnd selectedEnd) {
        System.out.println("[Pipe] removeConnection(selectedEnd)");
        if (selectedEnd == null) {
            return false;
        }

        if (selectedEnd != end1 && selectedEnd != end2) {
            return false;
        }

        selectedEnd.disconnect();
        return true;
    }

    @Override
    public void breakElement() {
        System.out.println("[Pipe] breakElement()");
        this.isBroken = true;
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pipe] isBroken()");
        return isBroken;
    }

    @Override
    public void repair() {
        System.out.println("[Pipe] repair()");
        isBroken = false;
    }
}
