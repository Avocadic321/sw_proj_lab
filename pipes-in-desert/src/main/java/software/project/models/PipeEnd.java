package software.project.models;

public class PipeEnd {
    public Pipe pipe;
    public ActiveElement connectedTo;
    public void connectsTo(ActiveElement element){}
    public void disconnect(){}
    public boolean isFree() {
        return false;
    }
}
