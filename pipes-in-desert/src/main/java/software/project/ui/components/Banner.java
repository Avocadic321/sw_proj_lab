package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.BitmapFont;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheets;

public class Banner extends Component {
    private static BitmapFont defaultFont;

    private ImageComponent background;
    private BitmapFont font;
    private String text;
    private float textScale;

    /**
     * Creates a banner with a centered text using the default font.
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
        int x, int y, int width, int height,
        Sprite sprite, String text, float textScale
    ) {
        this(x, y, width, height, sprite, null, text, textScale);
    }

    /**
     * Creates a banner with a centered text and a custom font.
     *
     * @param x         x position of the banner
     * @param y         y position of the banner
     * @param width     width of the banner
     * @param height    height of the banner
     * @param sprite    background sprite
     * @param font      the BitmapFont to use for text (can be null to use default)
     * @param text      initial text content
     * @param textScale scale factor for the text size
     */
    public Banner(
        int x, int y, int width, int height,
        Sprite sprite, BitmapFont font,
        String text, float textScale
    ) {
        super(x, y, width, height);
        this.background = new ImageComponent(x, y, width, height, sprite);
        this.font = (font != null) ? font : getDefaultFont();
        this.text = text;
        this.textScale = textScale;
    }

    private static BitmapFont getDefaultFont() {
        if (defaultFont == null) {
            var sheet = SpriteManager.getInstance().getSpriteSheet(SpriteSheets.FONT);
            if (sheet != null && sheet.isValid()) {
                defaultFont = new BitmapFont(sheet, 8, 8, 32);
            }
        }
        return defaultFont;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextScale(float scale) {
        this.textScale = scale;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
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