package software.project.models;

public abstract class Player {
    public Element currentPosition;
    public Element getCurrentPosition() {
        System.out.println("[Player] getCurrentPosition()");
        return currentPosition;
    }
    public boolean moveTo(Element target) {
        System.out.println("[Player] moveTo(target)");
        return true;
    }
    public boolean changePumpDirection(Pump pump, Pipe in, Pipe out) {
        System.out.println("[Player] changePumpDirection(pump, in, out)");
        return false;
    }
}
