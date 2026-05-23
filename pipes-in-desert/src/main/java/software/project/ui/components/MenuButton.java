package software.project.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import software.project.audio.AudioPlayer;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;

public class MenuButton {
    // Base size (original art size)
    public static final int BASE_WIDTH = 140;
    public static final int BASE_HEIGHT = 56;

    // Global scale factor for all buttons (set once, e.g., from MainMenuLayer)
    private static float globalScale = 1.0f;
    private static final boolean DEBUG = false;

    public static void setGlobalScale(float scale) {
        globalScale = scale;
    }

    public static int getScaledWidth() {
        return (int) (BASE_WIDTH * globalScale);
    }

    public static int getScaledHeight() {
        return (int) (BASE_HEIGHT * globalScale);
    }

    private int x;
    private int y; // centre position (in virtual coordinates)
    private Sprite normal;
    private Sprite hover;
    private Sprite pressed;
    private Sprite currentSprite;
    private boolean mouseOver;
    private boolean mousePressed;
    private Runnable action;

    public MenuButton(int rowIndex, int x, int y) {
        this.x = x;
        this.y = y;

        SpriteSheet sheet = SpriteManager.getInstance().getSpriteSheet(SpriteSheets.BUTTONS);
        if (sheet == null) {
            return;
        }

        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
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
            int w = getScaledWidth();
            int h = getScaledHeight();
            currentSprite.draw(g, x - w / 2, y, w, h);
        }
        if (DEBUG) {
            drawBounds(g);
        }
    }

    private void drawBounds(Graphics2D g) {
        final float STROKE_WIDTH = 2.0f;
        final Color STROKE_COLOR = Color.RED;

        Rectangle bounds = getBounds();
        g.setColor(STROKE_COLOR);
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(STROKE_WIDTH));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setStroke(originalStroke);
    }

    public void mouseMoved(MouseEvent e) {
        mouseOver = getBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (getBounds().contains(e.getX(), e.getY())) {
            mousePressed = true;
            AudioPlayer.getInstance().playEffect("button_pressed");
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (mousePressed && getBounds().contains(e.getX(), e.getY()) && action != null) {
            action.run();
        }
        mousePressed = false;
    }

    public Rectangle getBounds() {
        int w = getScaledWidth();
        int h = getScaledHeight();
        return new Rectangle(x - w / 2, y, w, h);
    }
}
