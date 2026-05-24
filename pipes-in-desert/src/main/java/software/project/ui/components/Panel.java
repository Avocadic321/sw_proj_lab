package software.project.ui.components;

import java.awt.Color;
import java.awt.Graphics2D;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.Sprites;

public class Panel {
    private int x, y, width, height;
    private Sprite sprite;
    private Color fallbackColor;
    private boolean roundRect;

    public Panel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = SpriteManager.getInstance().getSprite(Sprites.MENU_PANEL);
        this.fallbackColor = new Color(40, 50, 70);
        this.roundRect = true;
    }

    public Panel(int x, int y, int width, int height, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.fallbackColor = new Color(40, 50, 70);
        this.roundRect = true;
    }

    public Panel(int x, int y, int width, int height, Sprite sprite, Color fallbackColor, boolean roundRect) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.fallbackColor = fallbackColor;
        this.roundRect = roundRect;
    }

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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}