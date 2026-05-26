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

public class ConnectionsElement extends HudElement {
    private final GameModel model;
    private final Grid grid = Grid.getInstance();

    private boolean active = false;
    private List<HighlightTile> pipeTiles = new ArrayList<>();
    private List<HighlightTile> pumpTiles = new ArrayList<>();

    public ConnectionsElement(GameModel model) {
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
                Pipe pipeToPlace = (Pipe) item;
                Element currentPos = player.getCurrentPosition();

                // Set position
                pipeToPlace.setPosition(tile.mapPos.x, tile.mapPos.y);

                // Set orientation
                if (tile.direction == Directions.NORTH || tile.direction == Directions.SOUTH) {
                    pipeToPlace.setOrientation(PipeOrientation.VERTICAL);
                } else {
                    pipeToPlace.setOrientation(PipeOrientation.HORIZONTAL);
                }

                // Connect to current position (player's standing element)
                if (currentPos instanceof ActiveElement activeElem) {
                    PipeEnd freeEnd = pipeToPlace.getFreeEnd();
                    if (freeEnd != null) {
                        freeEnd.connectsTo(activeElem);
                    } else {
                        pipeToPlace.getEnd1().connectsTo(activeElem);
                    }
                }

                // Add to map
                // model.getGameMap().addElement(pipeToPlace);

                pipeToPlace.onConnect(model.getGameMap());

                // Remove from inventory by ID
                plumber.getInventory().removeById(pipeToPlace.getId());

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
        if (!(player.getCurrentPosition() instanceof Pipe standingPipe)) return false;

        for (HighlightTile tile : pumpTiles) {
            if (tile.bounds.contains(screenPos)) {
                Pump pump = (Pump) item;

                // Set pump position
                pump.setPosition(tile.mapPos.x, tile.mapPos.y);

                // Connect to the standing pipe (just connect, no orientation)
                standingPipe.getFreeEnd().connectsTo(pump);

                // Add to map
                // model.getGameMap().addElement(pump);

                // Auto-connect
                pump.onConnect(model.getGameMap());

                // Remove from inventory
                plumber.getInventory().removeById(pump.getId());

                deactivate();
                return true;
            }
        }
        return false;
    }

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
        if (!(standing instanceof Pipe standingPipe)) return;

        grid.update(model.getGameMap());
        pumpTiles.clear();

        // Get the two directions based on pipe orientation
        Directions dir1, dir2;
        if (standingPipe.getOrientation() == PipeOrientation.VERTICAL) {
            dir1 = Directions.NORTH;
            dir2 = Directions.SOUTH;
        } else {
            dir1 = Directions.EAST;
            dir2 = Directions.WEST;
        }

        // Check both directions
        Point pos1 = getPositionInDirection(standing, dir1);
        Point pos2 = getPositionInDirection(standing, dir2);

        // Check if position is empty (no element) and add highlight
        if (pos1 != null && model.getGameMap().isEmpty(pos1.x, pos1.y)) {
            Point gridPos = grid.mapToGrid(pos1.x, pos1.y);
            if (gridPos != null) {
                Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
                if (bounds != null) {
                    pumpTiles.add(new HighlightTile(bounds, pos1, dir1));
                    System.out.println("Added pump tile at " + pos1);
                }
            }
        }

        if (pos2 != null && model.getGameMap().isEmpty(pos2.x, pos2.y)) {
            Point gridPos = grid.mapToGrid(pos2.x, pos2.y);
            if (gridPos != null) {
                Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
                if (bounds != null) {
                    pumpTiles.add(new HighlightTile(bounds, pos2, dir2));
                    System.out.println("Added pump tile at " + pos2);
                }
            }
        }

        System.out.println("Total pumpTiles: " + pumpTiles.size());
    }

    private Point getPositionInDirection(Element element, Directions dir) {
        int x = element.getX();
        int y = element.getY();
        switch (dir) {
            case NORTH: return new Point(x, y - 1);
            case SOUTH: return new Point(x, y + 1);
            case EAST:  return new Point(x + 1, y);
            case WEST:  return new Point(x - 1, y);
            default: return null;
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