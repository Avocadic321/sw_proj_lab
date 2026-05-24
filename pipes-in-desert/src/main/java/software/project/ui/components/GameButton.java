package software.project.ui.components;

import software.project.audio.AudioPlayer;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteSheet;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameButton extends Component {

    private final Sprite normal;
    private final Sprite hover;
    private final Sprite pressed;

    private Sprite currentSprite;

    private boolean mouseOver;
    private boolean mousePressed;
    private boolean enabled = true;

    private Runnable action;

    public GameButton(SpriteSheet sheet, int rowIndex, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
        this.currentSprite = normal;
    }

    public void setCenter(int cx, int cy) {
        this.x = cx - width / 2;
        this.y = cy - height / 2;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            mouseOver = false;
            mousePressed = false;
            update();
        } else {
            update();
        }
    }

    @Override
    public void update() {
        if (!enabled) {
            currentSprite = normal; // will be tinted in draw
            return;
        }
        if (mousePressed) {
            currentSprite = pressed;
        } else if (mouseOver) {
            currentSprite = hover;
        } else {
            currentSprite = normal;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (currentSprite == null) {
            return;
        }

        if (!enabled) {
            drawDisabled(g);
        } else {
            currentSprite.draw(g, x, y, width, height);
        }
    }

    /**
     * Draws the button in a disabled state – applies a gray tint only to non‑transparent pixels.
     */
    private void drawDisabled(Graphics2D g) {
        BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG = temp.createGraphics();
        currentSprite.draw(tempG, 0, 0, width, height);
        tempG.setComposite(AlphaComposite.SrcAtop);
        tempG.setColor(new Color(100, 100, 100, 180));
        tempG.fillRect(0, 0, width, height);
        tempG.dispose();
        g.drawImage(temp, x, y, null);
    }

    public void mouseMoved(MouseEvent e) {
        if (!enabled) {
            return;
        }
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (!enabled) {
            return;
        }
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
        return new Rectangle(x, y, width, height);
    }
}