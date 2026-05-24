package software.project.ui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import software.project.ui.components.MenuButton;
import software.project.ui.components.Panel;

public class PauseOverlay extends Layer {
    private static final int BUTTON_COUNT = 3;
    private static final int[] BUTTON_ROW_INDICES = { 0, 1, 2 };

    private static final float MENU_SCALE_FACTOR = 0.6f;
    private static final int MENU_VERTICAL_OFFSET = 30;
    private static final double TOP_MARGIN_PERCENT = 0.25;
    private static final double BOTTOM_MARGIN_PERCENT = 0.10;
    private static final double INNER_PADDING_PERCENT = 0.05;
    private static final double EFFECTIVE_TOP = TOP_MARGIN_PERCENT + INNER_PADDING_PERCENT;
    private static final double EFFECTIVE_BOTTOM = BOTTOM_MARGIN_PERCENT + INNER_PADDING_PERCENT;

    private final List<MenuButton> buttons = new ArrayList<>();

    // ✅ Initialize panel immediately at the field declaration
    private Panel panel = new Panel(MENU_SCALE_FACTOR, MENU_VERTICAL_OFFSET);

    private Runnable resumeAction = () -> {};
    private Runnable optionsAction = () -> {};
    private Runnable quitAction = () -> {};

    public PauseOverlay() {
        super(true, true);
        createButtons();
    }

    public void setResumeAction(Runnable resumeAction) {
        if (resumeAction != null) this.resumeAction = resumeAction;
    }

    public void setOptionsAction(Runnable optionsAction) {
        if (optionsAction != null) this.optionsAction = optionsAction;
    }

    public void setQuitAction(Runnable quitAction) {
        if (quitAction != null) this.quitAction = quitAction;
    }

    private void createButtons() {
        buttons.clear();
        panel.recomputeLayout();

        int[] yPositions = computeButtonPositions();
        int centerX = panel.getCenterX();

        for (int i = 0; i < BUTTON_COUNT; i++) {
            MenuButton button = new MenuButton(BUTTON_ROW_INDICES[i], centerX, yPositions[i]);
            button.setAction(getActionForIndex(i));
            buttons.add(button);
        }
    }

    private int[] computeButtonPositions() {
        int scaledButtonHeight = MenuButton.getScaledHeight();
        int usableHeight = (int) (panel.getHeight() * (1.0 - EFFECTIVE_TOP - EFFECTIVE_BOTTOM));
        int totalButtonsHeight = BUTTON_COUNT * scaledButtonHeight;
        int gapBetweenButtons;

        if (totalButtonsHeight > usableHeight) {
            gapBetweenButtons = 0;
        } else {
            int remaining = usableHeight - totalButtonsHeight;
            gapBetweenButtons = remaining / (BUTTON_COUNT - 1);
        }

        int startY = panel.getY() + (int) (panel.getHeight() * EFFECTIVE_TOP);
        int[] yPositions = new int[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; ++i) {
            yPositions[i] = startY + i * (scaledButtonHeight + gapBetweenButtons);
        }
        return yPositions;
    }

    private Runnable getActionForIndex(int index) {
        return switch (index) {
            case 0 -> resumeAction;
            case 1 -> optionsAction;
            case 2 -> quitAction;
            default -> () -> {};
        };
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        panel.recomputeLayout();
        createButtons();
    }

    @Override
    public void update(float deltaTime) {
        for (MenuButton button : buttons) {
            button.update();
        }
    }

    @Override
    public void render(Graphics2D g) {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, virtualW, virtualH);

        panel.draw(g);

        for (MenuButton button : buttons) {
            button.draw(g);
        }
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mouseMoved(e);
        }
        return true;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mousePressed(e);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mouseReleased(e);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
            resumeAction.run();
            return true;
        }
        return false;
    }
}