package software.project.models;

public class PipeEnd {
    public Pipe pipe;
    public ActiveElement connectedTo;

    public void connectsTo(ActiveElement element) {
        System.out.println("[PipeEnd] connectsTo()");
        connectedTo = element;
    }
    public boolean connectTo(Element targetElement) {
        System.out.println("[PipeEnd] connectTo(targetElement)");
        if (!(targetElement instanceof ActiveElement activeElement)) {
            return false;
        }

        activeElement.connect(this);
        connectsTo(activeElement);
        return true;
    }
    public void disconnect() {
        System.out.println("[PipeEnd] disconnect()");
        if (connectedTo != null) {
            connectedTo.disconnect(this);
        }
        connectedTo = null;
    }
    public boolean isFree() {
        System.out.println("[PipeEnd] isFree()");
        return connectedTo == null;
    }
}
