package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.List;

public class Cistern extends ActiveElement implements IConnectable {
    public int storedWater;
    public int capacity;
    public void receiveWater(int amount) {}
    public boolean isFull() {
        return false;
    }
    public Pipe producePipe() {
        return new Pipe();
    }
    public Pump producePump() {
        return new Pump();
    }

    @Override
    public void connect(PipeEnd end) {

    }

    @Override
    public void disconnect(PipeEnd end) {

    }

    @Override
    public List<PipeEnd> getConnections() {
        return this.connections;
    }
}
