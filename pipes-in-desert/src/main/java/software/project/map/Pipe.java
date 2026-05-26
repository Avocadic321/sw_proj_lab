package software.project.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import software.project.core.GameConfig;
import software.project.map.interfaces.IBreakable;
import software.project.map.interfaces.ICarriable;
import software.project.map.interfaces.IRepairable;
import software.project.utils.ElementWaterState;
import software.project.utils.Helper;

/**
 * Pipe segment that connects two pipe ends and can carry water.
 */
public class Pipe extends ActiveElement implements IBreakable, IRepairable, ICarriable {

    private final PipeEnd end1;
    private final PipeEnd end2;

    public static class DirectionEnd {
        private PipeEnd end;
        private boolean isInput;

        public DirectionEnd(PipeEnd end, boolean isInput) {
            this.end = end;
            this.isInput = isInput;
        }

        public PipeEnd getEnd() { return end; }
        public void setEnd(PipeEnd end) { this.end = end; }
        public boolean isInput() { return isInput; }
        public void setInput(boolean input) { isInput = input; }
    }

    private DirectionEnd directionEnd1;
    private DirectionEnd directionEnd2;

    private final int capacity;
    private int currentWater;
    private boolean isBroken;
    private boolean isConflict;
    private int pendingFlowingWater;
    private int currentFlowingWater;

    // Orientation (only VERTICAL / HORIZONTAL for now)
    private PipeOrientation orientation;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public Pipe() {
        this(null, -1, -1, GameConfig.PIPE_DEFAULT_CAPACITY);
    }

    public Pipe(String id) {
        this(id, -1, -1, GameConfig.PIPE_DEFAULT_CAPACITY);
    }

    public Pipe(int x, int y) {
        this(null, x, y, GameConfig.PIPE_DEFAULT_CAPACITY);
    }

    public Pipe(String id, int x, int y) {
        this(id, x, y, GameConfig.PIPE_DEFAULT_CAPACITY);
    }

    public Pipe(String id, int x, int y, int capacity) {
        super(id, x, y);
        if (capacity < 1 || capacity > GameConfig.PIPE_MAX_CAPACITY) {
            throw new IllegalArgumentException("[ERROR] PIPE INVALID_CAPACITY");
        }
        this.capacity = capacity;
        this.isBroken = false;
        this.end1 = new PipeEnd();
        this.end2 = new PipeEnd();
        this.end1.pipe = this;
        this.end2.pipe = this;
        this.directionEnd1 = new DirectionEnd(this.end1, false);
        this.directionEnd2 = new DirectionEnd(this.end2, false);
        this.orientation = PipeOrientation.VERTICAL; // default
    }

    // ------------------------------------------------------------------------
    // Getters / Setters for ends and orientation
    // ------------------------------------------------------------------------
    public PipeEnd getEnd1() { return end1; }
    public PipeEnd getEnd2() { return end2; }

    public DirectionEnd getDirectionEnd1() { return directionEnd1; }
    public void setDirectionEnd1(DirectionEnd directionEnd1) { this.directionEnd1 = directionEnd1; }

    public DirectionEnd getDirectionEnd2() { return directionEnd2; }
    public void setDirectionEnd2(DirectionEnd directionEnd2) { this.directionEnd2 = directionEnd2; }

    public PipeOrientation getOrientation() { return orientation; }
    public void setOrientation(PipeOrientation orientation) { this.orientation = orientation; }

    public Directions getOpenDirection1() { return orientation.getDirection1(); }
    public Directions getOpenDirection2() { return orientation.getDirection2(); }

    // ------------------------------------------------------------------------
    // Orientation utilities
    // ------------------------------------------------------------------------
    public void rotateClockwise() {
        this.orientation = orientation.rotateClockwise();
    }

    public void rotateCounterClockwise() {
        this.orientation = orientation.rotateCounterClockwise();
    }

    // ------------------------------------------------------------------------
    // Connection helpers
    // ------------------------------------------------------------------------
    @Override
    public void connect(PipeEnd end) {
        if (!connections.contains(end)) {
            connections.add(end);
        }
    }

    @Override
    public void disconnect(PipeEnd end) {
        connections.remove(end);
        if (end != null && end.connectedTo == this) {
            end.connectedTo = null;
        }
    }

    public void connectBothEnds(ActiveElement end1Target, ActiveElement end2Target) {
        if (end1Target == null && end2Target == null) return;
        if (end1Target != null && end1Target == end2Target) {
            System.out.println("[ERROR] PIPE SAME_ENDS_TARGET");
            return;
        }
        if (end1Target != null) {
            end1.connectsTo(end1Target);
        }
        if (end2Target != null) {
            end2.connectsTo(end2Target);
        }
    }

    public PipeEnd getFreeEnd() {
        if (end1.isFree()) return end1;
        if (end2.isFree()) return end2;
        return null;
    }

    public boolean hasFreeEnd() {
        return end1.isFree() || end2.isFree();
    }

    // ------------------------------------------------------------------------
    // Water flow methods
    // ------------------------------------------------------------------------
    @Override
    public int moveWater() {
        if (isBroken) return 0;
        return currentFlowingWater;
    }

    @Override
    public void receiveWater(int water) {
        pendingFlowingWater += water;
    }

    @Override
    public int commit() {
        int maxTransfer = GameConfig.PIPE_MAX_FLOW_PER_TICK;
        ElementWaterState state = Helper.waterToBePumpedOut(
            pendingFlowingWater, maxTransfer, currentWater, capacity, this::breakElement);
        currentWater = state.currentlyStoredWater();
        int waterAmount = state.pumpedWater();
        if (isBroken || end1.isFree() || end2.isFree()) {
            int lost = waterAmount + currentWater;
            currentWater = 0;
            pendingFlowingWater = 0;
            currentFlowingWater = waterAmount;
            return lost;
        }
        currentFlowingWater = waterAmount;
        pendingFlowingWater = 0;
        return 0;
    }

    // ------------------------------------------------------------------------
    // Break / Repair
    // ------------------------------------------------------------------------
    @Override
    public void breakElement() {
        this.isBroken = true;
        currentFlowingWater = 0;
    }

    @Override
    public boolean isBroken() { return isBroken; }

    @Override
    public void repair() { this.isBroken = false; }

    // ------------------------------------------------------------------------
    // Pipe splitting (for pump insertion)
    // ------------------------------------------------------------------------
    public Pipe[] splitForPump(Pump carriedPump) {
        Pipe leftPipe = new Pipe(getId() == null ? "PIPE_LEFT" : getId() + "_LEFT");
        Pipe rightPipe = new Pipe(getId() == null ? "PIPE_RIGHT" : getId() + "_RIGHT");

        leftPipe.end1.connectedTo = this.end1.connectedTo;
        if (leftPipe.end1.connectedTo != null) {
            leftPipe.end1.connectedTo.connect(leftPipe.end1);
        }

        rightPipe.end2.connectedTo = this.end2.connectedTo;
        if (rightPipe.end2.connectedTo != null) {
            rightPipe.end2.connectedTo.connect(rightPipe.end2);
        }

        leftPipe.end2.connectsTo(carriedPump);
        rightPipe.end1.connectsTo(carriedPump);

        return new Pipe[]{leftPipe, rightPipe};
    }

    public Pump getNextPump() {
        if (end2.connectedTo instanceof Pump) {
            return (Pump) end2.connectedTo;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Occupancy
    // ------------------------------------------------------------------------
    @Override
    public boolean canOccupy() {
        return occupants.isEmpty();
    }

    // ------------------------------------------------------------------------
    // Direction helpers (used by water simulator)
    // ------------------------------------------------------------------------
    public boolean isMeetingAtSamePipe() {
        return directionEnd2.isInput && directionEnd1.isInput;
    }

    public DirectionEnd resolveInputEnd() {
        if (directionEnd1.isInput()) return directionEnd1;
        if (directionEnd2.isInput()) return directionEnd2;
        return null;
    }

    public DirectionEnd resolveOutputEnd() {
        if (!directionEnd1.isInput()) return directionEnd1;
        if (!directionEnd2.isInput()) return directionEnd2;
        return null;
    }

    public DirectionEnd resolveEnd(PipeEnd end) {
        if (end == directionEnd1.getEnd()) return directionEnd1;
        if (end == directionEnd2.getEnd()) return directionEnd2;
        return null;
    }

    public boolean isConflict() { return isConflict; }
    public void setConflict(boolean conflict) { isConflict = conflict; }

    public int getCurrentFlowingWater() { return currentFlowingWater; }
    public void setCurrentFlowingWater(int currentFlowingWater) { this.currentFlowingWater = currentFlowingWater; }

    public int getCapacity() { return capacity; }
    public int getCurrentWater() { return currentWater; }

    // ------------------------------------------------------------------------
    // Legacy placement helper (used by UI)
    // ------------------------------------------------------------------------
    public boolean isVertical() {
        if (end1 != null && !end1.isFree()) {
            return end1.connectedTo.getX() == getX();
        } else if (end2 != null && !end2.isFree()) {
            return end2.connectedTo.getX() == getX();
        }
        return false;
    }

    public Point getFreeEndConnectionCoordinates(List<Point> adjacentFreePoints) {
        if (end1.isFree() && end2.isFree()) return null;
        int x = getX();
        int y = getY();
        boolean isVert = isVertical();
        return adjacentFreePoints.stream()
                                 .filter(p -> !isVert ? p.y == y : p.x == x)
                                 .findFirst()
                                 .orElse(null);
    }

    // ------------------------------------------------------------------------
    // ToString
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        String end1State = end1 == null || end1.connectedTo == null ? "FREE" : end1.connectedTo.getId();
        String end2State = end2 == null || end2.connectedTo == null ? "FREE" : end2.connectedTo.getId();
        String occupant = occupants.isEmpty() ? "NONE" : occupants.getFirst().getId();

        return String.format(
            "[STATE] PIPE %s orientation=%s broken=%s currentWater=%d end1=%s end2=%s occupant=%s",
            getId(),
            orientation,
            isBroken,
            currentWater,
            end1State,
            end2State,
            occupant);
    }

    public void determineOrientationFromConnections() {
        List<Directions> connectedDirs = new ArrayList<>();

        if (end1.connectedTo != null) {
            Directions dir = getDirectionTo(end1.connectedTo);
            if (dir != null) connectedDirs.add(dir);
        }
        if (end2.connectedTo != null) {
            Directions dir = getDirectionTo(end2.connectedTo);
            if (dir != null) connectedDirs.add(dir);
        }

        if (connectedDirs.isEmpty()) return;

        if (connectedDirs.size() == 1) {
            // Single connection - orient away from it
            Directions dir = connectedDirs.get(0);
            if (dir == Directions.NORTH || dir == Directions.SOUTH) {
                orientation = PipeOrientation.VERTICAL;
            } else {
                orientation = PipeOrientation.HORIZONTAL;
            }
        } else if (connectedDirs.size() == 2) {
            Directions d1 = connectedDirs.get(0);
            Directions d2 = connectedDirs.get(1);

            boolean isVertical = (d1 == Directions.NORTH || d1 == Directions.SOUTH) &&
                (d2 == Directions.NORTH || d2 == Directions.SOUTH);
            boolean isHorizontal = (d1 == Directions.EAST || d1 == Directions.WEST) &&
                (d2 == Directions.EAST || d2 == Directions.WEST);

            if (isVertical) {
                orientation = PipeOrientation.VERTICAL;
            } else if (isHorizontal) {
                orientation = PipeOrientation.HORIZONTAL;
            }
        }
    }

    private Directions getDirectionTo(ActiveElement target) {
        int dx = target.getX() - getX();
        int dy = target.getY() - getY();
        if (dx == 0 && dy == -1) {
            return Directions.NORTH;
        }
        if (dx == 1 && dy == 0) {
            return Directions.EAST;
        }
        if (dx == 0 && dy == 1) {
            return Directions.SOUTH;
        }
        if (dx == -1 && dy == 0) {
            return Directions.WEST;
        }
        return null;
    }

    /**
     * Automatically connects this pipe to adjacent elements (pipes, pumps, springs, cisterns).
     * Should be called after the pipe is placed on the map.
     *
     * @param map the game map to check adjacent elements
     */
    /**
     * Automatically connects this pipe to adjacent elements and adds to map.
     * Should be called after the pipe's position is set.
     *
     * @param map the game map to check adjacent elements and add to
     * @return true if added successfully
     */
    public boolean onConnect(GameMap map) {
        int x = getX();
        int y = getY();

        // First add to map
        map.addElement(this);


        // Get all four adjacent elements
        Element north = map.getElementAt(x, y - 1);
        Element south = map.getElementAt(x, y + 1);
        Element east = map.getElementAt(x + 1, y);
        Element west = map.getElementAt(x - 1, y);

        // Track which ends are used
        boolean end1Used = false;
        boolean end2Used = false;

        // Connect to north if it's a compatible element
        if (north instanceof ActiveElement activeNorth && !end1Used) {
            end1.connectsTo(activeNorth);
            end1Used = true;
            System.out.println("[PIPE] Connected north to " + activeNorth.getId());
        }

        // Connect to south
        if (south instanceof ActiveElement activeSouth && !end2Used) {
            if (!end1Used) {
                end1.connectsTo(activeSouth);
                end1Used = true;
            } else if (!end2Used) {
                end2.connectsTo(activeSouth);
                end2Used = true;
            }
            System.out.println("[PIPE] Connected south to " + activeSouth.getId());
        }

        // Connect to east
        if (east instanceof ActiveElement activeEast && !end2Used) {
            if (!end1Used) {
                end1.connectsTo(activeEast);
                end1Used = true;
            } else if (!end2Used) {
                end2.connectsTo(activeEast);
                end2Used = true;
            }
            System.out.println("[PIPE] Connected east to " + activeEast.getId());
        }

        // Connect to west
        if (west instanceof ActiveElement activeWest && !end2Used) {
            if (!end1Used) {
                end1.connectsTo(activeWest);
                end1Used = true;
            } else if (!end2Used) {
                end2.connectsTo(activeWest);
                end2Used = true;
            }
            System.out.println("[PIPE] Connected west to " + activeWest.getId());
        }

        // Update orientation after connections
        determineOrientationFromConnections();

        return true;
    }


}