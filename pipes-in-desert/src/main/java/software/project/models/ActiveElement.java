package software.project.models;

import java.util.ArrayList;
import java.util.List;

public abstract class ActiveElement extends Element {
    public List<PipeEnd> connections;

    public ActiveElement() {
        super();
        this.connections = new ArrayList<>();
    }

    public ActiveElement(String id) {
        super(id);
        this.connections = new ArrayList<>();
    }
    public ActiveElement(String id, int x, int y) {
        super(id, x, y);
        this.connections = new ArrayList<>();
    }

    public void connect(PipeEnd end) {
        System.out.println("[ActiveElement] connect(this)");
        if (!connections.contains(end)) {
            connections.add(end);
        }
    }

    public void disconnect(PipeEnd end) {
        System.out.println("[ActiveElement] disconnect(this)");
        connections.remove(end);
    }

    public List<PipeEnd> getConnections() {
        System.out.println("[ActiveElement] getConnections()");
        return connections;
    }

    @Override
    public boolean canOccupy() {
        return true;
    }
}
