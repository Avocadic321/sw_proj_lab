package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IConnectable;
import software.project.interfaces.IRepairable;

import java.util.ArrayList;
import java.util.List;

public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {
    public List<PipeEnd> connections;

    public PipeEnd inputPipe;
    public PipeEnd outputPipe;

    public int tankCapacity;
    public int storedWater;
    public int maxConnections;

    public boolean isBroken;

    public Pump() {
        super();
        connections = new ArrayList<>();
        inputPipe = new PipeEnd();
        outputPipe = new PipeEnd();
        isBroken = false;
    }

    public Pump(String id) {
        super(id);
        this.connections = new ArrayList<>();
        inputPipe = new PipeEnd();
        outputPipe = new PipeEnd();
        isBroken = false;
    }



    public void setDirection(PipeEnd input, PipeEnd output) {
        System.out.println("[Pump] setDirection()");
    }

    public int transferWater(int amount) {
        System.out.println("[Pump] transferWater(" + amount + ")");
        return amount;
    }

    @Override
    public void breakElement() {
        System.out.println("[Pump] breakElement()");
        this.isBroken = true;
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pump] isBroken()");
        return this.isBroken;
    }

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Pump] connect()");
        connections.add(end);
    }

    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Pump] disconnect()");
        connections.remove(end);
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

    public boolean isTankFull() {
        System.out.println("[Pump] isTankFull()");
        return false;
    }

    public Pipe getOutgoingPipe() {
        System.out.println("[Pump] getOutgoingPipe()");
        if (outputPipe != null && outputPipe.pipe != null) {
            return outputPipe.pipe;
        }
        return null;
    }

    public boolean isConnectedToCistern() {
        System.out.println("[Pump] isConnectedToCistern()");
        return outputPipe != null && outputPipe.connectedTo instanceof Cistern;
    }

    public Cistern getTargetCistern() {
        System.out.println("[Pump] getTargetCistern()");
        return new Cistern();
    }
}
