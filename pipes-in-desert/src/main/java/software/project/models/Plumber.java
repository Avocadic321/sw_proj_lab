package software.project.models;

import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Plumber extends Player {
    public ICarriable carriedItem;
    public void repair(IRepairable target){}
    public void extendPipeSystem() {}
    public void pickUpPump(Cistern cistern) {}
    public void pickUpPump(Pump pump){}
    public void pickUpPipe(Cistern cistern){}
    public void disconnect(PipeEnd end){}
    public void connect(PipeEnd end, ActiveElement tgt){}
    public void insertPumpIntoPipe(Pump pump, Pipe pipe){}

}
