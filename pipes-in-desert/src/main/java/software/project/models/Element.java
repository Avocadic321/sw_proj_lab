package software.project.models;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class representing any element that can exist within the pipe network.
 * <p>
 * This class provides common structural properties shared by all elements such as pipes,
 * pumps, cisterns, and springs, and enables them to participate in the network connectivity
 * structure. Elements also manage player occupancy, as players can stand on them during
 * the game.
 * </p>
 * <p>
 * Subclasses must implement their own occupancy rules by overriding {@link #canOccupy()}.
 * By default, elements allow an unlimited number of players to occupy them simultaneously.
 * </p>
 *
 * @see Player
 * @see Pipe
 * @see Pump
 * @see Cistern
 * @see Spring
 * @since 1.0
 */
public abstract class Element {
    private String id;

    private int x;
    private int y;

    protected List<Player> occupants;

    protected String getId() { return id; }
    protected int getX() { return x; }
    protected int getY() { return y; }

    /**
     * Constructs an element with default values.
     * <p>
     * The identifier is {@code null} and coordinates are zero.
     * Initializes an empty occupant list.
     * </p>
     */
    public Element() {
        this("",0,0);
    }

    /**
     * Constructs an element with the specified identifier.
     * <p>
     * Coordinates default to zero. Initializes an empty occupant list.
     * </p>
     *
     * @param id the unique identifier for this element
     */
    public Element(String id) {
        this(id,0,0);
    }

    /**
     * Constructs an element with the specified identifier and coordinates.
     *
     * @param id the unique identifier for this element
     * @param x  the x-coordinate position
     * @param y  the y-coordinate position
     */
    public Element(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.occupants = new ArrayList<>();
    }

    /**
     * Places a player onto this element.
     * <p>
     * Called when a player successfully moves onto this element.
     * The caller should verify that {@link #canOccupy()} returns {@code true}
     * before invoking this method.
     * </p>
     *
     * @param p the player to add to this element's occupants
     */
    public void addOccupant(Player p) {
        System.out.println("[Element] addOccupant(Player p)");
        occupants.add(p);
    }

    /**
     * Removes a player from this element.
     * <p>
     * Called when a player moves away from this element.
     * If the specified player is not an occupant, the method has no effect.
     * </p>
     *
     * @param p the player to remove from this element's occupants
     */
    public void removeOccupant(Player p) {
        System.out.println("[Element] removeOccupant(Player p)");
        occupants.remove(p);
    }

    /**
     * Returns the list of players currently occupying this element.
     *
     * @return a list of players on this element
     */
    public List<Player> getOccupants() {
        System.out.println("[Element] getOccupant()");
        return occupants;
    }

    /**
     * Determines whether a new player can move onto this element.
     * <p>
     * The default implementation returns {@code true}, allowing any number
     * of players to occupy the element. Subclasses should override this
     * method to enforce specific occupancy constraints:
     * <ul>
     *   <li>Pipes typically override to return {@code true} only when
     *       the pipe is not already occupied (one player per pipe)</li>
     *   <li>Pumps and active elements typically retain the default
     *       behavior (multiple players allowed)</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if a player can occupy this element,
     *         {@code false} otherwise
     */
    public boolean canOccupy() {
        System.out.println("[Element] canOccupy()");
        return true; // by default, any number of players can occupy
    }
}
