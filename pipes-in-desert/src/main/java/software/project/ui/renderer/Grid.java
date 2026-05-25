package software.project.ui.renderer;

import software.project.map.GameMap;
import software.project.ui.ScreenManager;

import java.awt.Point;
import java.awt.Rectangle;

public class Grid {
    private static final int DEFAULT_GRID_WIDTH = 7;
    private static final int DEFAULT_GRID_HEIGHT = 5;
    private static final int TOP_BORDER_TILES = 1;
    private static final int BOTTOM_BORDER_TILES = 1;
    private static final int LEFT_BORDER_TILES = 1;
    private static final int RIGHT_BORDER_TILES = 1;

    private int gridWidth;
    private int gridHeight;
    private int tileSize;
    private int offsetX;
    private int offsetY;

    public void computeFromMap(GameMap map) {
        computeMapBounds(map);
        computeLayout();
    }

    private void computeMapBounds(GameMap map) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        boolean hasElements = false;
        for (var e : map.getElements()) {
            hasElements = true;
            int x = e.getX(), y = e.getY();
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        if (!hasElements || maxX < minX || maxY < minY) {
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
        } else {
            gridWidth = maxX - minX + 1;
            gridHeight = maxY - minY + 1;
        }
        if (gridWidth <= 0 || gridHeight <= 0) {
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
        }
    }

    private void computeLayout() {
        int totalAreaWidth = gridWidth + LEFT_BORDER_TILES + RIGHT_BORDER_TILES;
        int totalAreaHeight = gridHeight + TOP_BORDER_TILES + BOTTOM_BORDER_TILES;
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();
        tileSize = vh / totalAreaHeight;
        if (tileSize < 8) {
            tileSize = 8;
        }
        int totalW = tileSize * totalAreaWidth;
        int totalH = tileSize * totalAreaHeight;
        int areaX = (vw - totalW) / 2;
        int areaY = (vh - totalH) / 2;
        offsetX = areaX + LEFT_BORDER_TILES * tileSize;
        offsetY = areaY + TOP_BORDER_TILES * tileSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public Point getCellCenter(int gridX, int gridY) {
        return new Point(
            offsetX + gridX * tileSize + tileSize / 2,
            offsetY + gridY * tileSize + tileSize / 2
        );
    }

    public Rectangle getCellBounds(int gridX, int gridY) {
        return new Rectangle(
            offsetX + gridX * tileSize,
            offsetY + gridY * tileSize,
            tileSize, tileSize
        );
    }

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
}