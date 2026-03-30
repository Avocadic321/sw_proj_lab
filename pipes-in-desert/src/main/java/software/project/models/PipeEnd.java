package software.project.models;

public class PipeEnd {
    public Pipe pipe;
    public ActiveElement connectedTo;

    public void connectsTo(ActiveElement element) {
        System.out.println("[PipeEnd] connectsTo()");
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
        return false;
    }
}
