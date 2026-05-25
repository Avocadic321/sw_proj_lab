package software.project.map;

import software.project.core.GameConfig;
import software.project.map.interfaces.IBreakable;
import software.project.map.interfaces.ICarriable;
import software.project.map.interfaces.IConnectable;
import software.project.map.interfaces.IRepairable;
import software.project.utils.Debug;
import software.project.utils.ElementWaterState;
import software.project.utils.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Active element that routes water from an input to an output.
 */
public class Pump extends ActiveElement implements IBreakable, IRepairable, IConnectable, ICarriable {

    /**
     * Selected input pipe end.
     */
    private PipeEnd inputPipe;
    /**
     * Selected output pipe end.
     */
    private PipeEnd outputPipe;

    /**
     * Current amount stored in the pump tank.
     */
    private int storedWater;

    /**
     * Whether the pump is broken.
     */
    private boolean isBroken;

    /**
     * Set of directions where a pipe is physically connected.
     */
    private Set<Directions> connectedDirections = EnumSet.noneOf(Directions.class);

    // Water flow fields
    private int pendingFlowingWater;
    private int currentFlowingWater;

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

    @Override
    public void connect(PipeEnd end) {
        if (!connections.contains(end)) {
            connections.add(end);
            updateConnectedDirections();
        }
    }

    @Override
    public void disconnect(PipeEnd end) {
        connections.remove(end);
        // If the disconnected end was the current input or output, clear it
        if (inputPipe == end) inputPipe = null;
        if (outputPipe == end) outputPipe = null;
        updateConnectedDirections();
    }

    /**
     * Recalculates the set of directions that have a connected pipe.
     * Should be called after any connection change.
     */
    public void updateConnectedDirections() {
        connectedDirections.clear();
        for (PipeEnd end : connections) {
            if (end.pipe != null) {
                Directions dir = getDirectionFromPipe(end.pipe);
                if (dir != null) {
                    connectedDirections.add(dir);
                }
            }
        }
    }

    /**
     * Returns the direction of a pipe relative to this pump's position.
     */
    private Directions getDirectionFromPipe(Pipe pipe) {
        int dx = pipe.getX() - getX();
        int dy = pipe.getY() - getY();
        if (dx == 0 && dy == -1) return Directions.NORTH;
        if (dx == 1 && dy == 0)  return Directions.EAST;
        if (dx == 0 && dy == 1)  return Directions.SOUTH;
        if (dx == -1 && dy == 0) return Directions.WEST;
        return null;
    }

    /**
     * Returns the pipe end connected in the given direction, or null if none.
     */
    public PipeEnd getPipeEndForDirection(Directions dir) {
        for (PipeEnd end : connections) {
            if (end.pipe != null && getDirectionFromPipe(end.pipe) == dir) {
                return end;
            }
        }
        return null;
    }

    /**
     * Returns the set of directions that have a connected pipe.
     */
    public Set<Directions> getAvailableDirections() {
        return Collections.unmodifiableSet(connectedDirections);
    }

    // ------------------------------------------------------------------------
    // Direction setting
    // ------------------------------------------------------------------------

    /**
     * Sets the input and output pipe ends using Directions.
     *
     * @param inputDir  direction of the input pipe
     * @param outputDir direction of the output pipe
     * @return true if both directions have a connected pipe and they are distinct
     */
    public boolean setDirection(Directions inputDir, Directions outputDir) {
        if (inputDir == null || outputDir == null) return false;
        if (inputDir == outputDir) return false;

        PipeEnd input = getPipeEndForDirection(inputDir);
        PipeEnd output = getPipeEndForDirection(outputDir);
        if (input == null || output == null) return false;

        return setDirection(input, output);
    }

    /**
     * Sets the input and output pipe ends directly.
     */
    public boolean setDirection(PipeEnd input, PipeEnd output) {
        if (input == null || output == null) return false;
        if (input == output) return false;
        if (!connections.contains(input) || !connections.contains(output)) return false;

        this.inputPipe = input;
        this.outputPipe = output;
        return true;
    }

    // ------------------------------------------------------------------------
    // Getters for current direction
    // ------------------------------------------------------------------------

    public PipeEnd getInputPipe() {
        return inputPipe;
    }

    public PipeEnd getOutputPipe() {
        return outputPipe;
    }

    /**
     * Returns the direction of the current input pipe, or null if not set.
     */
    public Directions getInputDirection() {
        if (inputPipe == null || inputPipe.pipe == null) return null;
        return getDirectionFromPipe(inputPipe.pipe);
    }

    /**
     * Returns the direction of the current output pipe, or null if not set.
     */
    public Directions getOutputDirection() {
        if (outputPipe == null || outputPipe.pipe == null) return null;
        return getDirectionFromPipe(outputPipe.pipe);
    }

    // ------------------------------------------------------------------------
    // Water flow methods (existing)
    // ------------------------------------------------------------------------

    public int getStoredWater() {
        return storedWater;
    }

    public int getCurrentFlowingWater() {
        return currentFlowingWater;
    }

    public void setCurrentFlowingWater(int currentFlowingWater) {
        this.currentFlowingWater = currentFlowingWater;
    }

    @Override
    public int moveWater() {
        return currentFlowingWater;
    }

    @Override
    public void receiveWater(int water) {
        pendingFlowingWater += water;
    }

    @Override
    public int commit() {
        ElementWaterState state = Helper.waterToBePumpedOut(
            pendingFlowingWater, GameConfig.PUMP_MAX_FLOW_PER_TICK,
            storedWater, GameConfig.PUMP_TANK_CAPACITY,
            this::breakElement
        );
        storedWater = state.currentlyStoredWater();
        int waterAmount = state.pumpedWater();

        if (isBroken || outputPipe == null || outputPipe.isFree()) {
            int lost = waterAmount + storedWater;
            storedWater = 0;
            pendingFlowingWater = 0;
            return lost;
        }

        currentFlowingWater = waterAmount;
        pendingFlowingWater = 0;
        return 0;
    }

    @Override
    public int receiveAndTransferWater() {
        if (inputPipe == null || outputPipe == null) return 0;
        int incoming = inputPipe.consumeWater();

        int maxCapacity = GameConfig.PUMP_TANK_CAPACITY;
        int maxTransfer = GameConfig.PUMP_MAX_FLOW_PER_TICK;

        ElementWaterState waterAmount = Helper.waterToBePumpedOut(
            incoming,
            maxTransfer,
            storedWater,
            maxCapacity,
            this::breakElement);

        storedWater = waterAmount.currentlyStoredWater();
        int out = waterAmount.pumpedWater();

        if (isBroken || outputPipe.isFree()) {
            int lost = out + storedWater;
            storedWater = 0;
            System.out.printf("[EVENT] WATER_LEAK %s amount=%d", this.getId(), out);
            return lost;
        }
        Debug.log("[PUMP] %s AMOUNT FORWARDED %d", this.getId(), out);
        outputPipe.addPendingWater(out);
        return 0;
    }

    /**
     * Breaks the pump.
     */
    @Override
    public void breakElement() {
        this.isBroken = true;
    }

    @Override
    public boolean isBroken() {
        return this.isBroken;
    }

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
        return storedWater >= GameConfig.PUMP_TANK_CAPACITY;
    }

    public Pipe getOutgoingPipe() {
        if (outputPipe != null) return outputPipe.pipe;
        return null;
    }

    public boolean isConnectedToCistern() {
        return outputPipe != null && outputPipe.connectedTo instanceof Cistern;
    }

    @Override
    public String toString() {
        String inputId = (inputPipe == null || inputPipe.pipe == null) ? "NONE" : toPipeEndId(inputPipe);
        String outputId = (outputPipe == null || outputPipe.pipe == null) ? "NONE" : toPipeEndId(outputPipe);

        return String.format(
            "[STATE] PUMP %s broken=%s storedWater=%d input=%s output=%s connections=%s",
            getId(),
            isBroken,
            storedWater,
            inputId,
            outputId,
            connectedDirections
        );
    }

    private String toPipeEndId(PipeEnd end) {
        if (end == null || end.pipe == null) return "NONE";
        if (end.pipe.getEnd1() == end) return end.pipe.getId() + "_END1";
        if (end.pipe.getEnd2() == end) return end.pipe.getId() + "_END2";
        return end.pipe.getId();
    }
}