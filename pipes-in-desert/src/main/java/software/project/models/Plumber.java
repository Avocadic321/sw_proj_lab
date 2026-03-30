package software.project.models;

import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Plumber extends Player {
    public ICarriable carriedItem;

    public void repair(IRepairable target) {
        System.out.println("[Plumber] repair()");
    }

    public void extendPipeSystem() {
        System.out.println("[Plumber] extendPipeSystem()");
    }

    public void pickUpPump(Cistern cistern) {
        System.out.println("[Plumber] pickUpPump() - at Cistern");
    }

    public void pickUpPump(Pump pump) {
        System.out.println("[Plumber] pickUpPump()");
    }
    public void pickUpPipe(Cistern cistern) {
        System.out.println("[Plumber] pickUpPipe()");
    }
    public void disconnect(PipeEnd end) {
        System.out.println("[Plumber] disconnect(end)");
        end.disconnect();
        System.out.println("[Plumber] Pipe end disconnected");
    }
    public void connect(PipeEnd end, ActiveElement tgt) {
        System.out.println("[Plumber] connect()");
    }
    public void insertPumpIntoPipe(Pump pump, Pipe pipe) {
        System.out.println("[Plumber] insertPumpIntoPipe()");
    }

}
