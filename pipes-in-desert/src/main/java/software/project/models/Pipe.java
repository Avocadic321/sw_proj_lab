package software.project.models;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

public class Pipe extends Element implements IBreakable, IRepairable, ICarriable {
    public PipeEnd end1;
    public PipeEnd end2;

    public int capacity;
    public int currentWater;

    public boolean isBroken;

    public void transferWater() {
        System.out.println("[Pipe] transferWater()");
    }

    @Override
    public void breakElement() {
        System.out.println("[Pipe] breakElement()");
    }

    @Override
    public boolean isBroken() {
        System.out.println("[Pipe] isBroken()");
        
        System.out.println("Is the pipe already broken? (yes/no)");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        String answer = sc.nextLine();

        return answer.equalsIgnoreCase("yes");
    }

    @Override
    public void repair() {
        System.out.println("[Pipe] repair()");
    }
}
