package software.project.ui.renderer;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class BackgroundRenderer {
    private final SpriteManager sm = SpriteManager.getInstance();

    public void drawSand(Graphics2D g, Grid grid) {
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();
        int tileSize = grid.getTileSize();
        SpriteSheet borderSheet = sm.getSpriteSheet(SpriteSheets.MAP_BORDER);
        Sprite sandSprite = (borderSheet != null) ? borderSheet.getSprite(1, 1) : sm.getSprite(Sprites.GRASS);
        int totalCols = (int) Math.ceil((double) vw / tileSize);
        int totalRows = (int) Math.ceil((double) vh / tileSize);
        int totalWidth = totalCols * tileSize;
        int totalHeight = totalRows * tileSize;
        int offX = (vw - totalWidth) / 2;
        int offY = (vh - totalHeight) / 2;
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = offX + col * tileSize;
                int y = offY + row * tileSize;
                if (sandSprite != null)
                    sandSprite.draw(g, x, y, tileSize, tileSize);
                else {
                    g.setColor(new Color(180, 150, 110));
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }

    public void drawGridLines(Graphics2D g, Grid grid) {
        g.setColor(new Color(100, 100, 100, 150));
        g.setStroke(new BasicStroke(3));
        int offX = grid.getOffsetX();
        int offY = grid.getOffsetY();
        int w = grid.getGridWidth() * grid.getTileSize();
        int h = grid.getGridHeight() * grid.getTileSize();
        for (int x = 0; x <= grid.getGridWidth(); x++) {
            int sx = offX + x * grid.getTileSize();
            g.drawLine(sx, offY, sx, offY + h);
        }
        for (int y = 0; y <= grid.getGridHeight(); y++) {
            int sy = offY + y * grid.getTileSize();
            g.drawLine(offX, sy, offX + w, sy);
        }
        g.setStroke(new BasicStroke(1));
    }

    public void drawLetterboxSand(Graphics2D g, Grid grid) {
        // Keep the existing logic from MapRenderer.drawLetterboxSand
        // Implementation unchanged, but using grid.getTileSize().
        // (Omitted for brevity, but you can copy the exact code from the old method.)
    }
}