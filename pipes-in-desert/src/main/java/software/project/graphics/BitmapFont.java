package software.project.graphics;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Renders text using a sprite sheet of characters. The sheet is a grid of fixed‑size cells, each
 * containing one character.
 */
public class BitmapFont {
    private final SpriteSheet sheet;
    private final String mapping;
    private final Map<Character, Sprite> charCache = new HashMap<>();

    /**
     * Creates a bitmap font using a custom character mapping string.
     * The mapping string defines the exact order of characters in the sprite sheet.
     *
     * @param sheet   SpriteSheet containing all character sprites in row‑major order.
     * @param mapping String defining the order of characters (e.g., "abcdefghijklmnopqrstuvwxyz0123456789")
     */
    public BitmapFont(SpriteSheet sheet, String mapping) {
        this.sheet = sheet;
        this.mapping = mapping;
    }

    private Sprite getCharSprite(char c) {
        return charCache.computeIfAbsent(c, ch -> {
            int index = mapping.indexOf(ch);
            if (index < 0) return null;
            int col = index % sheet.getCols();
            int row = index / sheet.getCols();
            return sheet.getSprite(col, row);
        });
    }

    /**
     * Draws a string at (x, y) with optional scaling. Each character is drawn individually.
     */
    public void draw(Graphics2D g, String text, int x, int y, float scale) {
        int drawX = x;
        int drawW = (int) (sheet.getFrameWidth() * scale);
        int drawH = (int) (sheet.getFrameHeight() * scale);
        for (char c : text.toCharArray()) {
            Sprite sp = getCharSprite(c);
            if (sp != null) {
                sp.draw(g, drawX, y, drawW, drawH);
            }
            drawX += drawW;
        }
    }

    /**
     * Draws a single character at (x, y) with optional scaling.
     */
    public void drawChar(Graphics2D g, char c, int x, int y, float scale) {
        Sprite sp = getCharSprite(c);
        if (sp != null) {
            int drawW = (int) (sheet.getFrameWidth() * scale);
            int drawH = (int) (sheet.getFrameHeight() * scale);
            sp.draw(g, x, y, drawW, drawH);
        }
    }

    public int getCharWidth() {
        return sheet.getFrameWidth();
    }

    public int getCharHeight() {
        return sheet.getFrameHeight();
    }
}