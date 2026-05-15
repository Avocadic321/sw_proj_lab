package software.project.models;

import software.project.interfaces.ICarriable;
import software.project.interfaces.IRepairable;
import software.project.map.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A player whose role is to maintain and extend the pipe network.
 * <p>
 * Plumbers can repair damaged pipes and pumps, pick up new components from cisterns, and modify the network by
 * connecting, disconnecting, or inserting pumps into pipes. Their score is based on water successfully delivered to
 * cisterns.
 * </p>
 *
 * @see Player
 * @see Saboteur
 * @see IRepairable
 * @see ICarriable
 * @since 1.0
 */
public class Plumber extends Player {
    private ICarriable carriedItem;

    public Plumber(String id, Element startPosition) {
        super(id, startPosition);
        this.carriedItem = null;
    }

    public Plumber(Element startPosition) {
        super(startPosition);
    }

    /**
     * Repairs a broken or damaged element, restoring it to full functionality.
     *
     * @param target the element to repair (pipe or pump)
     */
    public void repair(IRepairable target) {
        target.repair();
    }

    /**
     * Places the currently carried item (pipe or pump) into the network at the plumber's current position.
     */
    public void extendPipeSystem() {
        if (carriedItem == null || currentPosition == null) {
            throw new IllegalStateException("No item being carried or current position is null");
        }

        if (carriedItem instanceof Pipe pipe && currentPosition instanceof ActiveElement active) {
            PipeEnd freeEnd = getFreeEnd(pipe);
            if (freeEnd == null) {
                throw new IllegalStateException("No free end available to connect the pipe");
            }

            if (!hasFreeSlot(active)) {
                throw new IllegalStateException("No free slot available on the active element");
            }

            freeEnd.connectsTo(active);
            carriedItem = null;
            return;
        }

        if (carriedItem instanceof Pump pump && currentPosition instanceof Pipe pipe) {
            insertPumpIntoPipe(pump, pipe);
        }
    }

    /**
     * Picks up a newly manufactured pump from a cistern.
     *
     * @param cistern the cistern to pick the pump from
     */
    public void pickUpPump(Cistern cistern) {
        if (cistern == null || currentPosition != cistern) {
            throw new IllegalArgumentException("Cistern is null or not at the current position");
        }

        if (carriedItem != null) {
            throw new IllegalStateException("Already carrying an item");
        }

        carriedItem = cistern.pickUpPump();
    }

    /**
     * Picks up an existing pump from the network.
     *
     * @param pump the pump to pick up
     */
    public void pickUpPump(Pump pump) {
        if (pump == null || currentPosition != pump) {
            throw new IllegalArgumentException("Pump is null or not at the current position");
        }

        if (carriedItem != null) {
            throw new IllegalStateException("Already carrying an item");
        }

        List<PipeEnd> endsToDisconnect = new ArrayList<>(pump.getConnections());
        for (PipeEnd end : endsToDisconnect) {
            if (end != null) {
                end.disconnect();
            }
        }

        carriedItem = pump;
    }

    /**
     * Picks up a newly manufactured pipe from a cistern.
     *
     * @param cistern the cistern to pick the pipe from
     */
    public void pickUpPipe(Cistern cistern) {
        if (cistern == null || currentPosition != cistern) {
            throw new IllegalArgumentException("Cistern is null or not at the current position");
        }

        if (carriedItem != null) {
            throw new IllegalStateException("Already carrying an item");
        }

        carriedItem = cistern.pickUpPipe();
    }

    /**
     * Picks up an existing pipe from the network.
     *
     * @param pipe the pipe to pick up
     */
    public void pickUpPipe(Pipe pipe) {
        if (pipe == null || currentPosition != pipe) {
            throw new IllegalArgumentException("Pipe is null or not at the current position");
        }

        if (carriedItem != null) {
            throw new IllegalStateException("Already carrying an item");
        }

        PipeEnd end1 = pipe.getEnd1();
        PipeEnd end2 = pipe.getEnd2();

        if (end1 != null) {
            end1.disconnect();
        }

        if (end2 != null) {
            end2.disconnect();
        }

        carriedItem = pipe;
    }

    /**
     * Detaches the specified pipe end from its connected active element.
     *
     * @param end the pipe end to disconnect
     */
    public void disconnect(PipeEnd end) {
        if (currentPosition != end.pipe && currentPosition != end.connectedTo) {
            throw new IllegalArgumentException("Plumber is not at the location of the pipe end or its connection");
        }

        if (end.isFree()) {
            throw new IllegalStateException("Pipe end is already free");
        }

        end.disconnect();
        System.out.println("[OK] DISCONNECT " + id + " " + getPipeEndId(end));
    }

    /**
     * Attaches a free pipe end to the specified active element.
     *
     * @param end the pipe end to connect
     * @param tgt the target active element (pump, cistern, or spring)
     */
    public void connect(PipeEnd end, ActiveElement tgt) {
        if (currentPosition != end.pipe && currentPosition != tgt) {
            throw new IllegalArgumentException("Plumber is not at the location of the pipe end or its connection");
        }

        if (!end.isFree()) {
            throw new IllegalStateException("Pipe end is already connected");
        }

        if (!hasFreeSlot(tgt)) {
            throw new IllegalStateException("No free slot available on the target element");
        }

        end.connectsTo(tgt);
        System.out.println("[OK] CONNECT " + id + " " + getPipeEndId(end) + " " + tgt.getId());
    }

    /**
     * Returns the item currently being carried by the plumber.
     *
     * @return the carried item, or null if nothing is being carried
     */
    public ICarriable getCarriedItem() {
        return carriedItem;
    }

    /**
     * Sets the item currently being carried by the plumber.
     *
     * @param item the item to carry
     */
    public void setCarriedItem(ICarriable item) {
        carriedItem = item;
    }

    /**
     * Clears the currently carried item, setting it to null.
     */
    public void clearCarriedItem() {
        carriedItem = null;
    }

    /**
     * Inserts a carried pump into the middle of an existing pipe.
     * <p>
     * The pipe is split into two separate pipes, each connected to the new pump. This implements the "splicing"
     * mechanic.
     * </p>
     *
     * @param pump the pump to insert (must be carried)
     * @param pipe the pipe to insert the pump into
     */
    public void insertPumpIntoPipe(Pump pump, Pipe pipe) {
        if (carriedItem != pump) {
            throw new IllegalStateException("Pump is not being carried");
        }

        if (currentPosition != pipe) {
            throw new IllegalArgumentException("Plumber is not at the location of the pipe");
        }

        Pipe[] newPipes = pipe.splitForPump(pump);
        Pipe pipe1 = newPipes[0];
        Pipe pipe2 = newPipes[1];

        pump.connect(pipe1.getEnd2());
        pump.connect(pipe2.getEnd1());

        carriedItem = null;

        currentPosition.removeOccupant(this);
        pump.addOccupant(this);
        currentPosition = pump;

        System.out.println("[OK] INSERT_PUMP " + id + " " + pipe.getId() + " " + pump.getId());
        System.out.println("[EVENT] PIPE_SPLIT " + pipe.getId() + " into " + pipe1.getId() + "," + pipe2.getId()
            + " via " + pump.getId());
    }

    private PipeEnd getFreeEnd(Pipe pipe) {
        PipeEnd end1 = pipe.getEnd1();
        if (end1 != null && end1.isFree()) {
            return end1;
        }

        PipeEnd end2 = pipe.getEnd2();
        if (end2 != null && end2.isFree()) {
            return end2;
        }

        return null;
    }

    private boolean hasFreeSlot(ActiveElement tgt) {
        if (tgt == null) {
            return false;
        }

        int maxConnections = (tgt instanceof Pump) ? 4 : 1;
        return tgt.getConnections().size() < maxConnections;
    }

    private String getPipeEndId(PipeEnd end) {
        if (end == null || end.pipe == null) {
            return "PIPE_END";
        }

        String pipeId = end.pipe.getId();
        if (pipeId == null) {
            pipeId = "PIPE";
        }

        if (end.pipe.getEnd1() == end) {
            return pipeId + "_END1";
        }

        if (end.pipe.getEnd2() == end) {
            return pipeId + "_END2";
        }

        return pipeId;
    }

    @Override
    public String toString() {
        String positionId = currentPosition == null ? "NONE" : currentPosition.getId();
        String carried = "NONE";

        if (carriedItem instanceof Element element) {
            carried = element.getId();
        } else if (carriedItem != null) {
            carried = carriedItem.getClass().getSimpleName().toUpperCase();
        }

        return String.format("[STATE] PLUMBER %s position=%s carriedItem=%s", id, positionId, carried);
    }

}
