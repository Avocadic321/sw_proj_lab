package software.project.models;

import java.util.ArrayList;
import java.util.List;

import software.project.interfaces.IBreakable;
import software.project.interfaces.ICarriable;
import software.project.interfaces.IConnectable;
import software.project.interfaces.IRepairable;

/**
 * Active element that routes water from an input to an output.
 */
public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {
    /** Connected pipe ends. */
    private List<PipeEnd> connections;

    /** Selected input pipe end. */
    private PipeEnd inputPipe;
    /** Selected output pipe end. */
    private PipeEnd outputPipe;

    /** Maximum water capacity of the pump tank. */
    private int tankCapacity;
    /** Current amount stored in the pump tank. */
    private int storedWater;
    /** Maximum number of connections allowed. */
    private int maxConnections;

    /** Whether the pump is broken. */
    private boolean isBroken;

    /** Creates a new pump with default values. */
    public Pump() {
        super();
        connections = new ArrayList<>();
        inputPipe = new PipeEnd();
        outputPipe = new PipeEnd();
        isBroken = false;
    }

    /**
     * Creates a new pump with the given identifier.
     *
     * @param id pump id
     */
    public Pump(String id) {
        super(id);
        this.connections = new ArrayList<>();
        inputPipe = new PipeEnd();
        outputPipe = new PipeEnd();
        isBroken = false;
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
     * @param amount incoming amount
     * @return forwarded amount
     */
    public int transferWater(int amount) {
        // TODO: Implement logic
        outputPipe.recieveWater(true);
        return amount;
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
        return storedWater == tankCapacity;
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
}
