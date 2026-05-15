package software.project.ui.layers;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.components.MenuButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainMenuLayer extends Layer {

    private static final int BUTTON_COUNT = 4;

    // Fallback dimensions (used only if menu panel sprite is missing)
    private static final int FALLBACK_MENU_WIDTH = 282;
    private static final int FALLBACK_MENU_HEIGHT = 406;

    // Layout percentages (relative to menu panel height)
    private static final double TOP_MARGIN_PERCENT = 0.23;
    private static final double BOTTOM_MARGIN_PERCENT = 0.07;
    private static final double INNER_PADDING_PERCENT = 0.02;
    private static final double EFFECTIVE_TOP = TOP_MARGIN_PERCENT + INNER_PADDING_PERCENT;
    private static final double EFFECTIVE_BOTTOM = BOTTOM_MARGIN_PERCENT + INNER_PADDING_PERCENT;

    // Global menu scale factor (1.0 = fit exactly; lower = smaller)
    private static final float MENU_SCALE_FACTOR = 0.72f;

    // Sprite sheet rows: 0=PLAY, 1=OPTIONS, 2=QUIT, 3=CREDITS
    // Desired order: PLAY, OPTIONS, CREDITS, QUIT
    private static final int[] BUTTON_ROW_INDICES = {0, 1, 3, 2};

    private final GameApplication app;
    private final List<MenuButton> buttons = new ArrayList<>();

    private Sprite menuPanelSprite;
    private int originalPanelWidth, originalPanelHeight;
    private int panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight;

    public MainMenuLayer(GameApplication app) {
        this.app = app;
        loadBackground();
        createButtons();
    }

    private void loadBackground() {
        menuPanelSprite = SpriteManager.getInstance().getSprite("menu_background");
        if (menuPanelSprite == null) {
            System.err.println("[WARN] Menu panel sprite missing. Using fallback.");
            originalPanelWidth = FALLBACK_MENU_WIDTH;
            originalPanelHeight = FALLBACK_MENU_HEIGHT;
        } else {
            originalPanelWidth = menuPanelSprite.getWidth();
            originalPanelHeight = menuPanelSprite.getHeight();
        }
        recomputeLayout(); // initial layout using current screen dimensions
    }

    /** Recalculates panel size and position, and updates button global scale. */
    private void recomputeLayout() {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        // Scale to fit the panel inside virtual screen (preserving aspect ratio)
        double fitScaleX = (double) virtualW / originalPanelWidth;
        double fitScaleY = (double) virtualH / originalPanelHeight;
        double fitScale = Math.min(fitScaleX, fitScaleY);
        double finalScale = fitScale * MENU_SCALE_FACTOR;

        panelDrawWidth = (int) (originalPanelWidth * finalScale);
        panelDrawHeight = (int) (originalPanelHeight * finalScale);
        panelDrawX = (virtualW - panelDrawWidth) / 2;
        panelDrawY = (virtualH - panelDrawHeight) / 2;

        // Apply the same scale to all buttons (global static scale)
        MenuButton.setGlobalScale((float) finalScale);
    }

    private void createButtons() {
        buttons.clear();
        recomputeLayout(); // ensure layout is up‑to‑date

        int[] yPositions = computeButtonPositions();
        int centerX = panelDrawX + panelDrawWidth / 2;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            MenuButton btn = new MenuButton(BUTTON_ROW_INDICES[i], centerX, yPositions[i]);
            btn.setAction(getActionForIndex(i));
            buttons.add(btn);
        }
    }

    /** Calculates Y positions for all buttons inside the scaled panel. */
    private int[] computeButtonPositions() {
        int scaledButtonHeight = MenuButton.getScaledHeight();
        int usableHeight = (int) (panelDrawHeight * (1.0 - EFFECTIVE_TOP - EFFECTIVE_BOTTOM));
        int totalButtonsHeight = BUTTON_COUNT * scaledButtonHeight;
        int gapBetweenButtons;

        if (totalButtonsHeight > usableHeight) {
            gapBetweenButtons = 0; // not enough space – no gaps
        } else {
            int remaining = usableHeight - totalButtonsHeight;
            gapBetweenButtons = remaining / (BUTTON_COUNT - 1);
        }

        int startY = panelDrawY + (int) (panelDrawHeight * EFFECTIVE_TOP);
        int[] yPositions = new int[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; i++) {
            yPositions[i] = startY + i * (scaledButtonHeight + gapBetweenButtons);
        }
        return yPositions;
    }

    /** Returns the action for each button based on its position in menu order. */
    private Runnable getActionForIndex(int index) {
        return switch (index) {
            case 0 -> () -> {
                System.out.println("PLAY clicked – replace with PlayingLayer");
                // app.replaceLayer(new PlayingLayer(app));
            };
            case 1 -> () -> System.out.println("OPTIONS clicked");
            case 2 -> () -> System.out.println("CREDITS clicked");
            case 3 -> () -> System.exit(0);
            default -> () -> {};
        };
    }

    // ------------------------------------------------------------------------
    // Layer overrides
    // ------------------------------------------------------------------------
    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        // When the screen size changes (window resize or fullscreen toggle),
        // rebuild the buttons so they reposition correctly.
        createButtons();
    }

    @Override
    public void update(float deltaTime) {
        for (MenuButton btn : buttons) btn.update();
    }

    @Override
    public void render(Graphics2D g) {
        // Full‑screen background (dark blue)
        g.setColor(new Color(20, 30, 50));
        g.fillRect(0, 0,
            ScreenManager.getInstance().getVirtualWidth(),
            ScreenManager.getInstance().getVirtualHeight());

        // Draw the menu panel (scaled and centred)
        if (menuPanelSprite != null) {
            menuPanelSprite.draw(g, panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight);
        } else {
            // Fallback rounded rectangle
            g.setColor(new Color(40, 50, 70));
            g.fillRoundRect(panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight, 20, 20);
        }

        // Draw all buttons (they already use the global scale internally)
        for (MenuButton btn : buttons) btn.draw(g);
    }

    // ------------------------------------------------------------------------
    // Input handling – mouse events are already transformed to virtual coordinates
    // by GamePanel, so we forward them directly to each button.
    // ------------------------------------------------------------------------
    @Override
    public boolean mouseMoved(MouseEvent e) {
        for (MenuButton btn : buttons) btn.mouseMoved(e);
        return true;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        for (MenuButton btn : buttons) btn.mousePressed(e);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        for (MenuButton btn : buttons) btn.mouseReleased(e);
        return true;
    }
}