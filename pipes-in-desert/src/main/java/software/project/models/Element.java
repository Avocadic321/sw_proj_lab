package software.project.models;

import java.util.List;

public abstract class Element {
    public String id;

    public int x;
    public int y;

    public List<Player> occupants;

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
        return true;
    }



}
