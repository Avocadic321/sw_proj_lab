package software.project.models;

public abstract class Player {
    public Element currentPosition;
    public boolean moveTo(Element target) {
        System.out.println("calling moveTo(Element target)");
        return true;
    }
    public boolean changePumpDirection(Pump pump, Pipe in, Pipe out) {
        return false;
    }
}
