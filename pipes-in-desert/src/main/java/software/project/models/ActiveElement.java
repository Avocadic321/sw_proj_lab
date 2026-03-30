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

    @Override
    public boolean canOccupy() {
        return true;
    }
}
