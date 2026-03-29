package software.project.models;

import software.project.core.Game;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Plumber extends Player {
    public ICarriable carriedItem;

    public void repair(IRepairable target) {
        System.out.println("[Plumber] repair()");
        if (target != null) {
            target.repair();
        }
    }
    public void setCarriedItem(ICarriable item) {
        System.out.println("[Plumber] setCarriedItem(item)");
        carriedItem = item;
    }
    public boolean repairPipe(Game game, Pipe targetPipe) {
        System.out.println("[Plumber] repairPipe(targetPipe)");
        if (game == null) {
            return false;
        }

        game.selectPipe(targetPipe);
        return game.repairPipe(targetPipe);
    }
    public boolean repairPump(Game game, Pump targetPump) {
        System.out.println("[Plumber] repairPump(targetPump)");
        if (game == null) {
            return false;
        }

        game.selectPump(targetPump);
        return game.repairPump(targetPump);
    }

    public void extendPipeSystem() {
        System.out.println("[Plumber] extendPipeSystem()");
    }

    public void pickUpPump(Cistern cistern) {
        System.out.println("[Plumber] pickUpPump() - at Cistern");
    }

    public void pickUpPump(Pump pump) {
        System.out.println("[Plumber] pickUpPump()");
        setCarriedItem(pump);
    }
    public void pickUpPipe(Cistern cistern) {
        System.out.println("[Plumber] pickUpPipe()");
    }
    public void disconnect(PipeEnd end) {
        System.out.println("[Plumber] disconnect()");
        if (end != null) {
            end.disconnect();
        }
    }
    public boolean disconnect(Game game, Pipe selectedPipe, PipeEnd selectedEnd) {
        System.out.println("[Plumber] disconnect(selectedPipe, selectedEnd)");
        if (game == null) {
            return false;
        }

        game.selectPipe(selectedPipe);
        return game.disconnect(selectedPipe, selectedEnd);
    }
    public void connect(PipeEnd end, ActiveElement tgt) {
        System.out.println("[Plumber] connect()");
        if (end != null) {
            end.connectTo(tgt);
        }
    }
    public boolean connect(Game game, Pipe selectedPipe, PipeEnd freeEnd, Element targetElement) {
        System.out.println("[Plumber] connect(selectedPipe, freeEnd, targetElement)");
        if (game == null) {
            return false;
        }

        game.selectFreePipeEnd(freeEnd);
        game.selectTargetElement(targetElement);
        return game.connect(selectedPipe, freeEnd, targetElement);
    }
    public ICarriable getCarriedItem() {
        System.out.println("[Plumber] getCarriedItem()");
        return carriedItem;
    }
    public void clearCarriedItem() {
        System.out.println("[Plumber] clearCarriedItem()");
        carriedItem = null;
    }
    public void insertPumpIntoPipe(Pump pump, Pipe pipe) {
        System.out.println("[Plumber] insertPumpIntoPipe()");
    }
    public boolean insertPumpIntoPipe(Game game, Pipe targetPipe) {
        System.out.println("[Plumber] insertPumpIntoPipe(targetPipe)");
        if (game == null) {
            return false;
        }

        game.selectPipe(targetPipe);
        return game.insertPumpIntoPipe(this, targetPipe);
    }
    public boolean requestComponent(Game game, Cistern sourceCistern) {
        System.out.println("[Plumber] requestComponent()");
        if (game == null || sourceCistern == null) {
            return false;
        }

        if (getCurrentPosition() != sourceCistern) {
            return false;
        }

        game.selectCistern(sourceCistern);
        return game.requestComponent();
    }

}
