package software.project.ui.renderer;

import java.awt.Point;
import java.awt.Rectangle;

import software.project.map.GameMap;
import software.project.ui.ScreenManager;

public class Grid {
    private static Grid instance;
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

    // Map bounds (absolute coordinates)
    private int mapMinX = 0;
    private int mapMinY = 0;

    private Grid() {}

    public static Grid getInstance() {
        if (instance == null) {
            instance = new Grid();
        }
        return instance;
    }

    public void update(GameMap map) {
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
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        if (!hasElements || maxX < minX || maxY < minY) {
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
            mapMinX = 0;
            mapMinY = 0;
        } else {
            gridWidth = maxX - minX + 1;
            gridHeight = maxY - minY + 1;
            mapMinX = minX;
            mapMinY = minY;
        }
        if (gridWidth <= 0 || gridHeight <= 0) {
            gridWidth = DEFAULT_GRID_WIDTH;
            gridHeight = DEFAULT_GRID_HEIGHT;
            mapMinX = 0;
            mapMinY = 0;
        }
    }

    private void computeLayout() {
        int totalAreaWidth = gridWidth + LEFT_BORDER_TILES + RIGHT_BORDER_TILES;
        int totalAreaHeight = gridHeight + TOP_BORDER_TILES + BOTTOM_BORDER_TILES;
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();
        int tileW = vw / totalAreaWidth;
        int tileH = vh / totalAreaHeight;
        tileSize = Math.min(tileW, tileH);
        if (tileSize < 8) tileSize = 8;
        int totalW = tileSize * totalAreaWidth;
        int totalH = tileSize * totalAreaHeight;
        int areaX = (vw - totalW) / 2;
        int areaY = (vh - totalH) / 2;
        offsetX = areaX + LEFT_BORDER_TILES * tileSize;
        offsetY = areaY + TOP_BORDER_TILES * tileSize;
    }

    // ----- Getters -----
    public int getTileSize() { return tileSize; }
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getMapMinX() { return mapMinX; }
    public int getMapMinY() { return mapMinY; }

    /**
     * Convert absolute map coordinates to grid indices (0..width-1).
     * Returns null if coordinates are outside the map bounds.
     */
    public Point mapToGrid(int mapX, int mapY) {
        int gridX = mapX - mapMinX;
        int gridY = mapY - mapMinY;
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight)
            return null;
        return new Point(gridX, gridY);
    }

    /**
     * Returns the centre point of a cell in screen coordinates.
     * @param gridX grid index (0..width-1)
     * @param gridY grid index (0..height-1)
     */
    public Point getCellCenter(int gridX, int gridY) {
        return new Point(
            offsetX + gridX * tileSize + tileSize / 2,
            offsetY + gridY * tileSize + tileSize / 2
        );
    }

    /**
     * Returns the bounding rectangle of a cell in screen coordinates.
     * Returns null if grid indices are out of bounds.
     */
    public Rectangle getCellBounds(int gridX, int gridY) {
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight)
            return null;
        return new Rectangle(
            offsetX + gridX * tileSize,
            offsetY + gridY * tileSize,
            tileSize, tileSize
        );
    }

    /**
     * Convert screen coordinates to grid indices.
     * Returns null if the point is outside the grid area.
     */
    public Point screenToGrid(int screenX, int screenY) {
        if (screenX < offsetX || screenY < offsetY) return null;
        int cellX = (screenX - offsetX) / tileSize;
        int cellY = (screenY - offsetY) / tileSize;
        if (cellX < 0 || cellX >= gridWidth || cellY < 0 || cellY >= gridHeight) return null;
        return new Point(cellX, cellY);
    }
}