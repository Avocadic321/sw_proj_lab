package software.project.ui.renderer;

import software.project.map.Directions;
import software.project.map.Element;
import software.project.map.Pipe;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class LeakRenderer {
    // ========== LEAK EFFECT CONFIGURATION ==========
    private static final int NOZZLE_WIDTH = 40;
    private static final int PARTICLE_SPACING = 3;
    private static final float WATER_VELOCITY = 50f;
    private static final float SPREAD_ANGLE = 0.5f;
    private static final int PARTICLES_PER_SOURCE = 2;
    private static final float SPAWN_RATE = 1.0f;
    // ================================================

    private final Map<Pipe, CopyOnWriteArrayList<LeakParticle>> pipeLeaks = new HashMap<>();
    private final Random leakRandom = new Random();

    private Directions getDirectionFromDelta(int dx, int dy) {
        if (dx == 0 && dy == -1) return Directions.NORTH;
        if (dx == 1 && dy == 0)  return Directions.EAST;
        if (dx == 0 && dy == 1)  return Directions.SOUTH;
        if (dx == -1 && dy == 0) return Directions.WEST;
        return null;
    }

    private Directions getDirectionFromElement(Element from, Element to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        return getDirectionFromDelta(dx, dy);
    }

    private Directions opposite(Directions dir) {
        switch (dir) {
            case NORTH: return Directions.SOUTH;
            case EAST:  return Directions.WEST;
            case SOUTH: return Directions.NORTH;
            case WEST:  return Directions.EAST;
            default: return dir;
        }
    }

    private Directions getLeakDirection(Pipe pipe) {
        if (pipe.getEnd1().isFree() && pipe.getEnd2().connectedTo != null) {
            Directions connectedDir = getDirectionFromElement(pipe, pipe.getEnd2().connectedTo);
            if (connectedDir != null) {
                return opposite(connectedDir);
            }
        }
        if (pipe.getEnd2().isFree() && pipe.getEnd1().connectedTo != null) {
            Directions connectedDir = getDirectionFromElement(pipe, pipe.getEnd1().connectedTo);
            if (connectedDir != null) {
                return opposite(connectedDir);
            }
        }
        return null;
    }

    private List<Point> getLeakPositions(Pipe pipe, Grid grid) {
        List<Point> positions = new ArrayList<>();
        Point center = grid.getCellCenter(pipe.getX(), pipe.getY());
        int tileSize = grid.getTileSize();
        int nozzleOffset = tileSize / 2;

        Directions leakDir = getLeakDirection(pipe);
        if (leakDir == null && pipe.isBroken()) {
            int halfWidth = NOZZLE_WIDTH / 2;
            for (int x = -halfWidth; x <= halfWidth; x += PARTICLE_SPACING) {
                for (int y = -halfWidth; y <= halfWidth; y += PARTICLE_SPACING) {
                    positions.add(new Point(center.x + x, center.y + y));
                }
            }
            return positions;
        }

        if (leakDir != null) {
            int halfWidth = NOZZLE_WIDTH / 2;
            int numPoints = NOZZLE_WIDTH / PARTICLE_SPACING;
            for (int i = 0; i <= numPoints; i++) {
                int offset = -halfWidth + i * PARTICLE_SPACING;
                switch (leakDir) {
                    case WEST:
                        positions.add(new Point(center.x - nozzleOffset, center.y + offset));
                        break;
                    case EAST:
                        positions.add(new Point(center.x + nozzleOffset, center.y + offset));
                        break;
                    case NORTH:
                        positions.add(new Point(center.x + offset, center.y - nozzleOffset));
                        break;
                    case SOUTH:
                        positions.add(new Point(center.x + offset, center.y + nozzleOffset));
                        break;
                }
            }
        }
        return positions;
    }

    private void addLeakParticles(Pipe pipe, Grid grid, List<Point> leakPositions, Directions leakDir, CopyOnWriteArrayList<LeakParticle> particles) {
        for (Point leakPos : leakPositions) {
            for (int p = 0; p < PARTICLES_PER_SOURCE; p++) {
                float baseSpeed = WATER_VELOCITY + leakRandom.nextFloat() * (WATER_VELOCITY * 0.5f);
                float vx, vy;
                switch (leakDir) {
                    case WEST:
                        vx = -baseSpeed;
                        vy = (leakRandom.nextFloat() - 0.5f) * SPREAD_ANGLE * baseSpeed;
                        break;
                    case EAST:
                        vx = baseSpeed;
                        vy = (leakRandom.nextFloat() - 0.5f) * SPREAD_ANGLE * baseSpeed;
                        break;
                    case NORTH:
                        vx = (leakRandom.nextFloat() - 0.5f) * SPREAD_ANGLE * baseSpeed;
                        vy = -baseSpeed;
                        break;
                    case SOUTH:
                        vx = (leakRandom.nextFloat() - 0.5f) * SPREAD_ANGLE * baseSpeed;
                        vy = baseSpeed;
                        break;
                    default:
                        vx = (leakRandom.nextFloat() - 0.5f) * 100;
                        vy = (leakRandom.nextFloat() - 0.5f) * 100;
                        break;
                }
                float size = 3 + leakRandom.nextFloat() * 5;
                particles.add(new LeakParticle(leakPos.x, leakPos.y, vx, vy, size));
            }
        }
    }

    public void update(float deltaTime, List<Pipe> pipes, Grid grid) {
        for (Pipe pipe : pipes) {
            boolean hasWater = pipe.getCurrentFlowingWater() > 0;
            boolean hasFreeEnd = pipe.getEnd1().isFree() || pipe.getEnd2().isFree();
            boolean isBroken = pipe.isBroken();
            boolean shouldLeak = hasWater && (hasFreeEnd || isBroken);

            if (shouldLeak) {
                CopyOnWriteArrayList<LeakParticle> particles = pipeLeaks.get(pipe);
                if (particles == null) {
                    particles = new CopyOnWriteArrayList<>();
                    pipeLeaks.put(pipe, particles);
                }

                if (leakRandom.nextFloat() < SPAWN_RATE * deltaTime * 60) {
                    List<Point> leakPositions = getLeakPositions(pipe, grid);
                    Directions leakDir = getLeakDirection(pipe);
                    if (!leakPositions.isEmpty() && leakDir != null) {
                        addLeakParticles(pipe, grid, leakPositions, leakDir, particles);
                    }
                }
                particles.removeIf(p -> !p.update(deltaTime));
            } else {
                CopyOnWriteArrayList<LeakParticle> particles = pipeLeaks.remove(pipe);
                if (particles != null) {
                    particles.clear();
                }
            }
        }
    }

    public void draw(Graphics2D g, List<Pipe> pipes) {
        for (Pipe pipe : pipes) {
            CopyOnWriteArrayList<LeakParticle> particles = pipeLeaks.get(pipe);
            if (particles != null) {
                for (LeakParticle p : particles) {
                    p.draw(g);
                }
            }
        }
    }

    public void clear() {
        for (CopyOnWriteArrayList<LeakParticle> particles : pipeLeaks.values()) {
            if (particles != null) {
                particles.clear();
            }
        }
        pipeLeaks.clear();
    }
}