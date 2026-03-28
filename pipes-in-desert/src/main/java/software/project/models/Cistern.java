package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.List;

public class Cistern extends ActiveElement implements IConnectable {
    public int storedWater;
    public int capacity;
    public int availablePipes = 10;
    public int availablePumps = 10;

    public void receiveWater(int amount) {
        System.out.println("[Cistern] receiveWater()]");
    }

    public boolean isFull() {
        System.out.println("[Cistern] isFull()]");
        return false;
    }

    public Pipe producePipe() {
        System.out.println("[Cistern] producePipe()]");
        if (!canProducePipe()) {
            return null;
        }

        availablePipes--;
        return new Pipe();
    }

    public Pump producePump() {
        System.out.println("[Cistern] producePump()");
        if (!canProducePump()) {
            return null;
        }

        availablePumps--;
        return new Pump();
    }

    public boolean canProducePipe() {
        System.out.println("[Cistern] canProducePipe()");
        return availablePipes > 0;
    }

    public boolean canProducePump() {
        System.out.println("[Cistern] canProducePump()");
        return availablePumps > 0;
    }

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Cistern] connect()]");
    }

    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Cistern] disconnect()");
    }

    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Cistern] getConnections()");
        return this.connections;
    }
}
