package software.project.models;

public class PipeEnd {
    public Pipe pipe;
    public ActiveElement connectedTo;

    public void connectsTo(ActiveElement element) {
        System.out.println("[PipeEnd] connectsTo()");
        connectedTo = element;
        element.connect(this);
        System.out.println("    [PipeEnd] connectedTo = " + element.id);
    }

    public void connectTo(ActiveElement element) {
        System.out.println("[PipeEnd] connectTo()");
        connectsTo(element);
    }

    public void disconnect() {
        System.out.println("[PipeEnd] disconnect()");
        if (connectedTo != null) {
            connectedTo.disconnect(this);
            connectedTo = null;
        }
        System.out.println("    [PipeEnd] connectedTo = null");
    }
    public boolean isFree() {
        System.out.println("[PipeEnd] isFree()");
        return connectedTo == null;
    }
}
