package software.project.ui.hud;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class HudElement {
    protected int x, y, width, height;
    protected boolean visible = true;

    public HudElement(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int w, int h) { this.width = w; this.height = h; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isVisible() { return visible; }

    public void update(float deltaTime) {}
    public void draw(Graphics2D g) {}
    public void onResolutionChanged(int newWidth, int newHeight) {}

    public boolean mousePressed(MouseEvent e) { return false; }
    public boolean mouseReleased(MouseEvent e) { return false; }
    public boolean mouseDragged(MouseEvent e) { return false; }
    public boolean mouseMoved(MouseEvent e) { return false; }
}