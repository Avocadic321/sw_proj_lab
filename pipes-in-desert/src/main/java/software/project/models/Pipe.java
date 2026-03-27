package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Pipe implements IBreakable, IRepairable, ICarriable {
    public PipeEnd end1;
    public PipeEnd end2;
    public int capacity;
    public int currentWater;
    public boolean isBroken;
    public void transferWater(){}

    @Override
    public void breakElement() {

    }

    @Override
    public boolean isBroken() {
        return false;
    }

    @Override
    public void repair() {

    }
}
