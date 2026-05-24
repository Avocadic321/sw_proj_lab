package software.project.ui.layers;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Layer {
    private final boolean blocksInput;
    private final boolean blocksUpdate;

    protected Layer() {
        this(false, false);
    }

    protected Layer(boolean blocksInput, boolean blocksUpdate) {
        this.blocksInput = blocksInput;
        this.blocksUpdate = blocksUpdate;
    }

    public final boolean blocksInput() {
        return blocksInput;
    }

    public final boolean blocksUpdate() {
        return blocksUpdate;
    }

    public void onEnter() {
    }

    public void onExit() {
    }

    public void update(float deltaTime) {
    }

    public void render(Graphics2D g) {
    }

    /**
     * Called when the window is resized (virtual resolution changed).
     */
    public void onResolutionChanged(int newWidth, int newHeight) {
    }

    public boolean keyPressed(KeyEvent e) {
        return false;
    }

    public boolean keyReleased(KeyEvent e) {
        return false;
    }

    public boolean mousePressed(MouseEvent e) {
        return false;
    }

    public boolean mouseReleased(MouseEvent e) {
        return false;
    }

    public boolean mouseMoved(MouseEvent e) {
        return false;
    }

    public boolean mouseDragged(MouseEvent e) {
        return false;
    }
}
