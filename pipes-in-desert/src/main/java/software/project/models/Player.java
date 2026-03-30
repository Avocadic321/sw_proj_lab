package software.project.models;

import software.project.App;

import java.util.Scanner;

public abstract class Player {
    public Element currentPosition;
    public boolean moveTo(Element target) {
        System.out.println("[Player] moveTo(" + target.getClass().getSimpleName() + ")");

        // Simulate adjacency check via user input
        System.out.print("Is the target element adjacent and connected? (Y/N): ");
        boolean adjacent = new Scanner(System.in).next().equalsIgnoreCase("Y");

        if (!adjacent) {
            System.out.println("Returned: false");
            System.out.println("Cannot move: target is not adjacent or not connected.");
            return false;
        }

        // Check occupancy
        boolean canOccupy = target.canOccupy();

        if (!canOccupy) {
            System.out.println("Returned: false");
            System.out.println("Cannot move: target element is occupied (only one player per pipe).");
            return false;
        }

        // Perform the move
        if (currentPosition != null) {
            currentPosition.removeOccupant(this);
        }

        target.addOccupant(this);
        currentPosition = target;

        System.out.println("[Player] currentPosition = " + target.getClass().getSimpleName());
        System.out.println("Returned: true");
        System.out.println("Move successful.");
        return true;
    }
    public boolean changePumpDirection(Pump pump, Pipe in, Pipe out) {
        System.out.println("[Player] changePumpDirection(pump, in, out)");
        if (pump == null || in == null || out == null) {
            return false;
        }

        PipeEnd inputEnd = in.end1 != null && in.end1.connectedTo == pump ? in.end1 : in.end2;
        PipeEnd outputEnd = out.end1 != null && out.end1.connectedTo == pump ? out.end1 : out.end2;

        return pump.setDirection(inputEnd, outputEnd);
    }
}
