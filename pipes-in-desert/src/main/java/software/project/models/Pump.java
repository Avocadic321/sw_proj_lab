package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IConnectable;
import software.project.interfaces.IRepairable;

import java.util.List;

public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {
    public List<PipeEnd> connections;

    public PipeEnd inputPipe;
    public PipeEnd outputPipe;

    public int tankCapacity;
    public int storedWater;
    public int maxConnections;

    public boolean isBroken;

    public void setDirection(PipeEnd input, PipeEnd output) {
        System.out.println("[Pump] setDirection()");
    }
    public void transferWater() {
        System.out.println("[Pump] transferWater()");
    }

    @Override
    public void breakElement() {
        System.out.println("[Pump] breakElement()");
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pump] isBroken()");
        return false;
    }

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Pump] connect()");
    }

    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Pump] disconnect()");
    }

    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Pump] getConnections()");
        return List.of();
    }

    @Override
    public void repair() {
        System.out.println("[Pump] repair()");
    }
}
