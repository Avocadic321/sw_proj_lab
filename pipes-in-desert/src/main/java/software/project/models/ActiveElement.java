package software.project.models;

import java.util.ArrayList;
import java.util.List;

public abstract class ActiveElement extends Element {

    public List<PipeEnd> connections;

    protected ActiveElement() {
        connections = new ArrayList<>();
    }
}
