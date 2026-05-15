package software.project.ui.layers;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.ui.GameApplication;
import software.project.ui.components.MenuButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static software.project.ui.components.MenuButton.BUTTON_HEIGHT;

public class MainMenuLayer extends Layer {

    private static final int BUTTON_COUNT = 4;

    // Fallback dimensions (used only if menu background image is missing)
    private static final int FALLBACK_MENU_WIDTH = 282;
    private static final int FALLBACK_MENU_HEIGHT = 406;

    // Layout percentages (relative to menu panel height)
    private static final double TOP_MARGIN_PERCENT = 0.23;      // 23% from top edge
    private static final double BOTTOM_MARGIN_PERCENT = 0.07;   // 7% from bottom edge
    private static final double INNER_PADDING_PERCENT = 0.02;   // extra 2% inside (top & bottom)

    // Effective margins (top margin + inner padding, bottom margin + inner padding)
    private static final double EFFECTIVE_TOP = TOP_MARGIN_PERCENT + INNER_PADDING_PERCENT;
    private static final double EFFECTIVE_BOTTOM = BOTTOM_MARGIN_PERCENT + INNER_PADDING_PERCENT;

    // Sprite sheet row indices for each menu item (matching button atlas)
    // Atlas rows: 0=PLAY, 1=OPTIONS, 2=QUIT, 3=CREDITS
    // Desired menu order: PLAY, OPTIONS, CREDITS, QUIT
    private static final int[] BUTTON_ROW_INDICES = {0, 1, 3, 2};

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private final GameApplication app;
    private final List<MenuButton> buttons = new ArrayList<>();

    private Sprite menuPanelSprite;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public MainMenuLayer(GameApplication app) {
        this.app = app;
        loadBackground();
        createButtons();
    }

    private void loadBackground() {
        menuPanelSprite = SpriteManager.getInstance().getSprite("menu_background");
        if (menuPanelSprite == null) {
            System.err.println("[ERROR] Background sprite 'menu_background' not found. Using fallback.");
            panelWidth = FALLBACK_MENU_WIDTH;
            panelHeight = FALLBACK_MENU_HEIGHT;
        } else {
            panelWidth = menuPanelSprite.getWidth();
            panelHeight = menuPanelSprite.getHeight();
        }

        // Centre the panel (original size, no scaling)
        panelX = (GameApplication.WIDTH - panelWidth) / 2;
        panelY = (GameApplication.HEIGHT - panelHeight) / 2;
    }

    private void createButtons() {
        // Compute vertical positions inside the panel
        int[] yPositions = computeButtonPositions();

        // Horizontal centre = panel centre
        int centerX = panelX + panelWidth / 2;

        // Create and store buttons
        for (int i = 0; i < BUTTON_COUNT; i++) {
            MenuButton btn = new MenuButton(BUTTON_ROW_INDICES[i], centerX, yPositions[i]);
            btn.setAction(getActionForIndex(i));
            buttons.add(btn);
        }
    }

    /**
     * Calculates the Y coordinates for all buttons based on margins, padding,
     * button height, and auto‑computed gaps.
     *
     * @return array of Y positions (relative to screen, not panel)
     */
    private int[] computeButtonPositions() {
        int usableHeight = (int) (panelHeight * (1.0 - EFFECTIVE_TOP - EFFECTIVE_BOTTOM));
        int totalButtonsHeight = BUTTON_COUNT * BUTTON_HEIGHT;
        int gapBetweenButtons;

        if (totalButtonsHeight > usableHeight) {
            gapBetweenButtons = 0; // not enough space – no gaps
        } else {
            int remaining = usableHeight - totalButtonsHeight;
            gapBetweenButtons = remaining / (BUTTON_COUNT - 1); // 3 gaps for 4 buttons
        }

        int startY = panelY + (int) (panelHeight * EFFECTIVE_TOP);
        int[] yPositions = new int[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; i++) {
            yPositions[i] = startY + i * (BUTTON_HEIGHT + gapBetweenButtons);
        }
        return yPositions;
    }

    /**
     * Returns the action (Runnable) for the button at the given index (0‑based in menu order).
     * Index 0 = PLAY, 1 = OPTIONS, 2 = CREDITS, 3 = QUIT.
     */
    private Runnable getActionForIndex(int index) {
        // Placeholder actions – replace with actual layer pushes when those layers exist
        return switch (index) {
            case 0 -> () -> {}; // PLAY → push PlayingLayer
            case 1 -> () -> {}; // OPTIONS → push SettingsLayer
            case 2 -> () -> {}; // CREDITS → push CreditsLayer
            case 3 -> () -> System.exit(0); // QUIT
            default -> () -> {};
        };
    }

    @Override
    public void update(float deltaTime) {
        for (MenuButton button : buttons) {
            button.update();
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Full‑screen background
        g.setColor(new Color(20, 30, 50));
        g.fillRect(0, 0, GameApplication.WIDTH, GameApplication.HEIGHT);

        // Draw menu panel (if available)
        if (menuPanelSprite != null) {
            menuPanelSprite.draw(g, panelX, panelY);
        } else {
            // Fallback panel
            g.setColor(new Color(40, 50, 70));
            g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        }

        // Draw all buttons
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
}