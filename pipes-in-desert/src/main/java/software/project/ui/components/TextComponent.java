package software.project.ui.components;

import java.awt.Color;
import java.awt.Graphics2D;

import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;

/**
 * A simple text component that renders text at a specific position.
 */
public class TextComponent extends Component {
    private String text;
    private BitmapFont font;
    private float scale;
    private Color color;
    private int xOffset = 0;
    private int yOffset = 0;

    public TextComponent(int x, int y, String text, float scale) {
        super(x, y, 0, 0);
        this.text = text;
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        this.scale = scale;
        this.color = Color.WHITE;
        recomputeSize();
    }

    public void setText(String text) {
        this.text = text;
        recomputeSize();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setScale(float scale) {
        this.scale = scale;
        recomputeSize();
    }

    public void setFont(BitmapFont font) {
        this.font = font;
        recomputeSize();
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getTextWidth() {
        if (font == null || text == null) return 0;
        return (int) (text.length() * font.getCharWidth() * scale);
    }

    public int getTextHeight() {
        if (font == null) return 0;
        return (int) (font.getCharHeight() * scale);
    }

    private void recomputeSize() {
        if (font != null && text != null) {
            width = getTextWidth();
            height = getTextHeight();
        } else {
            width = 0;
            height = 0;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (text == null || text.isEmpty() || font == null) return;

        int drawX = x + xOffset;
        int drawY = y + yOffset;

        Color oldColor = g.getColor();
        g.setColor(color);
        font.draw(g, text, drawX, drawY, scale);
        g.setColor(oldColor);
    }
}