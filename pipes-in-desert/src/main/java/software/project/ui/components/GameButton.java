package software.project.ui.components;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteSheet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class GameButton extends Component {

    private final Sprite normal;
    private final Sprite hover;
    private final Sprite pressed;

    private Sprite currentSprite;

    private boolean mouseOver;
    private boolean mousePressed;

    private Runnable action;
    private boolean enabled = true;

    public GameButton(SpriteSheet sheet, int rowIndex, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
        this.currentSprite = normal;
    }

    public GameButton(
        Sprite normal,
        Sprite hover,
        Sprite pressed,
        int x,
        int y,
        int width,
        int height
    ) {
        super(x, y, width, height);
        this.normal = normal;
        this.hover = hover;
        this.pressed = pressed;
        this.currentSprite = normal;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            // Reset interaction states when disabled
            mouseOver = false;
            mousePressed = false;
            update(); // force sprite back to normal
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        if (!enabled) {
            currentSprite = normal;
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
        if (currentSprite != null) {
            currentSprite.draw(g, x, y, width, height);
        }

        // Disabled tint: draw a semi‑transparent gray rectangle over the button
        if (!enabled) {
            Color originalColor = g.getColor();
            g.setColor(new Color(100, 100, 100, 180)); // gray tint, ~70% opacity
            g.fillRect(x, y, width, height);
            g.setColor(originalColor);
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (!enabled) return;
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (!enabled) return;
        if (getBounds().contains(e.getX(), e.getY())) {
            mousePressed = true;
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