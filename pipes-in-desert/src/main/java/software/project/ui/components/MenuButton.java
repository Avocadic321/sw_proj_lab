package software.project.ui.components;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class MenuButton {
    public static final int BUTTON_WIDTH = 140;
    public static final int BUTTON_HEIGHT = 56;

    private int x, y;
    private Sprite normal, hover, pressed;
    private Sprite currentSprite;
    private boolean mouseOver, mousePressed;

    private Runnable action;

    public MenuButton(int rowIndex, int x, int y) {
        this.x = x;
        this.y = y;

        SpriteSheet sheet = SpriteManager.getInstance().getSpriteSheet("buttons");
        if (sheet == null) {
            System.err.println("[ERROR] Button sheet not loaded");
            return;
        }

        this.normal = sheet.getSprite(0, rowIndex);   // col 0, row = button index
        this.hover  = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
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
            currentSprite.draw(g, x - BUTTON_WIDTH/2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (getBounds().contains(e.getX(), e.getY())) mousePressed = true;
    }

    public void mouseReleased(MouseEvent e) {
        if (mousePressed && getBounds().contains(e.getX(), e.getY()) && action != null) {
            action.run();
        }
        mousePressed = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - BUTTON_WIDTH/2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
}