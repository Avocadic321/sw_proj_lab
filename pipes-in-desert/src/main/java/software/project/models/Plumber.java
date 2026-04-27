package software.project.models;

import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;

/**
 * A player whose role is to maintain and extend the pipe network.
 * <p>
 * Plumbers can repair damaged pipes and pumps, pick up new components from
 * cisterns, and modify the network by connecting, disconnecting, or inserting
 * pumps into pipes. Their score is based on water successfully delivered to cisterns.
 * </p>
 *
 * @see Player
 * @see Saboteur
 * @see IRepairable
 * @see ICarriable
 * @since 1.0
 */
public class Plumber extends Player {
    public ICarriable carriedItem;

    public Plumber(String id) {
        super(id);
    }

    /**
     * Repairs a broken or damaged element, restoring it to full functionality.
     *
     * @param target the element to repair (pipe or pump)
     */
    public void repair(IRepairable target) {
        System.out.println("[Plumber] repair()");
        target.repair();
    }

    /**
     * Places the currently carried item (pipe or pump) into the network
     * at the plumber's current position.
     */
    public void extendPipeSystem() {
        System.out.println("[Plumber] extendPipeSystem()");
    }

    /**
     * Picks up a newly manufactured pump from a cistern.
     *
     * @param cistern the cistern to pick the pump from
     */
    public void pickUpPump(Cistern cistern) {
        System.out.println("[Plumber] pickUpPump() - at Cistern");
    }

    /**
     * Picks up an existing pump from the network.
     *
     * @param pump the pump to pick up
     */
    public void pickUpPump(Pump pump) {
        System.out.println("[Plumber] pickUpPump()");
    }

    /**
     * Picks up a newly manufactured pipe from a cistern.
     *
     * @param cistern the cistern to pick the pipe from
     */
    public void pickUpPipe(Cistern cistern) {
        System.out.println("[Plumber] pickUpPipe()");
    }

    /**
     * Detaches the specified pipe end from its connected active element.
     *
     * @param end the pipe end to disconnect
     */
    public void disconnect(PipeEnd end) {
        System.out.println("[Plumber] disconnect(end)");
        end.disconnect();
        System.out.println("[Plumber] Pipe end disconnected");
    }

    /**
     * Attaches a free pipe end to the specified active element.
     *
     * @param end the pipe end to connect
     * @param tgt the target active element (pump, cistern, or spring)
     */
    public void connect(PipeEnd end, ActiveElement tgt) {
        System.out.println("[Plumber] connect()");
        end.connectsTo(tgt);
        System.out.println("[Plumber] Pipe end connected");
    }

    /**
     * Returns the item currently being carried by the plumber.
     *
     * @return the carried item, or null if nothing is being carried
     */
    public ICarriable getCarriedItem() {
        System.out.println("[Plumber] getCarriedItem()");
        return carriedItem;
    }

    /**
     * Sets the item currently being carried by the plumber.
     *
     * @param item the item to carry
     */
    public void setCarriedItem(ICarriable item) {
        System.out.println("[Plumber] setCarriedItem()");
        carriedItem = item;
    }

    /**
     * Clears the currently carried item, setting it to null.
     */
    public void clearCarriedItem() {
        System.out.println("[Plumber] clearCarriedItem()");
        carriedItem = null;
    }

    /**
     * Inserts a carried pump into the middle of an existing pipe.
     * <p>
     * The pipe is split into two separate pipes, each connected to the new pump.
     * This implements the "splicing" mechanic.
     * </p>
     *
     * @param pump the pump to insert (must be carried)
     * @param pipe the pipe to insert the pump into
     */
    public void insertPumpIntoPipe(Pump pump, Pipe pipe) {
        System.out.println("[Plumber] insertPumpIntoPipe()");
    }

}
