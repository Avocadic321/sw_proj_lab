package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.map.*;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.ui.renderer.Grid;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ConnectModeElement extends HudElement {
    private final GameModel model;
    private final Grid grid = Grid.getInstance();

    private boolean active = false;
    private List<HighlightTile> pipeTiles = new ArrayList<>();
    private List<HighlightTile> pumpTiles = new ArrayList<>();

    public ConnectModeElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
    }

    // Called when dragging a PIPE
    public void activatePipeMode() {
        this.active = true;
        refreshPipeHighlights();
    }

    // Called when dragging a PUMP
    public void activatePumpMode() {
        this.active = true;
        refreshPumpHighlights();
    }

    public void deactivate() {
        this.active = false;
        pipeTiles.clear();
        pumpTiles.clear();
    }

    public boolean isActive() { return active; }

    public Rectangle getHoveredTileBounds(Point screenPos) {
        for (HighlightTile tile : pipeTiles)
            if (tile.bounds.contains(screenPos)) return tile.bounds;
        for (HighlightTile tile : pumpTiles)
            if (tile.bounds.contains(screenPos)) return tile.bounds;
        return null;
    }

    public Directions getDirectionForHoveredTile(Point screenPos) {
        for (HighlightTile tile : pipeTiles) {
            if (tile.bounds.contains(screenPos)) {
                return tile.direction;
            }
        }
        return null;
    }

    public boolean tryPlacePipe(ICarriable item, int slotIndex, Point screenPos, PipeOrientation orientation) {
        if (!active || !(item instanceof Pipe)) return false;
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return false;

        for (HighlightTile tile : pipeTiles) {
            if (tile.bounds.contains(screenPos)) {
                Pipe newPipe = new Pipe(tile.mapPos.x, tile.mapPos.y);
                // Set orientation based on direction
                if (tile.direction == Directions.NORTH || tile.direction == Directions.SOUTH) {
                    newPipe.setOrientation(PipeOrientation.VERTICAL);
                } else {
                    newPipe.setOrientation(PipeOrientation.HORIZONTAL);
                }
                Element currentPos = player.getCurrentPosition();
                if (currentPos instanceof ActiveElement activeElem) {
                    PipeEnd freeEnd = newPipe.getFreeEnd();
                    if (freeEnd != null) freeEnd.connectsTo(activeElem);
                    else newPipe.getEnd1().connectsTo(activeElem);
                }
                model.getGameMap().addElement(newPipe);
                plumber.getInventory().remove(slotIndex);
                deactivate();
                return true;
            }
        }
        return false;
    }

    public boolean tryPlacePump(ICarriable item, int slotIndex, Point screenPos) {
        if (!active || !(item instanceof Pump)) return false;
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return false;

        for (HighlightTile tile : pumpTiles) {
            if (tile.bounds.contains(screenPos)) {
                Pipe pipe = (Pipe) model.getGameMap().getElementAt(tile.mapPos.x, tile.mapPos.y);
                if (pipe == null) return false;
                List<Point> points = model.getGameMap().getAdjacentEmptyPositions(pipe);
                Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
                if (freePoint == null || freePoint.x != tile.mapPos.x || freePoint.y != tile.mapPos.y) return false;
                boolean success = plumber.placePump(pipe, (Pump) item, freePoint);
                if (success) {
                    model.getGameMap().addElement((Pump) item);
                    plumber.getInventory().remove(slotIndex);
                    deactivate();
                    return true;
                }
            }
        }
        return false;
    }

    // SIMPLE ALGORITHM for PIPE placement squares
    private void refreshPipeHighlights() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return;
        Element standing = player.getCurrentPosition();
        if (standing == null) return;

        grid.update(model.getGameMap());
        pipeTiles.clear();

        // Get all empty adjacent positions
        List<Point> emptyAdjacent = model.getGameMap().getAdjacentEmptyPositions(standing);

        for (Point emptyPos : emptyAdjacent) {
            // Calculate direction from standing element to empty square
            Directions dir = model.getGameMap().getDirection(standing, emptyPos);
            if (dir == null) continue;

            // Check if this direction is allowed
            if (!isDirectionAllowed(standing, dir)) continue;

            // Get screen bounds for this tile
            Point gridPos = grid.mapToGrid(emptyPos.x, emptyPos.y);
            if (gridPos == null) continue;
            Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
            if (bounds != null) {
                pipeTiles.add(new HighlightTile(bounds, emptyPos, dir));
            }
        }
    }

    private boolean isDirectionAllowed(Element standing, Directions dir) {
        if (standing instanceof Pipe pipe) {
            // Pipe: only same orientation
            if (pipe.getOrientation() == PipeOrientation.VERTICAL) {
                return dir == Directions.NORTH || dir == Directions.SOUTH;
            } else {
                return dir == Directions.EAST || dir == Directions.WEST;
            }
        }
        else if (standing instanceof Pump pump) {
            // Pump: only directions that are NOT already connected
            Set<Directions> occupied = pump.getAvailableDirections();
            return !occupied.contains(dir);
        }
        else if (standing instanceof Cistern || standing instanceof Spring) {
            // Cistern/Spring: all directions allowed
            return true;
        }
        return false;
    }

    private void refreshPumpHighlights() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return;
        Element standing = player.getCurrentPosition();
        if (standing == null) return;

        grid.update(model.getGameMap());
        pumpTiles.clear();

        // Get all empty tiles adjacent to the player
        List<Point> emptyAdjacentToPlayer = model.getGameMap().getAdjacentEmptyPositions(standing);

        // For each empty adjacent tile, check if it's a free end of any pipe
        for (Point emptyPos : emptyAdjacentToPlayer) {
            // Check if this empty position is adjacent to a pipe's free end
            // Actually, the empty position itself is where the pump would go
            // We need to find if there's a pipe adjacent to this empty position that has a free end

            // Get all adjacent elements to this empty position
            Element north = model.getGameMap().getElementAt(emptyPos.x, emptyPos.y - 1);
            Element south = model.getGameMap().getElementAt(emptyPos.x, emptyPos.y + 1);
            Element east = model.getGameMap().getElementAt(emptyPos.x + 1, emptyPos.y);
            Element west = model.getGameMap().getElementAt(emptyPos.x - 1, emptyPos.y);

            boolean hasPipeWithFreeEnd = false;

            // Check each adjacent element
            for (Element adj : new Element[]{north, south, east, west}) {
                if (adj instanceof Pipe pipe) {
                    // Check if this pipe has a free end
                    List<Point> points = model.getGameMap().getAdjacentEmptyPositions(pipe);
                    Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
                    if (freePoint != null && freePoint.x == emptyPos.x && freePoint.y == emptyPos.y) {
                        hasPipeWithFreeEnd = true;
                        break;
                    }
                }
            }

            if (hasPipeWithFreeEnd) {
                Point gridPos = grid.mapToGrid(emptyPos.x, emptyPos.y);
                if (gridPos == null) continue;
                Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
                if (bounds != null) {
                    pumpTiles.add(new HighlightTile(bounds, emptyPos, null));
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        for (HighlightTile tile : pipeTiles)
            drawHighlight(g, tile.bounds, new Color(0, 200, 0, 100), Color.GREEN, 3);
        for (HighlightTile tile : pumpTiles)
            drawHighlight(g, tile.bounds, new Color(70, 130, 220, 100), new Color(70, 130, 220), 2);
    }

    private void drawHighlight(Graphics2D g, Rectangle bounds, Color fill, Color stroke, int strokeWidth) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2.setColor(fill);
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setColor(stroke);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.dispose();
    }

    public void drawPossiblePumpConnections(Graphics2D g) {
        refreshPumpHighlights();
        for (HighlightTile tile : pumpTiles)
            drawHighlight(g, tile.bounds, new Color(70, 130, 220, 100), new Color(70, 130, 220), 2);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {}

    private record HighlightTile(Rectangle bounds, Point mapPos, Directions direction) {}
}