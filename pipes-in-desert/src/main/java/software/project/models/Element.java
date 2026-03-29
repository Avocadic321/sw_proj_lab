package software.project.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    public String id;

    public int x;
    public int y;

    public List<Player> occupants;

    protected Element() {
        occupants = new ArrayList<>();
    }

    public void addOccupant(Player p) {
        System.out.println("[Element] addOccupant(Player p)");
    }
    public void removeOccupant(Player p) {
        System.out.println("[Element] removeOccupant(Player p)");
    }

    public List<Player> getOccupants() {
        System.out.println("[Element] getOccupant()");
        return occupants;
    }

    public boolean canOccupy() {
        System.out.println("[Element] canOccupy()");
        return true;
    }

    public boolean validateConnection(Pipe selectedPipe, PipeEnd freeEnd) {
        System.out.println("[Element] validateConnection(selectedPipe, freeEnd)");
        if (selectedPipe == null || freeEnd == null || freeEnd.pipe != selectedPipe) {
            return false;
        }

        if (!(this instanceof ActiveElement)) {
            return false;
        }

        if (this instanceof Pump pump) {
            int currentConnections = pump.getConnections().size();
            if (pump.maxConnections > 0 && currentConnections >= pump.maxConnections) {
                return false;
            }
        }

        return true;
    }



}
