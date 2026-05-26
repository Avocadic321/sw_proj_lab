package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.map.*;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.ui.renderer.Grid;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectModeElement extends HudElement {
    private final GameModel model;
    private final Grid grid = Grid.getInstance();
    private boolean active = false;
    private List<HighlightTile> highlightedTiles = new ArrayList<>();

    public ConnectModeElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) refreshHighlights();
        else highlightedTiles.clear();
    }

    public boolean isActive() { return active; }

    public boolean tryPlacePipe(ICarriable item, int slotIndex, Point screenPos, PipeOrientation orientation) {
        if (!active || !(item instanceof Pipe)) return false;

        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return false;

        for (HighlightTile tile : highlightedTiles) {
            if (tile.bounds.contains(screenPos)) {
                Pipe newPipe = new Pipe(tile.gridPos.x, tile.gridPos.y);
                newPipe.setOrientation(orientation);
                Element currentPos = player.getCurrentPosition();
                if (currentPos instanceof ActiveElement activeElem) {
                    PipeEnd freeEnd = newPipe.getFreeEnd();
                    if (freeEnd != null) freeEnd.connectsTo(activeElem);
                    else newPipe.getEnd1().connectsTo(activeElem);
                }
                model.getGameMap().addElement(newPipe);
                plumber.getInventory().remove(slotIndex);
                setActive(false);
                return true;
            }
        }
        return false;
    }

    private void refreshHighlights() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return;
        Element pos = player.getCurrentPosition();
        if (pos == null) return;

        List<Point> emptyAdjacent = model.getGameMap().getAdjacentEmptyPositions(pos);
        highlightedTiles.clear();
        grid.update(model.getGameMap());
        for (Point p : emptyAdjacent) {
            Rectangle bounds = grid.getCellBounds(p.x, p.y);
            if (bounds != null) {
                highlightedTiles.add(new HighlightTile(bounds, p));
            }
        }
        active = !highlightedTiles.isEmpty();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return;
        for (HighlightTile tile : highlightedTiles) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2.setColor(new Color(0, 200, 0, 100));
            g2.fillRect(tile.bounds.x, tile.bounds.y, tile.bounds.width, tile.bounds.height);
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(tile.bounds.x, tile.bounds.y, tile.bounds.width, tile.bounds.height);
            g2.dispose();
        }
    }

    // Helper for possible pump connections (optional – can be moved to HudLayer)
    public void drawPossiblePumpConnections(Graphics2D g) {
        if (!active) return;
        List<Pipe> pipes = model.getGameMap().getAllPipes();
        for (Pipe pipe : pipes) {
            List<Point> points = model.getGameMap().getAdjacentEmptyPositions(
                model.getGameMap().getElementAt(pipe.getX(), pipe.getY()));
            Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
            if (freePoint == null) continue;
            Rectangle tileRect = grid.getCellBounds(freePoint.x, freePoint.y);
            if (tileRect != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setColor(new Color(0, 200, 0, 100));
                g2.fillRect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
                g2.dispose();
            }
        }
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (active) refreshHighlights();
    }

    private record HighlightTile(Rectangle bounds, Point gridPos) {}
}