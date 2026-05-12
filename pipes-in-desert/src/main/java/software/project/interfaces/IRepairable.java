package software.project.interfaces;

/**
 * Defines the behavior of game elements that can be repaired by a plumber.
 * <p>
 * Any class implementing this interface must provide functionality to restore the element to a working state. This
 * interface is implemented by elements such as pipes and pumps that can become broken or damaged during gameplay.
 * </p>
 * <p>
 * When a plumber performs a repair action on an element implementing this interface, the repair method is called to
 * restore the element's normal functionality, such as allowing water flow through a previously broken pipe or
 * reactivating a failed pump.
 * </p>
 *
 * @see software.project.models.Pipe
 * @see software.project.models.Pump
 * @see software.project.models.Plumber
 * @see IBreakable
 */
public interface IRepairable {
    /**
     * Restores the element to its functional state after it has been damaged or broken.
     * <p>
     * Called when a plumber performs a repair action on this element. After repair, the element should resume normal
     * operation, such as allowing water flow or functioning as intended.
     * </p>
     */
    void repair();
}