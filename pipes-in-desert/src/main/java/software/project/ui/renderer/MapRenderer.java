package software.project.ui.renderer;

import java.awt.*;
import java.util.ArrayList;
import software.project.core.GameModel;
import software.project.graphics.*;
import software.project.map.*;
import software.project.ui.ScreenManager;

public class MapRenderer {
    private static final int LEFT_BORDER = 40;
    private static final int RIGHT_BORDER = 40;
    private static final int TOP_BORDER = 40;
    private static final int BOTTOM_BORDER = 40;

    private static final int DEFAULT_GRID_WIDTH = 7;
    private static final int DEFAULT_GRID_HEIGHT = 7;

    private static final int WATER_LEVEL_FRAMES = 4;

    private int gridWidth;
    private int gridHeight;
    private int tileSize;
    private int offsetX;
    private int offsetY;

    public void draw(Graphics2D g, GameModel model) {
        var map = model.getGameMap();
        if (gridWidth == 0) computeMapBounds(map);
        computeLayout();

        drawBackground(g);
        drawGrid(g);
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
        var sm = ScreenManager.getInstance();
        int availW = sm.getVirtualWidth() - LEFT_BORDER - RIGHT_BORDER;
        int availH = sm.getVirtualHeight() - TOP_BORDER - BOTTOM_BORDER;

        tileSize = Math.min(availW / gridWidth, availH / gridHeight);
        if (tileSize < 8) tileSize = 8;

        int totalW = tileSize * gridWidth;
        int totalH = tileSize * gridHeight;
        offsetX = LEFT_BORDER + (availW - totalW) / 2;
        offsetY = TOP_BORDER + (availH - totalH) / 2;
    }

    private void drawBackground(Graphics2D g) {
        int w = ScreenManager.getInstance().getVirtualWidth();
        int h = ScreenManager.getInstance().getVirtualHeight();

        g.setPaint(new GradientPaint(0, 0, new Color(230, 200, 150), 0, h, new Color(160, 120, 80)));
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(210, 180, 140));
        g.fillRect(offsetX - 5, offsetY - 5, tileSize * gridWidth + 10, tileSize * gridHeight + 10);
        g.setColor(new Color(180, 150, 110));
        g.fillRect(offsetX, offsetY, tileSize * gridWidth, tileSize * gridHeight);
    }

    private void drawGrid(Graphics2D g) {
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

    private void drawPipes(Graphics2D g, GameMap map) {
        var sm = SpriteManager.getInstance();
        var normalSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        var brokenSheet = sm.getSpriteSheet(SpriteSheets.PIPE_BROKEN);

        if (normalSheet == null || brokenSheet == null) {
            System.err.println("[ERROR] Pipe sprites not loaded – nothing drawn");
            return;
        }

        for (Pipe pipe : map.getAllPipes()) {
            // Collect connection directions
            var dirs = new ArrayList<Point>();
            for (var end : new PipeEnd[]{pipe.getEnd1(), pipe.getEnd2()}) {
                if (end.connectedTo != null) {
                    int dx = end.connectedTo.getX() - pipe.getX();
                    int dy = end.connectedTo.getY() - pipe.getY();
                    dirs.add(new Point(dx, dy));
                }
            }
            if (dirs.isEmpty()) continue;

            // Determine shape and angle
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

            // Water level column (0..WATER_LEVEL_FRAMES-1)
            int col = 0;
            if (!pipe.isBroken()) {
                int percent = (pipe.getCurrentWater() * 100) / pipe.getCapacity();
                col = waterPercentToColumn(percent, WATER_LEVEL_FRAMES);
            }

            // Select sprite
            var sheet = pipe.isBroken() ? brokenSheet : normalSheet;
            int row = isCorner ? 1 : 0;
            var sprite = pipe.isBroken() ? sheet.getSprite(0, row) : sheet.getSprite(col, row);
            if (sprite == null) continue;

            // Draw centred and rotated
            var center = getCellCenter(pipe.getX(), pipe.getY());
            sprite.drawCentered(g, center.x, center.y, tileSize, baseAngle);
        }
    }

    private void drawPumps(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        Sprite pumpSprite = sm.getSprite(Sprites.PUMP);

        for (Pump pump : map.getAllPumps()) {
            Point center = getCellCenter(pump.getX(), pump.getY());
            pumpSprite.drawCentered(g, center.x, center.y, tileSize, 0);
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

    /**
     * Generic method to map a percentage (0‑99) to a column index.
     * @param percent water fullness percent, expected 0..99 (never 100)
     * @param numFrames number of sprite frames (e.g., 4)
     * @return column index from 0 to numFrames-1
     */
    private static int waterPercentToColumn(int percent, int numFrames) {
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