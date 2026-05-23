package software.project.ui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.ui.ScreenManager;
import software.project.ui.components.MenuButton;
import software.project.utils.SpritesEnum;

public class PauseOverlay extends Layer {
    private static final int BUTTON_COUNT = 3;

    private static final int FALLBACK_MENU_WIDTH = 280;
    private static final int FALLBACK_MENU_HEIGHT = 400;

    private static final double TOP_MARGIN_PERCENT = 0.25;
    private static final double BOTTOM_MARGIN_PERCENT = 0.10;
    private static final double INNER_PADDING_PERCENT = 0.05;
    private static final double EFFECTIVE_TOP = TOP_MARGIN_PERCENT + INNER_PADDING_PERCENT;
    private static final double EFFECTIVE_BOTTOM = BOTTOM_MARGIN_PERCENT + INNER_PADDING_PERCENT;

    private static final float MENU_SCALE_FACTOR = 0.6f;
    private static final int MENU_VERTICAL_OFFSET = 30;

    private static final int[] BUTTON_ROW_INDICES = { 0, 1, 2 };

    private final List<MenuButton> buttons = new ArrayList<>();

    private Sprite menuPanelSprite;
    private int originalPanelWidth, originalPanelHeight;
    private int panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight;

    private Runnable resumeAction = () -> {
    };
    private Runnable optionsAction = () -> {
    };
    private Runnable quitAction = () -> {
    };

    public PauseOverlay() {
        super(true, true);
        loadSprites();
        createButtons();
    }

    public void setResumeAction(Runnable resumeAction) {
        if (resumeAction != null) {
            this.resumeAction = resumeAction;
        } else {
            this.resumeAction = () -> {
            };
        }
    }

    public void setOptionsAction(Runnable optionsAction) {
        if (optionsAction != null) {
            this.optionsAction = optionsAction;
        } else {
            this.optionsAction = () -> {
            };
        }
    }

    public void setQuitAction(Runnable quitAction) {
        if (quitAction != null) {
            this.quitAction = quitAction;
        } else {
            this.quitAction = () -> {
            };
        }
    }

    private void loadSprites() {
        SpriteManager sm = SpriteManager.getInstance();
        menuPanelSprite = sm.getSprite(SpritesEnum.MENU_PANEL);

        if (menuPanelSprite == null) {
            originalPanelWidth = FALLBACK_MENU_WIDTH;
            originalPanelHeight = FALLBACK_MENU_HEIGHT;
        } else {
            originalPanelWidth = menuPanelSprite.getWidth();
            originalPanelHeight = menuPanelSprite.getHeight();
        }

        recomputeLayout();
    }

    private void recomputeLayout() {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        double fitScaleX = (double) virtualW / originalPanelWidth;
        double fitScaleY = (double) virtualH / originalPanelHeight;
        double fitScale = Math.min(fitScaleX, fitScaleY);
        double finalScale = fitScale * MENU_SCALE_FACTOR;

        panelDrawWidth = (int) (originalPanelWidth * finalScale);
        panelDrawHeight = (int) (originalPanelHeight * finalScale);
        panelDrawX = (virtualW - panelDrawWidth) / 2;
        panelDrawY = (virtualH - panelDrawHeight) / 2 + MENU_VERTICAL_OFFSET;

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
            default -> () -> {
            };
        };
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
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

        if (menuPanelSprite != null) {
            menuPanelSprite.draw(g, panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight);
        } else {
            g.setColor(new Color(40, 50, 70));
            g.fillRoundRect(panelDrawX, panelDrawY, panelDrawWidth, panelDrawHeight, 20, 20);
        }

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
