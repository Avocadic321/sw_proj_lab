package software.project.ui.renderer;

import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Cistern;
import software.project.map.Element;
import software.project.map.GameMap;
import software.project.map.Pipe;
import software.project.map.PipeEnd;
import software.project.map.Pump;
import software.project.map.Spring;
import software.project.models.Player;
import software.project.models.Team;
import software.project.ui.ScreenManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MapRenderer {
    private static final int DEFAULT_GRID_WIDTH = 7;
    private static final int DEFAULT_GRID_HEIGHT = 5;
    private static final int WATER_LEVEL_FRAMES = 4;

    // Border sizes (in tiles)
    private static final int TOP_BORDER_TILES = 1;
    private static final int BOTTOM_BORDER_TILES = 1;
    private static final int LEFT_BORDER_TILES = 1;
    private static final int RIGHT_BORDER_TILES = 1;

    // Player scale relative to tile size (1.0 = full tile size)
    private static final float PLAYER_SCALE = 0.6f;
    private static final float STEP_DURATION = 0.15f;
    private final GameModel model;
    private final List<ClickableElement> clickableElements = new ArrayList<>();
    private int gridWidth;
    private int gridHeight;
    private int tileSize;
    private int offsetX;
    private int offsetY;
    private float arrowTick = 0f;
    private boolean animating = false;
    private Player animatingPlayer = null;
    private List<Element> animationPath = new ArrayList<>();
    private int animationStep = 0;
    private float stepTimer = 0f;
    public MapRenderer(GameModel model) {
        this.model = model;
    }

    private static int waterPercentToColumn(int percent, int numFrames) {
        if (percent < 0) {
            return 0;
        }
        if (percent >= 100) {
            return numFrames - 1;
        }
        return (percent * numFrames) / 100;
    }

    public void draw(Graphics2D g) {
        var map = model.getGameMap();
        if (gridWidth == 0) {
            computeMapBounds(map);
        }
        computeLayout();

        // 1. Draw sand on all full tiles covering the screen, perfectly centered
        drawSandBackgroundCentered(g);

        // 2. Draw the centered grid on top of the sand
        drawGridLines(g);

        // 3. Draw game elements
        drawSprings(g, map);
        drawPipes(g, map);
        drawPumps(g, map);
        drawCisterns(g, map);

        drawPlayers(g);
        drawCurrentPlayer(g);
        rebuildClickTargets(map);
    }

    private void computeMapBounds(GameMap map) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        boolean hasElements = false;

        for (Element e : map.getElements()) {
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

        // Tile size from vertical constraint
        tileSize = vh / totalAreaHeight;
        if (tileSize < 8) {
            tileSize = 8;
        }

        // Center the total area within the screen
        int totalW = tileSize * totalAreaWidth;
        int totalH = tileSize * totalAreaHeight;
        int areaX = (vw - totalW) / 2;
        int areaY = (vh - totalH) / 2;

        // Grid top-left pixel position
        offsetX = areaX + LEFT_BORDER_TILES * tileSize;
        offsetY = areaY + TOP_BORDER_TILES * tileSize;
    }

    public void drawLetterboxSand(Graphics2D g) {
        int panelW = ScreenManager.getInstance().getPanel().getWidth();
        int panelH = ScreenManager.getInstance().getPanel().getHeight();

        int bufW = ScreenManager.GAME_WIDTH;
        int bufH = ScreenManager.GAME_HEIGHT;
        double scaleX = (double) panelW / bufW;
        double scaleY = (double) panelH / bufH;
        double scale = Math.min(scaleX, scaleY);
        int scaledTile = (int) (tileSize * scale);
        if (scaledTile < 1) {
            scaledTile = 1;
        }

        // Letterbox offset (where the virtual buffer starts on the screen)
        int offsetXScreen = (panelW - (int) (bufW * scale)) / 2;
        int offsetYScreen = (panelH - (int) (bufH * scale)) / 2;

        // Calculate starting positions aligned with the virtual buffer's tiling grid
        int startX = offsetXScreen % scaledTile;
        int startY = offsetYScreen % scaledTile;

        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet borderSheet = sm.getSpriteSheet(SpriteSheets.MAP_BORDER);
        Sprite sandSprite = (borderSheet != null) ? borderSheet.getSprite(1, 1) : null;
        if (sandSprite == null) {
            sandSprite = sm.getSprite(Sprites.GRASS);
        }

        // Tile across the whole screen, extending BEYOND the bottom edge to ensure no gap
        for (int y = startY - scaledTile; y < panelH + scaledTile; y += scaledTile) {
            for (int x = startX - scaledTile; x < panelW + scaledTile; x += scaledTile) {
                sandSprite.draw(g, x, y, scaledTile, scaledTile);
            }
        }
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

                if (sandSprite != null) {
                    sandSprite.draw(g, x, y, tileSize, tileSize);
                } else {
                    g.setColor(new Color(180, 150, 110));
                    g.fillRect(x, y, tileSize, tileSize);
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

    // ---------- Element drawing methods ----------

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
            if (dirs.isEmpty()) {
                continue;
            }

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

            // Water level column
            int col = 0;
            if (!pipe.isBroken()) {
                int percent = (pipe.getCurrentWater() * 100) / pipe.getCapacity();
                col = waterPercentToColumn(percent, WATER_LEVEL_FRAMES);
            }

            var sheet = pipe.isBroken() ? brokenSheet : normalSheet;
            int row = isCorner ? 1 : 0;
            var sprite = pipe.isBroken() ? sheet.getSprite(0, row) : sheet.getSprite(col, row);
            if (sprite == null) {
                continue;
            }

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

        // Tweak these values to center the fan blades manually (can be negative)
        int fanOffsetX = 0;
        int fanOffsetY = 0;

        final double MIN_SPEED_DEG_PER_SEC = 30.0;
        final double MAX_SPEED_DEG_PER_SEC = 180.0;

        for (Pump pump : map.getAllPumps()) {
            int percent = (pump.getStoredWater() * 100) / GameConfig.PUMP_TANK_CAPACITY;
            int col = waterPercentToColumn(percent, 5);
            Sprite baseSprite = pumpSheet.getSprite(col, 0);
            if (baseSprite == null) {
                continue;
            }

            Point center = getCellCenter(pump.getX(), pump.getY());
            baseSprite.drawCentered(g, center.x, center.y, tileSize, 0);

            double fanSpeed = MIN_SPEED_DEG_PER_SEC + (percent / 100.0) * (MAX_SPEED_DEG_PER_SEC - MIN_SPEED_DEG_PER_SEC);
            double startOffset = ((pump.getX() * 31) + (pump.getY() * 97)) % 360;
            double angle = (System.currentTimeMillis() * (fanSpeed / 1000.0) + startOffset) % 360;

            // Apply manual offset to center the fan
            fanSprite.drawCentered(g, center.x + fanOffsetX, center.y + fanOffsetY, tileSize, angle);
        }
    }

    private void drawCisterns(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet cisternSheet = sm.getSpriteSheet(SpriteSheets.CISTERN);

        for (Cistern cistern : map.getAllCisterns()) {
            int percent = (cistern.getStoredWater() * 100) / cistern.getCapacity();
            int col = waterPercentToColumn(percent, WATER_LEVEL_FRAMES);
            Sprite sprite = cisternSheet.getSprite(col, 0);
            if (sprite == null) {
                continue;
            }

            Point center = getCellCenter(cistern.getX(), cistern.getY());
            sprite.drawCentered(g, center.x, center.y, tileSize, 0);
        }
    }

    private void drawSprings(Graphics2D g, GameMap map) {
        SpriteManager sm = SpriteManager.getInstance();
        Sprite springSprite = sm.getSprite(Sprites.SPRING);
        Sprite springPipe = sm.getSprite(Sprites.SPRING_PIPE);

        // 3x3 tile size
        int drawSize = (int) (tileSize * 2.5f);

        for (Spring spring : map.getAllSprings()) {
            Point center = getCellCenter(spring.getX(), spring.getY());

            // Draw the 3x3 spring sprite – this will cover the spring tile and all 8 adjacent tiles
            springSprite.drawCentered(g, center.x, center.y, drawSize, drawSize, 0);

            // Draw the pipe connection (usually 1x1) – draw centered at the spring's tile
            if (springPipe != null) {
                springPipe.drawCentered(g, center.x, center.y, tileSize, 0);
            }
        }
    }

    public void update(float deltaTime) {
        arrowTick += deltaTime;

        if (animating) {
            stepTimer += deltaTime;
            if (stepTimer >= STEP_DURATION) {
                stepTimer -= STEP_DURATION;
                animationStep++;

                if (animationStep >= animationPath.size() - 1) {
                    animatingPlayer.moveTo(animationPath.getLast());
                    animating = false;
                    animatingPlayer = null;
                    animationPath.forEach(Element::unlockElement);
                    animationPath.clear();
                }
            }
        }
    }

    private void drawCurrentPlayer(Graphics2D g) {
        Player current = animating ? animatingPlayer : model.getTurnManager().getCurrentPlayer();
        if (current == null) {
            return;
        }

        SpriteManager sm = SpriteManager.getInstance();
        boolean isSaboteur = model.getSaboteursTeam().getPlayers().contains(current);
        Sprite sprite = isSaboteur ? sm.getSprite(Sprites.SABOTEUR) : sm.getSprite(Sprites.PLUMBER);

        Point drawCenter;

        if (animating && animationStep + 1 < animationPath.size()) {
            Element from = animationPath.get(animationStep);
            Element to = animationPath.get(animationStep + 1);
            float t = stepTimer / STEP_DURATION;

            Point fromCenter = getCellCenter(from.getX(), from.getY());
            Point toCenter = getCellCenter(to.getX(), to.getY());

            int lerpX = (int) (fromCenter.x + (toCenter.x - fromCenter.x) * t);
            int lerpY = (int) (fromCenter.y + (toCenter.y - fromCenter.y) * t);
            drawCenter = new Point(lerpX, lerpY);
        } else {
            Element position = current.getCurrentPosition();
            if (position == null) {
                return;
            }
            drawCenter = getCellCenter(position.getX(), position.getY());
        }

        // Scale the player sprite
        int playerDrawSize = (int) (tileSize * PLAYER_SCALE);

        if (sprite != null) {
            sprite.drawCentered(g, drawCenter.x, drawCenter.y, playerDrawSize, 0);
        }

        // Scale the arrow pointer to match the player size
        int bounceOffset = (int) (Math.sin(arrowTick * 6.0) * 6 + 6);
        int tipX = drawCenter.x;
        int tipY = drawCenter.y - playerDrawSize / 2 - 6 - bounceOffset;
        int arrowW = playerDrawSize / 4;
        int arrowH = playerDrawSize / 4;

        int[] xPoints = {tipX, tipX + arrowW, tipX - arrowW};
        int[] yPoints = {tipY, tipY - arrowH, tipY - arrowH};

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 220, 0));
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(xPoints, yPoints, 3);
    }

    private void drawPlayers(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SpriteManager sm = SpriteManager.getInstance();
        Sprite spriteSaboteur = sm.getSprite(Sprites.SABOTEUR);
        Sprite spritePlumber = sm.getSprite(Sprites.PLUMBER);
        Player current = animating ? animatingPlayer : model.getTurnManager().getCurrentPlayer();
        Team saboteurs = model.getSaboteursTeam();

        // Scale the player sprite
        int playerDrawSize = (int) (tileSize * PLAYER_SCALE);

        for (Player player : saboteurs.getPlayers()) {
            if (animating && player.equals(current)) {
                continue;
            }
            Element position = player.getCurrentPosition();
            if (position == null) {
                continue;
            }
            Point center = getCellCenter(position.getX(), position.getY());
            spriteSaboteur.drawCentered(g, center.x, center.y, playerDrawSize, 0);
        }

        Team plumbers = model.getPlumbersTeam();
        for (Player player : plumbers.getPlayers()) {
            if (animating && player.equals(current)) {
                continue;
            }
            Element position = player.getCurrentPosition();
            if (position == null) {
                continue;
            }
            Point center = getCellCenter(position.getX(), position.getY());
            spritePlumber.drawCentered(g, center.x, center.y, playerDrawSize, 0);
        }
    }

    public boolean mousePressed(MouseEvent e) {
        if (animating) {
            return true;
        }

        Player player = model.getTurnManager().getCurrentPlayer();
        for (ClickableElement ce : clickableElements) {
            if (ce.bounds().contains(e.getX(), e.getY())) {
                List<Element> path = model.getGameMap().buildPathToDestination(
                    player.getCurrentPosition(), ce.element());

                if (path.size() <= 1) {
                    return true;
                }

                for (Element el : path) {
                    el.lockElement(player);
                }

                animatingPlayer = player;
                animationPath = path;
                animationStep = 0;
                stepTimer = 0f;
                animating = true;
                return true;
            }
        }
        return false;
    }

    // ---------- Geometry helpers ----------
    private double directionToAngle(Point dir) {
        if (dir.x == 0 && (dir.y == -1 || dir.y == 1)) {
            return 0;
        }
        if ((dir.x == 1 || dir.x == -1) && dir.y == 0) {
            return 90;
        }
        return 0;
    }

    private double cornerAngle(Point d1, Point d2) {
        boolean hasNorth = d1.y == -1 || d2.y == -1;
        boolean hasSouth = d1.y == 1 || d2.y == 1;
        boolean hasEast = d1.x == 1 || d2.x == 1;
        boolean hasWest = d1.x == -1 || d2.x == -1;

        if (hasNorth && hasEast) {
            return 0;
        }
        if (hasEast && hasSouth) {
            return 90;
        }
        if (hasSouth && hasWest) {
            return 180;
        }
        if (hasWest && hasNorth) {
            return 270;
        }
        return 0;
    }

    public int getTileX(int gridX) {
        return offsetX + gridX * tileSize;
    }

    public int getTileY(int gridY) {
        return offsetY + gridY * tileSize;
    }

    private Point getCellCenter(int gridX, int gridY) {
        return new Point(getTileX(gridX) + tileSize / 2, getTileY(gridY) + tileSize / 2);
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

    public Rectangle getCellBounds(int gridX, int gridY) {
        return new Rectangle(offsetX + gridX * tileSize, offsetY + gridY * tileSize, tileSize, tileSize);
    }

    private void rebuildClickTargets(GameMap map) {
        clickableElements.clear();
        for (Element e : map.getElements()) {
            Rectangle bounds = getCellBounds(e.getX(), e.getY());
            clickableElements.add(new ClickableElement(e, bounds));
        }
    }

    private record ClickableElement(Element element, Rectangle bounds) {
    }
}