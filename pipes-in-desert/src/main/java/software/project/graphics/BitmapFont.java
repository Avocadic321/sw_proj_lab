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
    private final int charWidth;
    private final int charHeight;
    private final int firstChar; // ASCII value of the first character in the sheet
    private final int columns;
    private final Map<Character, Sprite> charCache = new HashMap<>();

    /**
     * @param sheet      SpriteSheet containing all character sprites in row‑major order.
     * @param charWidth  width of one character in pixels (original sheet size).
     * @param charHeight height of one character in pixels.
     * @param firstChar  ASCII code of the first character in the sheet (e.g., 32 for space).
     */
    public BitmapFont(SpriteSheet sheet, int charWidth, int charHeight, int firstChar) {
        this.sheet = sheet;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.firstChar = firstChar;
        this.columns = sheet.getCols();
    }

    private Sprite getCharSprite(char c) {
        return charCache.computeIfAbsent(
            c, ch -> {
                int index = ch - firstChar;
                if (index < 0 || index >= sheet.getTotalSprites()) {
                    return null;
                }
                int col = index % columns;
                int row = index / columns;
                return sheet.getSprite(col, row);
            }
        );
    }

    /**
     * Draws a string at (x, y) with optional scaling. Each character is drawn individually.
     */
    public void draw(Graphics2D g, String text, int x, int y, float scale) {
        int drawX = x;
        int drawW = (int) (charWidth * scale);
        int drawH = (int) (charHeight * scale);
        for (char c : text.toCharArray()) {
            Sprite sp = getCharSprite(c);
            if (sp != null) {
                sp.draw(g, drawX, y, drawW, drawH);
            }
            drawX += drawW;
        }
    }
}
