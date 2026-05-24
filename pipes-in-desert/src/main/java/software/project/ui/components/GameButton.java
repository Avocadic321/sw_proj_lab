package software.project.ui.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteSheet;

public class GameButton {
    private int x, y, width, height;
    private Sprite normal, hover, pressed;
    private Sprite currentSprite;
    private boolean mouseOver, mousePressed;
    private Runnable action;

    /**
     * Creates a generic button from a SpriteSheet row.
     *
     * @param sheet    The SpriteSheet containing button states
     * @param rowIndex The row index (0-based) where the button sprites are located
     * @param x        The x position (top-left)
     * @param y        The y position (top-left)
     * @param width    The target width to draw the button
     * @param height   The target height to draw the button
     */
    public GameButton(SpriteSheet sheet, int rowIndex, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Columns: 0 = normal, 1 = hover, 2 = pressed
        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
        this.currentSprite = normal;
    }

    /**
     * Creates a generic button directly from three sprites.
     */
    public GameButton(Sprite normal, Sprite hover, Sprite pressed, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.normal = normal;
        this.hover = hover;
        this.pressed = pressed;
        this.currentSprite = normal;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void update() {
        if (mousePressed) {
            currentSprite = pressed;
        } else if (mouseOver) {
            currentSprite = hover;
        } else {
            currentSprite = normal;
        }
    }

    public void draw(Graphics2D g) {
        if (currentSprite != null) {
            currentSprite.draw(g, x, y, width, height);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (getBounds().contains(e.getX(), e.getY())) {
            mousePressed = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (mousePressed && getBounds().contains(e.getX(), e.getY()) && action != null) {
            action.run();
        }
        mousePressed = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}