package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IConnectable;
import software.project.interfaces.IRepairable;

import java.util.ArrayList;
import java.util.List;

public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {
    public PipeEnd inputPipe;
    public PipeEnd outputPipe;
    public Pipe selectedInputPipe;
    public Pipe selectedOutputPipe;

    public int tankCapacity;
    public int storedWater;
    public int maxConnections;

    public boolean isBroken;

    public void setDirection(PipeEnd input, PipeEnd output) {
        System.out.println("[Pump] setDirection()");
    }
    public void setDirection(Pipe leftPipe, Pipe rightPipe) {
        System.out.println("[Pump] setDirection(leftPipe,rightPipe)");
        if (leftPipe == null || rightPipe == null) {
            return;
        }

        selectedInputPipe = leftPipe;
        selectedOutputPipe = rightPipe;
        inputPipe = leftPipe.end2;
        outputPipe = rightPipe.end1;
    }

    public boolean validateSingleInputOutput(Pipe inputPipe, Pipe outputPipe) {
        System.out.println("[Pump] validateSingleInputOutput(inputPipe,outputPipe)");
        if (inputPipe == null || outputPipe == null) {
            return false;
        }
        return inputPipe != outputPipe;
    }

    public void storeDirectionConfiguration() {
        System.out.println("[Pump] storeDirectionConfiguration()");
    }
    public void transferWater() {
        System.out.println("[Pump] transferWater()");
    }

    @Override
    public void breakElement() {
        System.out.println("[Pump] breakElement()");
        isBroken = true;
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pump] isBroken()");
        return isBroken;
    }

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Pump] connect()");
        if (end == null) {
            return;
        }

        if (connections == null) {
            connections = new ArrayList<>();
        }

        if (!connections.contains(end)) {
            connections.add(end);
        }
    }

    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Pump] disconnect()");
        if (connections != null) {
            connections.remove(end);
        }
    }

    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Pump] getConnections()");
        if (connections == null) {
            connections = new ArrayList<>();
        }
        return connections;
    }

    @Override
    public void repair() {
        System.out.println("[Pump] repair()");
        isBroken = false;
    }
}
