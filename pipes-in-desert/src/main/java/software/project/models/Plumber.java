package software.project.models;

import software.project.audio.AudioPlayer;
import software.project.core.GameConfig;
import software.project.map.interfaces.IBreakable;
import software.project.map.interfaces.ICarriable;
import software.project.map.interfaces.IRepairable;
import software.project.map.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A player whose role is to maintain and extend the pipe network.
 * <p>
 * Plumbers can repair damaged pipes and pumps, pick up new components from cisterns, and modify the
 * network by connecting, disconnecting, or inserting pumps into pipes. Their score is based on
 * water successfully delivered to cisterns.
 * </p>
 *
 * @see Player
 * @see Saboteur
 * @see IRepairable
 * @see ICarriable
 * @since 1.0
 */
public class Plumber extends Player {
    private final Inventory inventory;

    public Plumber(String id, Element startPosition) {
        super(id, startPosition);
        this.inventory = new Inventory(GameConfig.DEFAULT_INVENTORY_SIZE);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Plumber(Element startPosition) {
        this("", startPosition);

    }

    @Override
    public boolean doMainAction() {
        if (getCurrentPosition() instanceof IBreakable breakable && getCurrentPosition() instanceof IRepairable repairable && breakable.isBroken()) {
            this.repair(repairable);
            return true;
        }
        return false;

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
     * Places the currently carried item (from inventory) into the network at the plumber's current
     * position.
     */
    public void extendPipeSystem(int item) {

        ICarriable carriedItem = inventory.get(item);
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
            inventory.remove(item);
            return;
        }

        if (carriedItem instanceof Pump pump && currentPosition instanceof Pipe pipe) {
            insertPumpIntoPipe(pump, pipe, new Pipe(), new Pipe(), new PipeEnd(), new PipeEnd());
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

        if (inventory.isFull()) {
            throw new IllegalStateException("Inventory is full");
        }

        if (!inventory.add(cistern.pickUpPump())) {
            throw new IllegalStateException("Inventory is full");
        }
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

        List<PipeEnd> endsToDisconnect = new ArrayList<>(pump.getConnections());
        for (PipeEnd end : endsToDisconnect) {
            if (end != null) {
                end.disconnect();
            }
        }

        if (inventory.isFull()) {
            throw new IllegalStateException("Inventory is full");
        }

        if (!inventory.add(pump)) {
            throw new IllegalStateException("Inventory is full");
        }

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
        if (inventory.isFull()) {
            throw new IllegalStateException("Inventory is full");
        }
        if (!inventory.add(cistern.pickUpPipe())) {
            throw new IllegalStateException("Inventory is full");
        }

    }

    /**
     * Places a pipe from inventory onto the map at the specified position.
     * Assumes all validation (adjacency, empty space, direction) has already been done by the UI.
     *
     * @param pipe the pipe to place (must be in inventory)
     * @param targetPos the grid position where the pipe should be placed
     * @param slotIndex the inventory slot index where the pipe is stored
     * @param map the game map to add the pipe to
     * @return true if placement was successful
     */
    public boolean placePipe(Pipe pipe, Point targetPos, int slotIndex, GameMap map) {
        // Check if pipe is in inventory at the specified slot
        if (inventory.get(slotIndex) != pipe) {
            System.out.println("[ERROR] placePipe: Pipe not found in inventory slot " + slotIndex);
            return false;
        }

        // Set pipe position
        pipe.setPosition(targetPos.x, targetPos.y);

        // Connect to current position (the element the player is standing on)
        if (currentPosition instanceof ActiveElement activeElem) {
            PipeEnd freeEnd = pipe.getFreeEnd();
            if (freeEnd != null) {
                freeEnd.connectsTo(activeElem);
            } else {
                pipe.getEnd1().connectsTo(activeElem);
            }
        }

        // Add to map
        map.addElement(pipe);

        // Remove from inventory by ID (safer)
        inventory.removeById(pipe.getId());

        System.out.println("[OK] placePipe: Pipe " + pipe.getId() + " placed at (" + targetPos.x + "," + targetPos.y + ")");
        return true;
    }

    public boolean placePump(Pipe pipe, Pump pump, Point p) {
        PipeEnd freeEnd = pipe.getFreeEnd();
        if (freeEnd == null) {
            return false;
        }
        pump.setPosition(p.x, p.y);
        freeEnd.connectsTo(pump);
        return true;
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
        PipeEnd end1 = pipe.getEnd1();
        PipeEnd end2 = pipe.getEnd2();

        if (end1 != null) {
            end1.disconnect();
        }

        if (end2 != null) {
            end2.disconnect();
        }

        if (inventory.isFull()) {
            throw new IllegalStateException("Inventory is full");
        }
        if (!inventory.add(pipe)) {
            throw new IllegalStateException("Inventory is full");
        }
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
    public ICarriable getCarriedItem(int item) {

        return inventory.get(item);
    }

    public record SplitItemPayload(Pump pump, Pipe pipeLeft, Pipe pipeRight) {
    }

    public SplitItemPayload canSplit(Pipe p) {
        ICarriable item = Arrays.stream(this.getInventory().getInventory())
                                .filter(Pump.class::isInstance)
                                .findFirst()
                                .orElse(null);
        if (item instanceof Pump pump && p.getEnd1().connectedTo != null && p.getEnd1().connectedTo instanceof Pipe pipeLeft && p.getEnd2().connectedTo != null && p.getEnd2().connectedTo instanceof Pipe pipeRight) {
            return new SplitItemPayload(pump, pipeLeft, pipeRight);
        }
        return null;
    }

    /**
     * Inserts a carried pump into the middle of an existing pipe.
     * <p>
     * The pipe is split into two separate pipes, each connected to the new pump. This implements
     * the "splicing" mechanic.
     * </p>
     *
     * @param pump the pump to insert (must be carried)
     * @param pipe the pipe to insert the pump into
     */
    public void insertPumpIntoPipe(Pump pump, Pipe pipe, Pipe p1, Pipe p2, PipeEnd e1, PipeEnd e2) {

        if (inventory.get(pump) == null) {
            throw new IllegalStateException("Pump is not being carried");
        }

        if (currentPosition != pipe) {
            throw new IllegalArgumentException("Plumber is not at the location of the pipe");
        }


        pump.setPosition(pipe.getX(), pipe.getY());
        pump.connect(e1);
        e1.connectsTo(pump);
        pump.connect(e2);
        e2.connectsTo(pump);
        inventory.removeItem(pump);
        currentPosition.removeOccupant(this);
        pump.addOccupant(this);
        currentPosition = pump;
        pump.setDirection(e1, e2);
        System.out.println("[OK] INSERT_PUMP " + id + " " + pipe.getId() + " " + pump.getId());
        System.out.println("[EVENT] PIPE_SPLIT " + pipe.getId() + " into " + p1.getId() + "," + p2.getId()
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

        for (int i = 0; i < inventory.getInventory().length; i++) {
            ICarriable carriedItem = inventory.getInventory()[i];
            if (carriedItem instanceof Element element) {
                carried = element.getId();
            } else if (carriedItem != null) {
                carried = carriedItem.getClass().getSimpleName().toUpperCase();
            }
        }

        return String.format("[STATE] PLUMBER %s position=%s carriedItem=%s", id, positionId, carried);
    }

}
