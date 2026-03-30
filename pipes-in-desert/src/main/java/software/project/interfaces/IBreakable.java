package software.project.interfaces;

/**
 * Represents game elements that can become damaged or broken during gameplay.
 * <p>
 * Classes implementing this interface must provide functionality to change
 * and check their broken state. This interface is implemented by elements
 * such as pipes and pumps that can be damaged by saboteurs or random events.
 * </p>
 */
public interface IBreakable {
    /**
     * Changes the state of the element to broken.
     * <p>
     * Called by the game during random events (e.g., pump failure) or by
     * saboteurs when puncturing a pipe. After this method is invoked,
     * the element should no longer function normally until repaired.
     * </p>
     */
    void breakElement();

    /**
     * Returns whether the element is currently in a broken state.
     *
     * @return true if the element is broken, false otherwise
     */
    boolean isBroken();
}
