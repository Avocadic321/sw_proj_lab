package software.project.ui.layers;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Layer {
    public void onEnter() {}
    public void onExit() {}
    public void update(float deltaTime) {}
    public void render(Graphics2D g) {}

    // --- Input handling (only top layer receives these) ---
    // Return true if the event is consumed (no further processing).

    public boolean keyPressed(KeyEvent e) { return false; }
    public boolean keyReleased(KeyEvent e) { return false; }
    public boolean mousePressed(MouseEvent e) { return false; }
    public boolean mouseReleased(MouseEvent e) { return false; }
    public boolean mouseMoved(MouseEvent e) { return false; }
    public boolean mouseDragged(MouseEvent e) { return false; }
}
