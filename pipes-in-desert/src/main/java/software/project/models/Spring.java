package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.ArrayList;
import java.util.List;

public class Spring extends ActiveElement implements IConnectable {
    public int waterProductionRate;
    private List<Pipe> attachedPipes = new ArrayList<>();

    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Spring] connect()");
    }

    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Spring] disconnect()");
    }

    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Spring] getConnections()");
        List<PipeEnd> ends = new ArrayList<>();
        for (Pipe p : attachedPipes) {
            if (p.end1 != null && p.end1.connectedTo == this) ends.add(p.end1);
            else if (p.end2 != null && p.end2.connectedTo == this) ends.add(p.end2);
        }
        return ends;
    }

    public List<Pipe> getConnectedPipes() {
        System.out.println("[Spring] getConnectedPipes()");
        return this.attachedPipes; 
    }

    public int generateWater() {
        System.out.println("[Spring] generateWater()");
        return waterProductionRate * 100;
    }

    public void addPipe(Pipe p) {
        attachedPipes.add(p);
    }
}
