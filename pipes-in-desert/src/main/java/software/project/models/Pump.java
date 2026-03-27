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

    public void setDirection(PipeEnd input, PipeEnd output) {}
    public void transferWater(){}

    @Override
    public void breakElement() {

    }

    @Override
    public boolean isBroken() {
        return false;
    }

    @Override
    public void connect(PipeEnd end) {

    }

    @Override
    public void disconnect(PipeEnd end) {

    }

    @Override
    public List<PipeEnd> getConnections() {
        return List.of();
    }

    @Override
    public void repair() {

    }
}
