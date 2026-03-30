package software.project.models;

public abstract class Player {
    public Element currentPosition;
    public boolean moveTo(Element target) {
        System.out.println("[Player] moveTo(targetElement)");
        currentPosition = target;
        return true;
    }

    public boolean changePumpDirection(Pump pump, Pipe in, Pipe out) {
        System.out.println("[Player] changePumpDirection(inputPipe, outputPipe)");
        if (pump == null) {
            return false;
        }
        return pump.setDirection(in, out);
    }
}
