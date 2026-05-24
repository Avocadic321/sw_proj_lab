package software.project.ui.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Component {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void draw(Graphics2D g);

    // Update is optional – components that don't need it can ignore it
    public void update() {
    }
}