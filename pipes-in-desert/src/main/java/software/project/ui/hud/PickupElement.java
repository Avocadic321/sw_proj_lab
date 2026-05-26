package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.map.*;
import software.project.models.Player;
import software.project.ui.renderer.Grid;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PickupElement extends HudElement {
    private final GameModel model;
    private final Grid grid = Grid.getInstance();
    private final SpriteManager sm = SpriteManager.getInstance();

    private boolean active = false;
    private List<HighlightTile> pickupTiles = new ArrayList<>();

    private PickupListener pickupListener;

    public interface PickupListener {
        void onPickup(Pipe pipe, Point mapPos);
    }

    public void setPickupListener(PickupListener listener) {
        this.pickupListener = listener;
    }

    public PickupElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
    }

    public void activatePickupMode() {
        this.active = true;
        refreshPickupHighlights();
    }

    public void deactivate() {
        this.active = false;
        pickupTiles.clear();
    }

    public boolean isActive() { return active; }

    public Rectangle getHoveredTileBounds(Point screenPos) {
        for (HighlightTile tile : pickupTiles) {
            if (tile.bounds.contains(screenPos)) return tile.bounds;
        }
        return null;
    }

    public boolean tryPickupPipe(Point screenPos) {
        if (!active) return false;

        for (HighlightTile tile : pickupTiles) {
            if (tile.bounds.contains(screenPos)) {
                if (pickupListener != null) {
                    Pipe pipe = (Pipe) model.getGameMap().getElementAt(tile.mapPos.x, tile.mapPos.y);
                    if (pipe != null) {
                        pickupListener.onPickup(pipe, tile.mapPos);
                        deactivate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void refreshPickupHighlights() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return;
        Element standing = player.getCurrentPosition();
        if (standing == null) return;

        grid.update(model.getGameMap());
        pickupTiles.clear();

        List<Element> adjacentElements = model.getGameMap().getAdjacentElements(standing);

        for (Element adj : adjacentElements) {
            if (adj instanceof Pipe pipe) {
                // Check if the pipe is occupied by any player
                if (!pipe.getOccupants().isEmpty()) {
                    System.out.println("Pipe " + pipe.getId() + " is occupied, cannot pick up");
                    continue;
                }
                Point mapPos = new Point(pipe.getX(), pipe.getY());
                Point gridPos = grid.mapToGrid(mapPos.x, mapPos.y);
                if (gridPos == null) continue;
                Rectangle bounds = grid.getCellBounds(gridPos.x, gridPos.y);
                if (bounds != null) {
                    pickupTiles.add(new HighlightTile(bounds, mapPos, pipe));
                    System.out.println("Added pickup tile for pipe " + pipe.getId());
                }
            }
        }
    }

    /**
     * Draws a pipe with a light red tint overlay (same technique as disabled button)
     */
    private void drawLightRedTintedPipe(Graphics2D g, Pipe pipe, Rectangle bounds) {
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        if (pipeSheet == null) return;

        PipeOrientation orientation = pipe.getOrientation();
        if (orientation == null) orientation = PipeOrientation.VERTICAL;

        Sprite pipeSprite = pipeSheet.getSprite(0, 0);
        if (pipeSprite == null) return;

        // Draw the pipe with a light red tint
        BufferedImage temp = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG = temp.createGraphics();

        // Draw the pipe sprite
        double angle = (orientation == PipeOrientation.HORIZONTAL) ? 90 : 0;
        int centerX = bounds.width / 2;
        int centerY = bounds.height / 2;

        Graphics2D spriteG = temp.createGraphics();
        pipeSprite.drawCentered(spriteG, centerX, centerY, bounds.width, angle);
        spriteG.dispose();

        // Apply light red tint (lighter alpha)
        tempG.setComposite(AlphaComposite.SrcAtop);
        tempG.setColor(new Color(255, 100, 100, 120)); // Light red with moderate transparency
        tempG.fillRect(0, 0, bounds.width, bounds.height);
        tempG.dispose();

        g.drawImage(temp, bounds.x, bounds.y, null);
    }

    @Override
    public void draw(Graphics2D g) {
        for (HighlightTile tile : pickupTiles) {
            drawLightRedTintedPipe(g, tile.pipe, tile.bounds);
        }
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (active) refreshPickupHighlights();
    }

    private record HighlightTile(Rectangle bounds, Point mapPos, Pipe pipe) {}
}