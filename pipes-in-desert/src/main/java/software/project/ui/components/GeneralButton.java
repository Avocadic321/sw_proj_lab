package software.project.ui.components;

import software.project.audio.AudioPlayer;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteSheet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GeneralButton extends Component {
    // Base measurements from original sprite (at 1x scale)
    private static final int BASE_LEFT_CAP_WIDTH = 5;
    private static final int BASE_RIGHT_CAP_WIDTH = 5;
    private static final int BASE_TOP_MARGIN = 7;
    private static final int BASE_BOTTOM_MARGIN = 11;
    private static final int PRESS_OFFSET = 2;

    private final Sprite normalSprite;
    private final Sprite hoverSprite;
    private final Sprite pressedSprite;
    private final float buttonScale;

    private final TextComponent label;
    private Runnable action;
    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private boolean enabled = true;

    private final int leftCapWidth;
    private final int rightCapWidth;
    private final int topMargin;
    private final int bottomMargin;
    private final int baseHeight;

    public GeneralButton(SpriteSheet sheet, int rowIndex, int x, int y, String text, float textScale, float buttonScale) {
        super(x, y, 0, 0);
        this.buttonScale = buttonScale;

        this.normalSprite = sheet.getSprite(0, rowIndex);
        this.hoverSprite = sheet.getSprite(1, rowIndex);
        this.pressedSprite = sheet.getSprite(2, rowIndex);

        this.leftCapWidth = (int)(BASE_LEFT_CAP_WIDTH * buttonScale);
        this.rightCapWidth = (int)(BASE_RIGHT_CAP_WIDTH * buttonScale);
        this.topMargin = (int)(BASE_TOP_MARGIN * buttonScale);
        this.bottomMargin = (int)(BASE_BOTTOM_MARGIN * buttonScale);
        this.baseHeight = normalSprite != null ? (int)(normalSprite.getHeight() * buttonScale) : (int)(40 * buttonScale);

        // Create text component
        this.label = new TextComponent(0, 0, text, textScale);
        this.label.setColor(Color.WHITE);

        // Calculate button size based on text
        int textWidth = label.getTextWidth();
        int buttonWidth = leftCapWidth + textWidth + rightCapWidth;

        // Set button size and position
        setSize(buttonWidth, baseHeight);
        setPosition(x, y);

        // Position text inside button
        repositionLabel();
    }

    public GeneralButton(SpriteSheet sheet, int rowIndex, int x, int y, String text, float textScale) {
        this(sheet, rowIndex, x, y, text, textScale, 1.0f);
    }

    private void repositionLabel() {
        // Calculate the exact center of the button's usable area
        int usableWidth = getWidth() - leftCapWidth - rightCapWidth;
        int usableHeight = getHeight() - topMargin - bottomMargin;

        // Center text horizontally and vertically within usable area
        int labelX = getX() + leftCapWidth + (usableWidth - label.getTextWidth()) / 2;
        int labelY = getY() + topMargin + (usableHeight - label.getTextHeight()) / 2;

        label.setPosition(labelX, labelY);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        repositionLabel();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        repositionLabel();
    }

    public void setText(String text) {
        label.setText(text);
        int buttonWidth = leftCapWidth + label.getTextWidth() + rightCapWidth;
        setSize(buttonWidth, getHeight());
        // repositionLabel is called inside setSize
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            mouseOver = false;
            mousePressed = false;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!enabled) {
            drawDisabled(g);
            return;
        }

        Sprite currentSprite = normalSprite;

        if (mousePressed) {
            currentSprite = pressedSprite;
            label.setOffset(0, PRESS_OFFSET);
            label.setColor(Color.decode("#e4b199"));
        } else if (mouseOver) {
            currentSprite = hoverSprite;
            label.setOffset(0, 0);
            label.setColor(Color.decode("#e4b199"));
        } else {
            currentSprite = normalSprite;
            label.setOffset(0, 0);
            label.setColor(Color.WHITE);
        }

        drawStretchedButton(g, currentSprite, getX(), getY(), getWidth(), getHeight());
        label.draw(g);
    }

    private void drawStretchedButton(Graphics2D g, Sprite sprite, int x, int y, int width, int height) {
        if (sprite == null) return;

        BufferedImage img = sprite.getImage();
        int srcWidth = img.getWidth();
        int srcHeight = img.getHeight();

        int leftCap = BASE_LEFT_CAP_WIDTH;
        int rightCap = BASE_RIGHT_CAP_WIDTH;
        int middleWidth = srcWidth - leftCap - rightCap;
        if (middleWidth < 1) middleWidth = 1;

        BufferedImage leftPart = img.getSubimage(0, 0, leftCap, srcHeight);
        BufferedImage rightPart = img.getSubimage(srcWidth - rightCap, 0, rightCap, srcHeight);
        BufferedImage middlePart = img.getSubimage(leftCap, 0, middleWidth, srcHeight);

        int scaledLeftWidth = (int)(leftCap * buttonScale);
        int scaledRightWidth = (int)(rightCap * buttonScale);

        g.drawImage(leftPart, x, y, scaledLeftWidth, height, null);

        int middleAreaWidth = width - scaledLeftWidth - scaledRightWidth;
        if (middleAreaWidth > 0) {
            g.drawImage(middlePart, x + scaledLeftWidth, y, middleAreaWidth, height, null);
        }

        g.drawImage(rightPart, x + width - scaledRightWidth, y, scaledRightWidth, height, null);
    }

    private void drawDisabled(Graphics2D g) {
        drawStretchedButton(g, normalSprite, getX(), getY(), getWidth(), getHeight());
        g.setColor(new Color(100, 100, 100, 180));
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        label.setColor(Color.GRAY);
        label.draw(g);
    }

    public void mouseMoved(MouseEvent e) {
        if (!enabled) return;
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (!enabled) return;
        if (getBounds().contains(e.getX(), e.getY())) {
            mousePressed = true;
            AudioPlayer.getInstance().playEffect("button_pressed");
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!enabled) {
            mousePressed = false;
            return;
        }
        if (mousePressed && getBounds().contains(e.getX(), e.getY()) && action != null) {
            action.run();
        }
        mousePressed = false;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}