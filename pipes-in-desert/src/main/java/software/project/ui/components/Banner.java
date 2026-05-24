package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;

public class Banner extends Component {

    private ImageComponent background;
    private BitmapFont font;
    private String text;
    private float textScale;

    /**
     * Creates a banner with centered text using a specific font.
     *
     * @param x         x position of the banner
     * @param y         y position of the banner
     * @param width     width of the banner
     * @param height    height of the banner
     * @param sprite    background sprite
     * @param fontKey   which font to use (e.g., BitmapFonts.FONT_MONO)
     * @param text      initial text content
     * @param textScale scale factor for the text size
     */
    public Banner(
        int x,
        int y,
        int width,
        int height,
        Sprite sprite,
        BitmapFonts fontKey,
        String text,
        float textScale
    ) {
        super(x, y, width, height);
        this.background = new ImageComponent(x, y, width, height, sprite);
        this.font = ResourceManager.getInstance().getFont(fontKey);
        this.text = text;
        this.textScale = textScale;
    }

    /**
     * Creates a banner with centered text using the default MONO font.
     *
     * @param x         x position of the banner
     * @param y         y position of the banner
     * @param width     width of the banner
     * @param height    height of the banner
     * @param sprite    background sprite
     * @param text      initial text content
     * @param textScale scale factor for the text size
     */
    public Banner(
        int x,
        int y,
        int width,
        int height,
        Sprite sprite,
        String text,
        float textScale
    ) {
        this(x, y, width, height, sprite, BitmapFonts.FONT_MONO, text, textScale);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextScale(float scale) {
        this.textScale = scale;
    }

    public void setFont(BitmapFonts fontKey) {
        this.font = ResourceManager.getInstance().getFont(fontKey);
    }

    @Override
    public void draw(Graphics2D g) {
        background.draw(g);

        if (font != null && text != null) {
            int centerX = x + width / 2;
            int centerY = y + height / 2 + (int) (font.getCharHeight() * textScale / 2);
            font.draw(g, text, centerX, centerY, textScale);
        }
    }

    @Override
    public void update() {
        // No dynamic update needed
    }
}