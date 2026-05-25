package software.project.ui.renderer;

import software.project.core.GameConfig;
import software.project.graphics.*;
import software.project.map.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ElementRenderer {
    private final SpriteManager sm = SpriteManager.getInstance();
    private final Map<Pump, Double> fanAngles = new HashMap<>();
    private final Map<Pump, Double> currentFanSpeeds = new HashMap<>();
    private final Random leakRandom = new Random();

    private static final double SMOOTHING_FACTOR = 3.0;

    private int waterPercentToColumn(int percent, int numFrames) {
        if (percent < 0) return 0;
        if (percent >= 100) return numFrames - 1;
        return (percent * numFrames) / 100;
    }

    private double directionToAngle(Point dir) {
        if (dir.x == 0 && (dir.y == -1 || dir.y == 1)) return 0;
        if ((dir.x == 1 || dir.x == -1) && dir.y == 0) return 90;
        return 0;
    }

    private double cornerAngle(Point d1, Point d2) {
        boolean hasNorth = d1.y == -1 || d2.y == -1;
        boolean hasSouth = d1.y == 1 || d2.y == 1;
        boolean hasEast = d1.x == 1 || d2.x == 1;
        boolean hasWest = d1.x == -1 || d2.x == -1;
        if (hasNorth && hasEast) return 0;
        if (hasEast && hasSouth) return 90;
        if (hasSouth && hasWest) return 180;
        if (hasWest && hasNorth) return 270;
        return 0;
    }

    public void drawPipes(Graphics2D g, List<Pipe> pipes, Grid grid) {
        SpriteSheet normalSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        SpriteSheet brokenSheet = sm.getSpriteSheet(SpriteSheets.PIPE_BROKEN);
        if (normalSheet == null || brokenSheet == null) return;
        for (Pipe pipe : pipes) {
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
                baseAngle = directionToAngle(dirs.get(0));
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
            } else continue;
            int col = 0;
            if (!pipe.isBroken()) {
                int percent = (pipe.getCurrentFlowingWater() * 100);
                col = waterPercentToColumn(percent, 4);
            }
            var sheet = pipe.isBroken() ? brokenSheet : normalSheet;
            int row = isCorner ? 1 : 0;
            var sprite = pipe.isBroken() ? sheet.getSprite(0, row) : sheet.getSprite(col, row);
            if (sprite == null) continue;
            Point center = grid.getCellCenter(pipe.getX(), pipe.getY());
            sprite.drawCentered(g, center.x, center.y, grid.getTileSize(), baseAngle);
        }
    }

    public void updateFanAngles(float deltaTime, List<Pump> pumps) {
        final double MIN_SPEED = 10.0;
        final double MAX_SPEED = 360.0;
        int maxFlow = GameConfig.PUMP_MAX_FLOW_PER_TICK;

        for (Pump pump : pumps) {
            int currentFlow = pump.getCurrentFlowingWater();
            int percentFlow = (int)((currentFlow / (float)maxFlow) * 100);
            if (percentFlow > 100) percentFlow = 100;
            if (percentFlow < 0) percentFlow = 0;

            double targetSpeed = MIN_SPEED + (percentFlow / 100.0) * (MAX_SPEED - MIN_SPEED);
            double currentSpeed = currentFanSpeeds.getOrDefault(pump, targetSpeed);
            double diff = targetSpeed - currentSpeed;
            currentSpeed += diff * Math.min(1.0, SMOOTHING_FACTOR * deltaTime);
            currentFanSpeeds.put(pump, currentSpeed);

            double currentAngle = fanAngles.getOrDefault(pump, 0.0);
            currentAngle += currentSpeed * deltaTime;
            currentAngle %= 360.0;
            fanAngles.put(pump, currentAngle);
        }
    }

    public void drawPumps(Graphics2D g, List<Pump> pumps, Grid grid) {
        SpriteSheet pumpSheet = sm.getSpriteSheet(SpriteSheets.PUMP);
        Sprite fanSprite = sm.getSprite(Sprites.PUMP_FAN);
        if (pumpSheet == null || fanSprite == null) return;
        int fanOffsetX = 0, fanOffsetY = 0;

        for (Pump pump : pumps) {
            int currentFlow = pump.getCurrentFlowingWater();
            int maxFlow = GameConfig.PUMP_MAX_FLOW_PER_TICK;
            int percentFlow = (int)((currentFlow / (float)maxFlow) * 100);
            if (percentFlow > 100) percentFlow = 100;
            if (percentFlow < 0) percentFlow = 0;
            int col = waterPercentToColumn(percentFlow, 5);
            Sprite baseSprite = pumpSheet.getSprite(col, 0);
            if (baseSprite == null) continue;
            Point center = grid.getCellCenter(pump.getX(), pump.getY());
            baseSprite.drawCentered(g, center.x, center.y, grid.getTileSize(), 0);

            double angle = fanAngles.getOrDefault(pump, 0.0);
            fanSprite.drawCentered(g, center.x + fanOffsetX, center.y + fanOffsetY, grid.getTileSize(), angle);
        }
    }

    public void drawCisterns(Graphics2D g, List<Cistern> cisterns, Grid grid) {
        SpriteSheet cisternSheet = sm.getSpriteSheet(SpriteSheets.CISTERN);
        if (cisternSheet == null) return;
        for (Cistern cistern : cisterns) {
            int percent = (cistern.getStoredWater() * 100) / cistern.getCapacity();
            int col = waterPercentToColumn(percent, 4);
            Sprite sprite = cisternSheet.getSprite(col, 0);
            if (sprite == null) continue;
            Point center = grid.getCellCenter(cistern.getX(), cistern.getY());
            sprite.drawCentered(g, center.x, center.y, grid.getTileSize(), 0);
        }
    }

    public void drawSprings(Graphics2D g, List<Spring> springs, Grid grid) {
        Sprite springSprite = sm.getSprite(Sprites.SPRING);
        Sprite springPipe = sm.getSprite(Sprites.SPRING_PIPE);
        int drawSize = (int)(grid.getTileSize() * 2.5f);
        for (Spring spring : springs) {
            Point center = grid.getCellCenter(spring.getX(), spring.getY());
            springSprite.drawCentered(g, center.x, center.y, drawSize, drawSize, 0);
            if (springPipe != null)
                springPipe.drawCentered(g, center.x, center.y, grid.getTileSize(), 0);
        }
    }
}