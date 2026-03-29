package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.List;

public class Cistern extends ActiveElement implements IConnectable {
    public int storedWater;
    public int capacity;

    public void receiveWater(int amount) {
        System.out.println("[Cistern] receiveWater()");
    }

    public boolean isFull() {
        System.out.println("[Cistern] isFull()");
        return false;
    }

    public Pipe producePipe() {
        System.out.println("[Cistern] producePipe()");
        return new Pipe();
    }

    public Pump producePump() {
        System.out.println("[Cistern] producePump()");
        return new Pump();
    }

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Cistern] connect()");
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
