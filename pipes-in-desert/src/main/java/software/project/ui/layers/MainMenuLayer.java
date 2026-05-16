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
    private static final double INNER_PADDING_PERCENT = 0.03;
    private static final double EFFECTIVE_TOP = TOP_MARGIN_PERCENT + INNER_PADDING_PERCENT;
    private static final double EFFECTIVE_BOTTOM = BOTTOM_MARGIN_PERCENT + INNER_PADDING_PERCENT;

    // Global menu scale factor (1.0 = fit exactly; lower = smaller)
    private static final float MENU_SCALE_FACTOR = 0.65f;

    // Title scale factor (independent from menu panel)
    private static final float TITLE_SCALE_FACTOR = 0.4f;

    // Vertical offset for the whole menu (in virtual screen pixels)
    private static final int MENU_VERTICAL_OFFSET = 50;

    // Title vertical offset from the top of the screen (virtual pixels)
    private static final int TITLE_VERTICAL_OFFSET = 50;

    // Sprite sheet rows: 0=PLAY, 1=OPTIONS, 2=QUIT, 3=CREDITS
    // Desired order: PLAY, OPTIONS, CREDITS, QUIT
    private static final int[] BUTTON_ROW_INDICES = {0, 1, 3, 2};

    private final GameApplication app;
    private final List<MenuButton> buttons = new ArrayList<>();

    private Sprite menuPanelSprite;
    private Sprite menuBackgroundSprite;
    private Sprite menuTitleSprite;

    private int originalPanelWidth, originalPanelHeight;
    private int panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight;
    private int titleDrawX, titleDrawY, titleDrawWidth, titleDrawHeight;

    public MainMenuLayer(GameApplication app) {
        this.app = app;
        loadSprites();
        createButtons();
    }

    private void loadSprites() {
        menuBackgroundSprite = SpriteManager.getInstance().getSprite("menu_background");
        if (menuBackgroundSprite == null) {
            System.out.println("[WARNING] Menu background missing");
        }

        menuPanelSprite = SpriteManager.getInstance().getSprite("menu_panel");
        if (menuPanelSprite == null) {
            System.err.println("[WARNING] Menu panel sprite missing. Using fallback.");
            originalPanelWidth = FALLBACK_MENU_WIDTH;
            originalPanelHeight = FALLBACK_MENU_HEIGHT;
        } else {
            originalPanelWidth = menuPanelSprite.getWidth();
            originalPanelHeight = menuPanelSprite.getHeight();
        }

        menuTitleSprite = SpriteManager.getInstance().getSprite("menu_title");
        if (menuTitleSprite == null) {
            System.out.println("[WARNING] Menu title sprite missing");
        }

        recomputeLayout();
    }

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
        panelDrawY = (virtualH - panelDrawHeight) / 2 + MENU_VERTICAL_OFFSET;

        // Title dimensions – use its own scale factor
        if (menuTitleSprite != null) {
            titleDrawWidth = (int) (menuTitleSprite.getWidth() * TITLE_SCALE_FACTOR);
            titleDrawHeight = (int) (menuTitleSprite.getHeight() * TITLE_SCALE_FACTOR);
            titleDrawX = (virtualW - titleDrawWidth) / 2;
            titleDrawY = TITLE_VERTICAL_OFFSET;
        }

        MenuButton.setGlobalScale((float) finalScale);
    }

    private void createButtons() {
        buttons.clear();
        recomputeLayout();

        int[] yPositions = computeButtonPositions();
        int centerX = panelDrawX + panelDrawWidth / 2;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            MenuButton button = new MenuButton(BUTTON_ROW_INDICES[i], centerX, yPositions[i]);
            button.setAction(getActionForIndex(i));
            buttons.add(button);
        }
    }

    private int[] computeButtonPositions() {
        int scaledButtonHeight = MenuButton.getScaledHeight();
        int usableHeight = (int) (panelDrawHeight * (1.0 - EFFECTIVE_TOP - EFFECTIVE_BOTTOM));
        int totalButtonsHeight = BUTTON_COUNT * scaledButtonHeight;
        int gapBetweenButtons;

        if (totalButtonsHeight > usableHeight) {
            gapBetweenButtons = 0;
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

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        createButtons();
    }

    @Override
    public void update(float deltaTime) {
        for (MenuButton button : buttons) button.update();
    }

    @Override
    public void render(Graphics2D g) {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        // Full‑screen background
        if (menuBackgroundSprite != null) {
            menuBackgroundSprite.draw(g, 0, 0, virtualW, virtualH);
        } else {
            g.setColor(new Color(20, 30, 50));
            g.fillRect(0, 0, virtualW, virtualH);
        }

        // Title (above the menu panel)
        if (menuTitleSprite != null) {
            menuTitleSprite.draw(g, titleDrawX, titleDrawY, titleDrawWidth, titleDrawHeight);
        }

        // Menu panel
        if (menuPanelSprite != null) {
            menuPanelSprite.draw(g, panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight);
        } else {
            g.setColor(new Color(40, 50, 70));
            g.fillRoundRect(panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight, 20, 20);
        }

        // Buttons
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