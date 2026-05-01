package software.project.models;

import java.util.ArrayList;
import java.util.List;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IConnectable;
import software.project.interfaces.IRepairable;
import software.project.utils.ElementWaterState;
import software.project.utils.Helper;

/**
 * Active element that routes water from an input to an output.
 */
public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {
    public static final int MAX_CONNECTIONS = 8;

    private static final int MAX_TANK_CAPACITY = 1000;

    /** Connected pipe ends. */
    private List<PipeEnd> connections;

    /** Selected input pipe end. */
    private PipeEnd inputPipe;
    /** Selected output pipe end. */
    private PipeEnd outputPipe;

    /** Current amount stored in the pump tank. */
    private int storedWater;

    /** Whether the pump is broken. */
    private boolean isBroken;

    public Pump() {
        this(null, -1, -1);
    }

    public Pump(int x, int y) {
        this(null, x, y);
    }

    public Pump(String id) {
        this(id, -1, -1);
    }

    public Pump(String id, int x, int y) {
        super(id, x, y);

        this.storedWater = 0;
        this.isBroken = false;
        this.connections = new ArrayList<>();
        this.inputPipe = null;
        this.outputPipe = null;
    }

    /**
     * Sets the input and output pipe ends for this pump.
     *
     * @param input  selected input pipe end
     * @param output selected output pipe end
     * @return true if the configuration is valid
     */
    public boolean setDirection(PipeEnd input, PipeEnd output) {
        if (input == null || output == null) {
            return false;
        }

        // Must be distinct ends
        if (input == output) {
            return false;
        }

        // Both ends must be among the pump’s connections
        if (!connections.contains(input) || !connections.contains(output)) {
            return false;
        }

        // Set the new direction
        this.inputPipe = input;
        this.outputPipe = output;
        return true;
    }

    /**
     * Transfers water through the pump.
     *
    // * @param amount incoming amount
     * @return forwarded amount
     */
    public int transferWater() {

        ElementWaterState waterAmount = Helper.waterToBePumpedOut(inputPipe.getReceivedWater(),1000,storedWater,MAX_TANK_CAPACITY, this::breakElement);
        inputPipe.clearInput();
        if(isBroken || outputPipe.isFree()) {
            storedWater = 0; // lose all water we hold
            System.out.printf("[EVENT] WATER_LEAK %s amount=%d", this.getId(), waterAmount);
            return waterAmount.pumpedWater();
        }
        outputPipe.setInput(waterAmount.pumpedWater());
        storedWater = waterAmount.currentlyStoredWater();
        return 0;
    }

    /** Breaks the pump. */
    @Override
    public void breakElement() {
        this.isBroken = true;
    }

    /**
     * Indicates whether the pump is broken.
     *
     * @return true if broken
     */
    @Override
    public boolean isBroken() {
        return this.isBroken;
    }

    /**
     * Connects a pipe end to this pump.
     *
     * @param end pipe end to connect
     */
    @Override
    public void connect(PipeEnd end) {
        if (!connections.contains(end)) {
            connections.add(end);
        }
    }

    /**
     * Disconnects a pipe end from this pump.
     *
     * @param end pipe end to disconnect
     */
    @Override
    public void disconnect(PipeEnd end) {
        connections.remove(end);
    }

    /**
     * Returns the connected pipe ends.
     *
     * @return list of connections
     */
    @Override
    public List<PipeEnd> getConnections() {
        return connections;
    }

    /** Repairs the pump. */
    @Override
    public void repair() {
        this.isBroken = false;
    }

    /**
     * Checks whether the pump tank is full.
     *
     * @return true if full
     */
    public boolean isTankFull() {
        return storedWater == MAX_TANK_CAPACITY;
    }

    /**
     * Returns the outgoing pipe based on outputPipe.
     *
     * @return outgoing pipe or null
     */
    public Pipe getOutgoingPipe() {
        if (outputPipe != null) {
            return outputPipe.pipe;
        }
        return null;
    }

    /* Do we need this? */
    public boolean isConnectedToCistern() {
        System.out.println("[Pump] isConnectedToCistern()");
        return outputPipe != null && outputPipe.connectedTo instanceof Cistern;
    }

    @Override
    public void receiveAndTransferWater() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
