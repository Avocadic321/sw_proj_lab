package software.project.ui.renderer;

import java.awt.*;
import java.util.ArrayList;

import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.graphics.*;
import software.project.map.*;
import software.project.ui.ScreenManager;

public class MapRenderer {
    private static final int DEFAULT_GRID_WIDTH = 7;
    private static final int DEFAULT_GRID_HEIGHT = 5;
    private static final int WATER_LEVEL_FRAMES = 4;

    // Border sizes (in tiles) - these only position the grid within the sand
    private static final int TOP_BORDER_TILES = 2;
    private static final int BOTTOM_BORDER_TILES = 1;
    private static final int LEFT_BORDER_TILES = 1;
    private static final int RIGHT_BORDER_TILES = 1;

    private int gridWidth;
    private int gridHeight;
    private int tileSize;
    private int offsetX;   // pixel offset for the grid top-left
    private int offsetY;

    public void draw(Graphics2D g, GameModel model) {
        var map = model.getGameMap();
        if (gridWidth == 0) computeMapBounds(map);
        computeLayout();

        // 1. Draw sand on all full tiles covering the screen, perfectly centered
        drawSandBackgroundCentered(g);

        // 2. Draw the border on the outermost tiles
        drawBorder(g);

        // 3. Draw the centered grid on top of the sand
        drawGridLines(g);

        // 4. Draw game elements
        drawPipes(g, map);
        drawPumps(g, map);
        drawCisterns(g, map);
        drawSprings(g, map);
    }

    private void computeMapBounds(GameMap map) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        boolean hasElements = false;

        for (Element e : map.getElements()) {
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

        // Tile size from vertical constraint (fills height with totalAreaHeight tiles)
        tileSize = vh / totalAreaHeight;
        if (tileSize < 8) tileSize = 8;

        // Center the total area within the screen
        int totalW = tileSize * totalAreaWidth;
        int totalH = tileSize * totalAreaHeight;
        int areaX = (vw - totalW) / 2;
        int areaY = (vh - totalH) / 2;

        // Grid top-left pixel position
        offsetX = areaX + LEFT_BORDER_TILES * tileSize;
        offsetY = areaY + TOP_BORDER_TILES * tileSize;
    }

    private void drawSandBackgroundCentered(Graphics2D g) {
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();

        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet borderSheet = sm.getSpriteSheet(SpriteSheets.MAP_BORDER);
        Sprite sandSprite = (borderSheet != null) ? borderSheet.getSprite(1, 1) : null;

        // Total columns/rows needed to cover the screen (round up)
        int totalCols = (int) Math.ceil((double) vw / tileSize);
        int totalRows = (int) Math.ceil((double) vh / tileSize);

        // Calculate total width/height of the tile grid
        int totalWidth = totalCols * tileSize;
        int totalHeight = totalRows * tileSize;

        // Calculate offset to center the tile grid evenly
        int offsetX = (vw - totalWidth) / 2;
        int offsetY = (vh - totalHeight) / 2;

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = offsetX + col * tileSize;
                int y = offsetY + row * tileSize;

                // Draw sand on all tiles
                if (sandSprite != null) {
                    sandSprite.draw(g, x, y, tileSize, tileSize);
                } else {
                    g.setColor(new Color(180, 150, 110));
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }

    private void drawBorder(Graphics2D g) {
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();

        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet borderSheet = sm.getSpriteSheet(SpriteSheets.MAP_BORDER);
        if (borderSheet == null) return;

        // Total columns/rows needed to cover the screen (round up)
        int totalCols = (int) Math.ceil((double) vw / tileSize);
        int totalRows = (int) Math.ceil((double) vh / tileSize);

        // Calculate total width/height of the tile grid
        int totalWidth = totalCols * tileSize;
        int totalHeight = totalRows * tileSize;

        // Calculate offset to center the tile grid evenly
        int offsetX = (vw - totalWidth) / 2;
        int offsetY = (vh - totalHeight) / 2;

        // Draw border only on the outermost tiles
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                // Only draw on the outermost edge
                boolean isEdge = (row == 0 || row == totalRows - 1 || col == 0 || col == totalCols - 1);
                if (!isEdge) continue;

                int x = offsetX + col * tileSize;
                int y = offsetY + row * tileSize;

                int spriteCol = 0;
                int spriteRow = 0;

                // Determine which border sprite to use based on position
                if (row == 0 && col == 0) {
                    // Top-left corner
                    spriteCol = 0;
                    spriteRow = 0;
                } else if (row == 0 && col == totalCols - 1) {
                    // Top-right corner
                    spriteCol = 2;
                    spriteRow = 0;
                } else if (row == totalRows - 1 && col == 0) {
                    // Bottom-left corner
                    spriteCol = 0;
                    spriteRow = 2;
                } else if (row == totalRows - 1 && col == totalCols - 1) {
                    // Bottom-right corner
                    spriteCol = 2;
                    spriteRow = 2;
                } else if (row == 0) {
                    // Top edge (middle)
                    spriteCol = 1;
                    spriteRow = 0;
                } else if (row == totalRows - 1) {
                    // Bottom edge (middle)
                    spriteCol = 1;
                    spriteRow = 2;
                } else if (col == 0) {
                    // Left edge (middle)
                    spriteCol = 0;
                    spriteRow = 1;
                } else if (col == totalCols - 1) {
                    // Right edge (middle)
                    spriteCol = 2;
                    spriteRow = 1;
                }

                Sprite borderSprite = borderSheet.getSprite(spriteCol, spriteRow);
                if (borderSprite != null) {
                    borderSprite.draw(g, x, y, tileSize, tileSize);
                }
            }
        }
    }

    private void drawGridLines(Graphics2D g) {
        g.setColor(new Color(100, 100, 100, 150));
        g.setStroke(new BasicStroke(3));
        for (int x = 0; x <= gridWidth; x++) {
            int sx = offsetX + x * tileSize;
            g.drawLine(sx, offsetY, sx, offsetY + gridHeight * tileSize);
        }
        for (int y = 0; y <= gridHeight; y++) {
            int sy = offsetY + y * tileSize;
            g.drawLine(offsetX, sy, offsetX + gridWidth * tileSize, sy);
        }
        g.setStroke(new BasicStroke(1));
    }

    // ---------- Element drawing methods (unchanged) ----------

    private void drawPipes(Graphics2D g, GameMap map) {
        var sm = SpriteManager.getInstance();
        var normalSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        var brokenSheet = sm.getSpriteSheet(SpriteSheets.PIPE_BROKEN);

        if (normalSheet == null || brokenSheet == null) {
            System.err.println("[ERROR] Pipe sprites not loaded – nothing drawn");
            return;
        }

        for (Pipe pipe : map.getAllPipes()) {
            var dirs = new ArrayList<Point>();
            for (var end : new PipeEnd[]{pipe.getEnd1(), pipe.getEnd2()}) {
                if (end.connectedTo != null) {
                    int dx = end.connectedTo.getX() - pipe.getX();
                    int dy = end.connectedTo.getY() - pipe.getY();
                    dirs.add(new Point(dx, dy));
                }
            }
            if (dirs.isEmpty()) continue;

            boolean isCorner;
            double baseAngle;
            if (dirs.size() == 1) {
                isCorner = false;
                baseAngle = directionToAngle(dirs.getFirst());
            } else if (dirs.size() == 2) {
                var d1 = dirs.get(0);
                var d2 = dirs.get(1);
                boolean opposite = (d1.x == -d2.x && d1.y == -d2.y);
                if (opposite) {
                    isCorner = false;
                    baseAngle = directionToAngle(d1);
                } else {
                    isCorner = true;
                    baseAngle = cornerAngle(d1, d2);
                }
            } else {
                continue;
            }

            int col = 0;
            if (!pipe.isBroken()) {
                int percent = (pipe.getCurrentWater() * 100) / pipe.getCapacity();
                col = waterPercentToColumn(percent, WATER_LEVEL_FRAMES);
            }

            var sheet = pipe.isBroken() ? brokenSheet : normalSheet;
            int row = isCorner ? 1 : 0;
            var sprite = pipe.isBroken() ? sheet.getSprite(0, row) : sheet.getSprite(col, row);
            if (sprite == null) continue;

            var center = getCellCenter(pipe.getX(), pipe.getY());
            sprite.drawCentered(g, center.x, center.y, tileSize, baseAngle);
        }
    }

    private void drawPumps(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet pumpSheet = sm.getSpriteSheet(SpriteSheets.PUMP);
        Sprite fanSprite = sm.getSprite(Sprites.PUMP_FAN);

        if (pumpSheet == null || fanSprite == null) {
            System.err.println("[ERROR] Pump sprites not loaded");
            return;
        }

        final double MIN_SPEED_DEG_PER_SEC = 30.0;
        final double MAX_SPEED_DEG_PER_SEC = 180.0;

        for (Pump pump : map.getAllPumps()) {
            int percent = (pump.getStoredWater() * 100) / GameConfig.PUMP_TANK_CAPACITY;
            int col = waterPercentToColumn(percent, 5);
            Sprite baseSprite = pumpSheet.getSprite(col, 0);
            if (baseSprite == null) continue;

            Point center = getCellCenter(pump.getX(), pump.getY());
            baseSprite.drawCentered(g, center.x, center.y, tileSize, 0);

            double fanSpeed = MIN_SPEED_DEG_PER_SEC + (percent / 100.0) * (MAX_SPEED_DEG_PER_SEC - MIN_SPEED_DEG_PER_SEC);
            double startOffset = ((pump.getX() * 31) + (pump.getY() * 97)) % 360;
            double angle = (System.currentTimeMillis() * (fanSpeed / 1000.0) + startOffset) % 360;
            fanSprite.drawCentered(g, center.x, center.y, tileSize, angle);
        }
    }

    private void drawCisterns(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet cisternSheet = sm.getSpriteSheet(SpriteSheets.CISTERN);

        for (Cistern cistern : map.getAllCisterns()) {
            int percent = (cistern.getStoredWater() * 100) / cistern.getCapacity();
            int col = waterPercentToColumn(percent, WATER_LEVEL_FRAMES);
            Sprite sprite = cisternSheet.getSprite(col, 0);
            if (sprite == null) continue;

            Point center = getCellCenter(cistern.getX(), cistern.getY());
            sprite.drawCentered(g, center.x, center.y, tileSize, 0);
        }
    }

    private void drawSprings(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        Sprite spriteSprite = sm.getSprite(Sprites.SPRING);

        for (Spring spring : map.getAllSprings()) {
            Point center = getCellCenter(spring.getX(), spring.getY());
            spriteSprite.drawCentered(g, center.x, center.y, tileSize, 0);
        }
    }

    private static int waterPercentToColumn(int percent, int numFrames) {
        if (percent < 0) return 0;
        if (percent >= 100) return numFrames - 1;
        return (percent * numFrames) / 100;
    }

    // ---------- Geometry helpers ----------
    private double directionToAngle(Point dir) {
        if (dir.x == 0 && (dir.y == -1 || dir.y == 1)) return 0;
        if ((dir.x == 1 || dir.x == -1) && dir.y == 0) return 90;
        return 0;
    }

    private double cornerAngle(Point d1, Point d2) {
        boolean hasNorth = d1.y == -1 || d2.y == -1;
        boolean hasSouth = d1.y == 1 || d2.y == 1;
        boolean hasEast  = d1.x == 1 || d2.x == 1;
        boolean hasWest  = d1.x == -1 || d2.x == -1;

        if (hasNorth && hasEast) return 0;
        if (hasEast  && hasSouth) return 90;
        if (hasSouth && hasWest) return 180;
        if (hasWest  && hasNorth) return 270;
        return 0;
    }

    public int getTileX(int gridX) { return offsetX + gridX * tileSize; }
    public int getTileY(int gridY) { return offsetY + gridY * tileSize; }

    private Point getCellCenter(int gridX, int gridY) {
        return new Point(getTileX(gridX) + tileSize / 2, getTileY(gridY) + tileSize / 2);
    }

    public Point screenToGrid(int screenX, int screenY) {
        if (screenX < offsetX || screenY < offsetY) return null;
        int cellX = (screenX - offsetX) / tileSize;
        int cellY = (screenY - offsetY) / tileSize;
        if (cellX < 0 || cellX >= gridWidth || cellY < 0 || cellY >= gridHeight) {
            return null;
        }
        return new Point(cellX, cellY);
    }

    public Rectangle getCellBounds(int gridX, int gridY) {
        return new Rectangle(offsetX + gridX * tileSize, offsetY + gridY * tileSize, tileSize, tileSize);
    }
}