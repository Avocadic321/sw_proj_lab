package software.project.models;

import software.project.utils.IdGenerator;

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

    public final String getId() { return id; }
    public final int getX() { return x; }
    public final int getY() { return y; }


    protected Element() {
        this(null,-1,-1);
    }

    /**
     * Constructs an element with the specified identifier.
     * <p>
     * Coordinates default to zero. Initializes an empty occupant list.
     * </p>
     *
     * @param id the unique identifier for this element
     */
    protected Element(String id) {
        this(id,-1,-1);
    }

    /**
     * Constructs an element with the specified identifier and coordinates.
     *
     * @param id the unique identifier for this element
     * @param x  the x-coordinate position
     * @param y  the y-coordinate position
     */
    protected Element(String id, int x, int y) {
        if (id == null || id.isEmpty()) {
            this.id = IdGenerator.generateId(this.getClass());
        } else {
            if (!IdGenerator.isIdAvailable(id)) {
                throw new IllegalArgumentException("[ERROR] ELEMENT DUPLICATE_ID");
            }
            IdGenerator.markIdUsed(id);
            this.id = id;
        }
        this.x = x;
        this.y = y;
        this.occupants = new ArrayList<>();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
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
        occupants.remove(p);
    }

    /**
     * Returns the list of players currently occupying this element.
     *
     * @return a list of players on this element
     */
    public List<Player> getOccupants() {
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
        return true; // by default, any number of players can occupy
    }

    /**
     * Transfer water from input(s) of Element to its output(s)
     * @return amount of leaked water
     */
    public abstract int receiveAndTransferWater();
}
