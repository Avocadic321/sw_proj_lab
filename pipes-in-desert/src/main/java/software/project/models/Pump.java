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
    public List<PipeEnd> connections;

    /** Selected input pipe end. */
    public PipeEnd inputPipe;
    /** Selected output pipe end. */
    public PipeEnd outputPipe;

    /** Maximum water capacity of the pump tank. */
    public int tankCapacity;
    /** Current amount stored in the pump tank. */
    public int storedWater;
    /** Maximum number of connections allowed. */
    public int maxConnections;

    /** Whether the pump is broken. */
    public boolean isBroken;

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
        System.out.println("[Pump] setDirection()");

        boolean valid = input != null
                && output != null
                && input != output
                && input.connectedTo == this
                && output.connectedTo == this;

        System.out.println("[Pump] validateSingleInputOutput(inputPipe, outputPipe) -> " + valid);
        if (!valid) {
            return false;
        }

        inputPipe = input;
        outputPipe = output;
        System.out.println("[Pump] storeDirectionConfiguration()");
        return true;
    }

    /**
     * Transfers water through the pump.
     *
     * @param amount incoming amount
     * @return forwarded amount
     */
    public int transferWater(int amount) {
        System.out.println("[Pump] transferWater(" + amount + ")");
        return amount;
    }

    /** Breaks the pump. */
    @Override
    public void breakElement() {
        System.out.println("[Pump] breakElement()");
        this.isBroken = true;
    }

    /**
     * Indicates whether the pump is broken.
     *
     * @return true if broken
     */
    @Override
    public boolean isBroken() {
        System.out.println("[Pump] isBroken()");
        return this.isBroken;
    }

    /**
     * Connects a pipe end to this pump.
     *
     * @param end pipe end to connect
     */
    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Pump] connect()");
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
        System.out.println("[Pump] disconnect()");
        connections.remove(end);
    }

    /**
     * Returns the connected pipe ends.
     *
     * @return list of connections
     */
    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Pump] getConnections()");
        return connections;
    }

    /** Repairs the pump. */
    @Override
    public void repair() {
        System.out.println("[Pump] repair()");
        this.isBroken = false;
    }

    /**
     * Checks whether the pump tank is full.
     *
     * @return true if full
     */
    public boolean isTankFull() {
        System.out.println("[Pump] isTankFull()");
        return false;
    }

    /**
     * Returns the outgoing pipe based on outputPipe.
     *
     * @return outgoing pipe or null
     */
    public Pipe getOutgoingPipe() {
        System.out.println("[Pump] getOutgoingPipe()");
        if (outputPipe != null && outputPipe.pipe != null) {
            return outputPipe.pipe;
        }
        return null;
    }

    /**
     * Indicates whether the pump is connected to a cistern.
     *
     * @return true if output connects to a cistern
     */
    public boolean isConnectedToCistern() {
        System.out.println("[Pump] isConnectedToCistern()");
        return outputPipe != null && outputPipe.connectedTo instanceof Cistern;
    }

    /**
     * Returns the target cistern for the output connection.
     *
     * @return target cistern
     */
    public Cistern getTargetCistern() {
        System.out.println("[Pump] getTargetCistern()");
        return new Cistern();
    }
}
