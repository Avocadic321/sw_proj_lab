package software.project.ui.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteSheet;

public class GameButton extends Component {

    private Sprite currentSprite;

    private Sprite normal;
    private Sprite hover;
    private Sprite pressed;

    private boolean mouseOver;
    private boolean mousePressed;

    private Runnable action;

    public GameButton(SpriteSheet sheet, int rowIndex, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.normal = sheet.getSprite(0, rowIndex);
        this.hover = sheet.getSprite(1, rowIndex);
        this.pressed = sheet.getSprite(2, rowIndex);
        this.currentSprite = normal;
    }

    public GameButton(Sprite normal, Sprite hover, Sprite pressed, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.normal = normal;
        this.hover = hover;
        this.pressed = pressed;
        this.currentSprite = normal;
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

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}