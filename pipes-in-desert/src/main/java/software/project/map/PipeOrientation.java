package software.project.map;

import java.util.EnumSet;
import java.util.Set;

public enum PipeOrientation {
    VERTICAL,
    HORIZONTAL;

    public Directions getDirection1() {
        switch (this) {
            case VERTICAL:   return Directions.NORTH;
            case HORIZONTAL: return Directions.EAST;
            default: return null;
        }
    }

    public Directions getDirection2() {
        switch (this) {
            case VERTICAL:   return Directions.SOUTH;
            case HORIZONTAL: return Directions.WEST;
            default: return null;
        }
    }

    public Set<Directions> getDirections() {
        return EnumSet.of(getDirection1(), getDirection2());
    }

    public PipeOrientation rotateClockwise() {
        switch (this) {
            case VERTICAL:   return HORIZONTAL;
            case HORIZONTAL: return VERTICAL;
            default: return this;
        }
    }

    public PipeOrientation rotateCounterClockwise() {
        return rotateClockwise(); // same for binary case
    }

    public static PipeOrientation fromDirectionPair(Directions d1, Directions d2) {
        if (d1 == null || d2 == null) return VERTICAL;
        Set<Directions> set = EnumSet.of(d1, d2);
        if (set.contains(Directions.NORTH) && set.contains(Directions.SOUTH))
            return VERTICAL;
        if (set.contains(Directions.EAST) && set.contains(Directions.WEST))
            return HORIZONTAL;
        // if not opposite, return default (no corners for now)
        return VERTICAL;
    }
}