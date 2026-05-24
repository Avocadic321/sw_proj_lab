package software.project.ui.components;

import software.project.audio.AudioPlayer;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

public class MenuButton extends Component {
    public static final int BASE_WIDTH = 140;
    public static final int BASE_HEIGHT = 56;
    private static final boolean DEBUG = false;
    private static float globalScale = 1.0f;
    private Sprite currentSprite;
    private Sprite normal;
    private Sprite hover;
    private Sprite pressed;
    private boolean mouseOver;
    private boolean mousePressed;
    private Runnable action;

    // x, y are centre position
    public MenuButton(int rowIndex, int x, int y) {
        super(x - getScaledWidth() / 2, y, getScaledWidth(), getScaledHeight());

        SpriteSheet sheet = SpriteManager.getInstance().getSpriteSheet(SpriteSheets.BUTTONS);
        if (sheet == null) {
            return;
        }

        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
        this.currentSprite = normal;
    }

    public static void setGlobalScale(float scale) {
        globalScale = scale;
    }

    public static int getScaledWidth() {
        return (int) (BASE_WIDTH * globalScale);
    }

    public static int getScaledHeight() {
        return (int) (BASE_HEIGHT * globalScale);
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void update() {
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
        if (DEBUG) {
            drawBounds(g);
        }
    }

    private void drawBounds(Graphics2D g) {
        g.setColor(Color.RED);
        Stroke original = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, width, height);
        g.setStroke(original);
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

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Methods to get centre position for external use
    public int getCentreX() {
        return x + width / 2;
    }

    public int getCentreY() {
        return y + height / 2;
    }
}