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
    private boolean pipeModeActive = false;
    private boolean pumpModeActive = false;

    private PipeOrientation draggedOrientation = PipeOrientation.VERTICAL;
    private List<HighlightTile> pipeTiles = new ArrayList<>();
    private List<HighlightTile> pumpTiles = new ArrayList<>();

    public ConnectModeElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
    }

    // Legacy
    public void setActive(boolean active) {
        this.active = active;
        if (active) setPipeMode(true, PipeOrientation.VERTICAL);
        else { setPipeMode(false, PipeOrientation.VERTICAL); setPumpMode(false); }
    }
    public boolean isActive() { return active; }

    // Pipe mode
    public void setPipeMode(boolean enabled, PipeOrientation orientation) {
        this.pipeModeActive = enabled;
        this.active = enabled;
        if (enabled) {
            this.pumpModeActive = false;
            this.draggedOrientation = orientation;
            refreshPipeHighlights();
        } else pipeTiles.clear();
    }
    public boolean isPipeMode() { return pipeModeActive; }
    public void setDraggedOrientation(PipeOrientation orientation) {
        this.draggedOrientation = orientation;
        if (pipeModeActive) refreshPipeHighlights();
    }

    // Pump mode
    public void setPumpMode(boolean enabled) {
        this.pumpModeActive = enabled;
        this.active = enabled;
        if (enabled) {
            this.pipeModeActive = false;
            refreshPumpHighlights();
        } else pumpTiles.clear();
    }
    public boolean isPumpMode() { return pumpModeActive; }

    public Rectangle getHoveredTileBounds(Point screenPos) {
        for (HighlightTile tile : pipeTiles)
            if (tile.bounds.contains(screenPos)) return tile.bounds;
        for (HighlightTile tile : pumpTiles)
            if (tile.bounds.contains(screenPos)) return tile.bounds;
        return null;
    }

    public boolean tryPlacePipe(ICarriable item, int slotIndex, Point screenPos, PipeOrientation orientation) {
        if (!pipeModeActive || !(item instanceof Pipe)) return false;
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return false;

        for (HighlightTile tile : pipeTiles) {
            if (tile.bounds.contains(screenPos)) {
                Pipe newPipe = new Pipe(tile.mapPos.x, tile.mapPos.y);
                newPipe.setOrientation(orientation);
                Element currentPos = player.getCurrentPosition();
                if (currentPos instanceof ActiveElement activeElem) {
                    PipeEnd freeEnd = newPipe.getFreeEnd();
                    if (freeEnd != null) freeEnd.connectsTo(activeElem);
                    else newPipe.getEnd1().connectsTo(activeElem);
                }
                model.getGameMap().addElement(newPipe);
                plumber.getInventory().remove(slotIndex);
                setPipeMode(false, orientation);
                return true;
            }
        }
        return false;
    }

    public boolean tryPlacePump(ICarriable item, int slotIndex, Point screenPos) {
        if (!pumpModeActive || !(item instanceof Pump)) return false;
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
                    setPumpMode(false);
                    return true;
                }
            }
        }
        return false;
    }

    private void refreshPipeHighlights() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return;
        Element current = player.getCurrentPosition();
        if (current == null) return;

        grid.update(model.getGameMap());

        // Determine allowed directions based on current element and dragged orientation
        Set<Directions> allowed = getAllowedPipeDirections(current, draggedOrientation);
        if (allowed.isEmpty()) { pipeTiles.clear(); return; }

        List<Point> emptyAdjacent = model.getGameMap().getAdjacentEmptyPositions(current);
        pipeTiles.clear();
        for (Point mapPos : emptyAdjacent) {
            Directions dir = model.getGameMap().getDirection(current, mapPos);
            if (dir == null || !allowed.contains(dir)) continue;
            Point gridPos = grid.mapToGrid(mapPos.x, mapPos.y);
            if (gridPos == null) continue;
            Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
            if (bounds != null) pipeTiles.add(new HighlightTile(bounds, mapPos));
        }
    }

    private Set<Directions> getAllowedPipeDirections(Element element, PipeOrientation orientation) {
        // For a pipe: use its orientation directly
        if (element instanceof Pipe pipe) {
            PipeOrientation currentOri = pipe.getOrientation();
            if (currentOri == PipeOrientation.VERTICAL)
                return EnumSet.of(Directions.NORTH, Directions.SOUTH);
            else
                return EnumSet.of(Directions.EAST, Directions.WEST);
        }
        // For pump: filter its available directions by orientation
        else if (element instanceof Pump pump) {
            Set<Directions> available = pump.getAvailableDirections();
            Set<Directions> result = EnumSet.noneOf(Directions.class);
            for (Directions dir : available) {
                if (orientation == PipeOrientation.VERTICAL && (dir == Directions.NORTH || dir == Directions.SOUTH))
                    result.add(dir);
                else if (orientation == PipeOrientation.HORIZONTAL && (dir == Directions.EAST || dir == Directions.WEST))
                    result.add(dir);
            }
            return result;
        }
        // Cistern / Spring: all directions matching orientation
        else if (element instanceof Cistern || element instanceof Spring) {
            if (orientation == PipeOrientation.VERTICAL)
                return EnumSet.of(Directions.NORTH, Directions.SOUTH);
            else
                return EnumSet.of(Directions.EAST, Directions.WEST);
        }
        return Set.of();
    }

    private void refreshPumpHighlights() {
        grid.update(model.getGameMap());
        List<Pipe> pipes = model.getGameMap().getAllPipes();
        pumpTiles.clear();
        for (Pipe pipe : pipes) {
            List<Point> points = model.getGameMap().getAdjacentEmptyPositions(pipe);
            Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
            if (freePoint == null) continue;
            Point gridPos = grid.mapToGrid(freePoint.x, freePoint.y);
            if (gridPos == null) continue;
            Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
            if (bounds != null) pumpTiles.add(new HighlightTile(bounds, freePoint));
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
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (pipeModeActive) refreshPipeHighlights();
        if (pumpModeActive) refreshPumpHighlights();
    }

    private record HighlightTile(Rectangle bounds, Point mapPos) {}
}