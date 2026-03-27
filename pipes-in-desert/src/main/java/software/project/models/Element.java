package software.project.models;

import java.util.List;

public abstract class Element {
    public String id;
    public int x;
    public int y;
    public List<Player> occupants;
    public void addOccupant(Player p) {
        System.out.println("calling addOccupant(Player p)");
    }
    public void removeOccupant(Player p) {
        System.out.println("calling removeOccupant(Player p)");
    }

    public List<Player> getOccupants() {
        System.out.println("calling getOccupant()");
        return occupants;
    }

    public boolean canOccupy() {
        System.out.println("calling canOccupy()");
        return true;
    }



}
