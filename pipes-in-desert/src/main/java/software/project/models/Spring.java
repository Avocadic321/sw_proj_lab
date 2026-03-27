package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.List;

public class Spring extends ActiveElement implements IConnectable {
    public int waterProductionRate;
    public int generateWater() {
        return waterProductionRate * 100;
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
}
