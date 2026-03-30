package software.project.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    public String id;

    public int x;
    public int y;

    public List<Player> occupants;

    public Element() {
        this.occupants = new ArrayList<>();
    }

    public Element(String id) {
        this.id = id;
        this.occupants = new ArrayList<>();
    }

    public Element(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.occupants = new ArrayList<>();
    }

    public void addOccupant(Player p) {
        System.out.println("[Element] addOccupant(Player p)");
        occupants.add(p);
    }
    public void removeOccupant(Player p) {
        System.out.println("[Element] removeOccupant(Player p)");
        occupants.remove(p);
    }

    public List<Player> getOccupants() {
        System.out.println("[Element] getOccupant()");
        return occupants;
    }

    public boolean canOccupy() {
        System.out.println("[Element] canOccupy()");
        return true; // by default, any number of players can occupy
    }



}
