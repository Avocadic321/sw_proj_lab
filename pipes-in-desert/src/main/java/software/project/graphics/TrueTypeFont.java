package software.project.graphics;

import java.awt.Font;
import java.awt.Graphics2D;

public class TrueTypeFont {
    private Font font;

    public TrueTypeFont(String path, float size) {
        font = ResourceLoader.loadTrueTypeFont(path, size);
        if (font == null) {
            System.err.println("TTF font not found: " + path);
        }
    }

    public void setSize(float size) {
        if (font != null) font = font.deriveFont(size);
    }

    public Font getFont() { return font; }

    public void draw(Graphics2D g, String text, int x, int y, java.awt.Color colour) {
        if (font == null) return;
        g.setFont(font);
        g.setColor(colour);
        g.drawString(text, x, y);
    }

    public void drawCentered(Graphics2D g, String text, int centerX, int y, java.awt.Color colour) {
        if (font == null) return;
        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(colour);
        g.drawString(text, centerX - width / 2, y);
    }
}