package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;
import software.project.ui.ScreenManager;

public class Banner extends Component {

    private ImageComponent background;
    private BitmapFont font;
    private String text;
    private float textScale;
    private float spriteScale;

    // Text offset for fine-tuning position inside the banner
    private int textOffsetX = 0;
    private int textOffsetY = 0;

    // For centering with offsets
    private boolean centeredHorizontally = false;
    private boolean centeredVertically = false;
    private int centerOffsetX = 0;
    private int centerOffsetY = 0;

    /**
     * Creates a banner with centered text. Initial position is (0,0).
     * Use centering methods or setPosition to place it.
     */
    public Banner(Sprite sprite, float spriteScale, BitmapFonts fontKey, String text, float textScale) {
        super(0, 0, (int)(sprite.getWidth() * spriteScale), (int)(sprite.getHeight() * spriteScale));
        this.background = new ImageComponent(0, 0, width, height, sprite);
        this.spriteScale = spriteScale;
        this.font = ResourceManager.getInstance().getFont(fontKey);
        this.text = text;
        this.textScale = textScale;
    }

    public Banner(Sprite sprite, float spriteScale, String text, float textScale) {
        this(sprite, spriteScale, BitmapFonts.FONT_MONO, text, textScale);
    }

    /**
     * Sets the text offset inside the banner (pixels).
     * Positive X moves right, positive Y moves down.
     */
    public void setTextOffset(int x, int y) {
        this.textOffsetX = x;
        this.textOffsetY = y;
    }

    // ----- Centering methods (use these instead of manual x,y) -----

    /**
     * Centers the banner horizontally on the screen, with an optional offset.
     * Call this again if screen resolution changes (e.g., in onResolutionChanged).
     */
    public void centerHorizontal(int offsetX) {
        this.centeredHorizontally = true;
        this.centerOffsetX = offsetX;
        applyCentering();
    }

    /**
     * Centers the banner vertically on the screen, with an optional offset.
     */
    public void centerVertical(int offsetY) {
        this.centeredVertically = true;
        this.centerOffsetY = offsetY;
        applyCentering();
    }

    /**
     * Centers the banner both horizontally and vertically.
     */
    public void centerOnScreen(int offsetX, int offsetY) {
        centerHorizontal(offsetX);
        centerVertical(offsetY);
    }

    /**
     * Re‑applies the last centering settings (useful after resolution change).
     * Call this from your layer's onResolutionChanged.
     */
    public void recenter() {
        if (centeredHorizontally || centeredVertically) {
            applyCentering();
        }
    }

    private void applyCentering() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        if (centeredHorizontally) {
            x = (screenW - width) / 2 + centerOffsetX;
        }
        if (centeredVertically) {
            y = (screenH - height) / 2 + centerOffsetY;
        }
        background.setPosition(x, y);
    }

    // ----- Manual positioning (overrides centering) -----
    @Override
    public void setPosition(int x, int y) {
        // Disable centering when manually setting position
        centeredHorizontally = false;
        centeredVertically = false;
        super.setPosition(x, y);
        background.setPosition(x, y);
    }

    // ----- Text / appearance setters -----
    public void setText(String text) { this.text = text; }
    public void setTextScale(float scale) { this.textScale = scale; }
    public void setFont(BitmapFonts fontKey) { this.font = ResourceManager.getInstance().getFont(fontKey); }

    @Override
    public void draw(Graphics2D g) {
        background.draw(g);
        if (font != null && text != null) {
            int charW = font.getCharWidth();
            int charH = font.getCharHeight();
            int scaledCharW = (int)(charW * textScale);
            int scaledCharH = (int)(charH * textScale);
            int textWidth = text.length() * scaledCharW;
            int drawX = x + (width - textWidth) / 2 + textOffsetX;
            float verticalAdjustFactor = 0.15f;
            int drawY = y + (height - scaledCharH) / 2 + (int)(scaledCharH * verticalAdjustFactor) + textOffsetY;
            font.draw(g, text, drawX, drawY, textScale);
        }
    }

    @Override
    public void update() {
        // Nothing dynamic
    }

    public String getText() {
        return text;
    }
}