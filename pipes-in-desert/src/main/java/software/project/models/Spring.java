package software.project.models;

import software.project.interfaces.IConnectable;

import java.util.List;

public class Spring extends ActiveElement implements IConnectable {
    public int waterProductionRate;

    public int generateWater() {
        System.out.println("[Spring] generateWater()");
        return waterProductionRate * 100;
    }

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
        return List.of();
    }
}
