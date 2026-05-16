package software.project.graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;

/**
 * Renders text using a loaded TrueType font (.ttf).
 */
public class TrueTypeFont {
    private Font font;

    /**
     * Loads a TTF file from the classpath.
     * @param path    resource path, e.g. "/fonts/Roboto.ttf"
     * @param size    initial font size in pixels
     */
    public TrueTypeFont(String path, float size) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("TTF font not found: " + path);
                return;
            }
            Font temp = Font.createFont(Font.TRUETYPE_FONT, is);
            font = temp.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    /** Changes the font size. */
    public void setSize(float size) {
        if (font != null) font = font.deriveFont(size);
    }

    /** Returns the underlying AWT Font. */
    public Font getFont() { return font; }

    /**
     * Draws text at (x, y) with the given colour.
     * @param g      graphics context
     * @param text   string to draw
     * @param x      left x coordinate
     * @param y      baseline y coordinate
     * @param colour colour (use g.setColor before calling if you prefer)
     */
    public void draw(Graphics2D g, String text, int x, int y, java.awt.Color colour) {
        if (font == null) return;
        g.setFont(font);
        g.setColor(colour);
        g.drawString(text, x, y);
    }

    /**
     * Draws text centred horizontally at the given y.
     * @param g        graphics context
     * @param text     string
     * @param centerX  horizontal centre
     * @param y        baseline y
     * @param colour   colour
     */
    public void drawCentered(Graphics2D g, String text, int centerX, int y, java.awt.Color colour) {
        if (font == null) return;
        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(colour);
        g.drawString(text, centerX - width / 2, y);
    }
}
