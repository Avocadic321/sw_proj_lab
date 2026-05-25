package software.project.ui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import software.project.ui.ScreenManager;
import software.project.ui.components.Menu;

/**
 * Pause screen overlay with resume, options, and quit actions.
 */
public class PauseOverlay extends Layer {
    private static final int[] BUTTON_ROW_INDICES = { 0, 1, 2 };

    private static final float MENU_SCALE_FACTOR = 0.6f;
    private static final int MENU_VERTICAL_OFFSET = 30;
    private static final double TOP_MARGIN_PERCENT = 0.25;
    private static final double BOTTOM_MARGIN_PERCENT = 0.10;
    private static final double INNER_PADDING_PERCENT = 0.05;

    private final Menu menu;

    private Runnable resumeAction = () -> {
    };
    private Runnable optionsAction = () -> {
    };
    private Runnable quitAction = () -> {
    };

    /**
     * Creates a pause overlay with a menu configured for the default actions.
     */
    public PauseOverlay() {
        super(true, true);
        // Create the menu
        menu = new Menu(MENU_SCALE_FACTOR, MENU_VERTICAL_OFFSET, BUTTON_ROW_INDICES, TOP_MARGIN_PERCENT,
                BOTTOM_MARGIN_PERCENT, INNER_PADDING_PERCENT);
        menu.setAction(0, resumeAction);
        menu.setAction(1, optionsAction);
        menu.setAction(2, quitAction);
    }

    /**
     * Sets the resume action handler.
     */
    public void setResumeAction(Runnable resumeAction) {
        this.resumeAction = resumeAction;
        menu.setAction(0, resumeAction);
    }

    /**
     * Sets the options action handler.
     */
    public void setOptionsAction(Runnable optionsAction) {
        this.optionsAction = optionsAction;
        menu.setAction(1, optionsAction);
    }

    /**
     * Sets the quit action handler.
     */
    public void setQuitAction(Runnable quitAction) {
        this.quitAction = quitAction;
        menu.setAction(2, quitAction);
    }

    /**
     * Updates the menu layout when the resolution changes.
     */
    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        menu.onResolutionChanged();
    }

    /**
     * Updates menu animations and hover state.
     */
    @Override
    public void update(float deltaTime) {
        menu.update();
    }

    /**
     * Renders the dimmed backdrop and the pause menu.
     */
    @Override
    public void render(Graphics2D g) {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, virtualW, virtualH);

        menu.render(g);
    }

    /**
     * Routes mouse movement to the menu.
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {
        menu.handleMouseMoved(e);
        return true;
    }

    /**
     * Routes mouse press events to the menu.
     */
    @Override
    public boolean mousePressed(MouseEvent e) {
        menu.handleMousePressed(e);
        return true;
    }

    /**
     * Routes mouse release events to the menu.
     */
    @Override
    public boolean mouseReleased(MouseEvent e) {
        menu.handleMouseReleased(e);
        return true;
    }

    /**
     * Resumes the game when ESC or P is pressed.
     */
    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
            resumeAction.run();
            return true;
        }
        return false;
    }
}