package software.project.ui.layers;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Layer {
    public void onEnter() {}
    public void onExit() {}
    public void update(float deltaTime) {}
    public void render(Graphics2D g) {}

    /** Called when the window is resized (virtual resolution changed). */
    public void onResolutionChanged(int newWidth, int newHeight) {}

    // --- Input handling (only top layer receives these) ---
    public boolean keyPressed(KeyEvent e) { return false; }
    public boolean keyReleased(KeyEvent e) { return false; }
    public boolean mousePressed(MouseEvent e) { return false; }
    public boolean mouseReleased(MouseEvent e) { return false; }
    public boolean mouseMoved(MouseEvent e) { return false; }
    public boolean mouseDragged(MouseEvent e) { return false; }
}