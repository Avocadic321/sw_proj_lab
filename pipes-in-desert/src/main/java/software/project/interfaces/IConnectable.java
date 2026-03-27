package software.project.interfaces;

import software.project.models.PipeEnd;

import java.util.List;

public interface IConnectable {
    void connect(PipeEnd end);
    void disconnect(PipeEnd end);
    List<PipeEnd> getConnections();
}
