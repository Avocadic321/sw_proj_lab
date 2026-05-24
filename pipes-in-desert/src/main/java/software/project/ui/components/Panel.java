package software.project.ui.components;

import java.awt.Color;
import java.awt.Graphics2D;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;

public class Panel extends Component {

    private Sprite sprite;

    private int originalWidth;
    private int originalHeight;

    private float scaleFactor;

    private int verticalOffset;

    private Color fallbackColor;
    private boolean roundRect;

    public Panel(float scaleFactor, int verticalOffset) {
        this(scaleFactor, verticalOffset, Sprites.MENU_PANEL);
    }

    public Panel(float scaleFactor, int verticalOffset, Sprites spriteKey) {
        super(0, 0, 0, 0);
        this.scaleFactor = scaleFactor;
        this.verticalOffset = verticalOffset;
        this.sprite = SpriteManager.getInstance().getSprite(spriteKey);
        this.fallbackColor = new Color(40, 50, 70);
        this.roundRect = true;

        if (this.sprite != null) {
            this.originalWidth = this.sprite.getWidth();
            this.originalHeight = this.sprite.getHeight();
        } else {
            this.originalWidth = 282;
            this.originalHeight = 406;
        }

        recomputeLayout();
    }

    public void recomputeLayout() {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        double fitScaleX = (double) virtualW / originalWidth;
        double fitScaleY = (double) virtualH / originalHeight;
        double fitScale = Math.min(fitScaleX, fitScaleY);
        double finalScale = fitScale * scaleFactor;

        width = (int) (originalWidth * finalScale);
        height = (int) (originalHeight * finalScale);
        x = (virtualW - width) / 2;
        y = (virtualH - height) / 2 + verticalOffset;
    }

    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            sprite.draw(g, x, y, width, height);
        } else {
            g.setColor(fallbackColor);
            if (roundRect) {
                g.fillRoundRect(x, y, width, height, 20, 20);
            } else {
                g.fillRect(x, y, width, height);
            }
        }
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }
}