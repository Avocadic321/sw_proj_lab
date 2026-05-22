package software.project.ui.renderer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import software.project.core.GameModel;
import software.project.map.Element;
import software.project.map.GameMap;
import software.project.ui.ScreenManager;

public class MapRenderer {
    private static final int LEFT_BORDER = 40;
    private static final int RIGHT_BORDER = 40;
    private static final int TOP_BORDER = 40;
    private static final int BOTTOM_BORDER = 40;

    private static final int DEFAULT_GRID_WIDTH = 7;
    private static final int DEFAULT_GRID_HEIGHT = 7;

    private int gridWidth;
    private int gridHeight;
    private int tileSize;

    private int offsetX;
    private int offsetY;

    public void draw(Graphics2D g, GameModel model) {
        GameMap map = model.getGameMap();
        if (gridWidth == 0) {
            computeMapBounds(map);
        }
        computeLayout();

    }

    private void computeMapBounds(GameMap map) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        boolean hasElements = false;
        for (Element e : map.getElements()) {
            hasElements = true;
            int x = e.getX();
            int y = e.getY();
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        if (!hasElements || maxX < minX || maxY < minY) {
            // Fallback to default map size
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
            return;
        }

        gridWidth = maxX - minX + 1;
        gridHeight = maxY - minY + 1;

        // Sanity check – if still invalid, use defaults
        if (gridWidth <= 0 || gridHeight <= 0) {
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
        }
    }

    private void computeLayout() {
        ScreenManager sm = ScreenManager.getInstance();
        int availableWidth = sm.getVirtualWidth() - LEFT_BORDER - RIGHT_BORDER;
        int availableHeight = sm.getVirtualHeight() - TOP_BORDER - BOTTOM_BORDER;

        int tileByWidth = availableWidth / gridWidth;
        int tileByHeight = availableHeight / gridHeight;
        tileSize = Math.min(tileByWidth, tileByHeight);
        if (tileSize < 8) {
            tileSize = 8;
        }
        int totalMapWidth = tileSize * gridWidth;
        int totalMapHeight = tileSize * gridHeight;
        offsetX = LEFT_BORDER + (availableWidth - totalMapWidth) / 2;
        offsetY = TOP_BORDER + (availableHeight - totalMapHeight) / 2;
    }

    private void drawBackground(Graphics2D g) {

    }

    private void drawGrid(Graphics2D g) {
        // if necessary
    }

    private void drawPipes() {

    }

    private void drawPipes(Graphics2D g, GameMap map) {

    }

    /* ========== Helper Methods for Geometry and Screen ========== */
    private int getTileX(int gridX) {
        return offsetX + gridX * tileSize;
    }

    private int getTileY(int gridY) {
        return offsetY + gridY * tileSize;
    }

    private Point getCellCenter(int gridX, int gridY) {
        return new Point(getTileX(gridX) + tileSize / 2, getTileY(gridY) + tileSize / 2);
    }

    /**
     * Converts screen coordinates (from mouse event) to grid cell coordinates.
     * 
     * @return Point with gridX, gridY or null if outside the map area.
     */
    public Point screenToGrid(int screenX, int screenY) {
        if (screenX < offsetX || screenY < offsetY) {
            return null;
        }
        int cellX = (screenX - offsetX) / tileSize;
        int cellY = (screenY - offsetY) / tileSize;
        if (cellX < 0 || cellX >= gridWidth || cellY < 0 || cellY >= gridHeight) {
            return null;
        }
        return new Point(cellX, cellY);
    }

    /**
     * Returns the screen rectangle (in virtual coordinates) of a given grid cell.
     */
    public Rectangle getCellBounds(int gridX, int gridY) {
        return new Rectangle(offsetX + gridX * tileSize, offsetY + gridY * tileSize, tileSize, tileSize);
    }
}
