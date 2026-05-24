package software.project.ui.components;

import software.project.ui.ScreenManager;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final Panel panel;
    private final List<MenuButton> buttons = new ArrayList<>();

    private final Runnable[] actions;

    private final int[] rowIndices;
    private final int buttonCount;
    private final float scaleFactor;
    private final int verticalOffset;
    private final double topMarginPercent;
    private final double bottomMarginPercent;
    private final double innerPaddingPercent;
    private final double effectiveTop;
    private final double effectiveBottom;
    private int[] yPositions;

    private int buttonWidth, buttonHeight;

    public Menu(
        float scaleFactor,
        int verticalOffset,
        int[] rowIndices,
        double topMarginPercent,
        double bottomMarginPercent,
        double innerPaddingPercent
    ) {
        this.scaleFactor = scaleFactor;
        this.verticalOffset = verticalOffset;
        this.rowIndices = rowIndices;
        this.buttonCount = rowIndices.length;
        this.topMarginPercent = topMarginPercent;
        this.bottomMarginPercent = bottomMarginPercent;
        this.innerPaddingPercent = innerPaddingPercent;
        this.effectiveTop = topMarginPercent + innerPaddingPercent;
        this.effectiveBottom = bottomMarginPercent + innerPaddingPercent;
        this.actions = new Runnable[buttonCount];

        this.panel = new Panel(scaleFactor, verticalOffset);
        recomputeLayout();
        createButtons();
    }

    public void setAction(int index, Runnable action) {
        if (index >= 0 && index < actions.length) {
            actions[index] = action;
            if (index < buttons.size()) {
                buttons.get(index).setAction(action);
            }
        }
    }

    public void recomputeLayout() {
        panel.recomputeLayout();

        // Compute the global scale for MenuButtons
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();
        float globalScale = (float) (Math.min((double) virtualW / panel.getOriginalWidth(), (double) virtualH / panel.getOriginalHeight()) * scaleFactor);
        MenuButton.setGlobalScale(globalScale);

        buttonWidth = MenuButton.getScaledWidth();
        buttonHeight = MenuButton.getScaledHeight();

        int panelY = panel.getY();
        int panelHeight = panel.getHeight();
        int usableHeight = (int) (panelHeight * (1.0 - effectiveTop - effectiveBottom));
        int totalButtonsHeight = buttonCount * buttonHeight;
        int gapBetweenButtons = 0;

        if (totalButtonsHeight <= usableHeight) {
            int remaining = usableHeight - totalButtonsHeight;
            gapBetweenButtons = remaining / (buttonCount - 1);
        }

        int startY = panelY + (int) (panelHeight * effectiveTop);
        yPositions = new int[buttonCount];
        for (int i = 0; i < buttonCount; i++) {
            yPositions[i] = startY + i * (buttonHeight + gapBetweenButtons);
        }
    }

    public void createButtons() {
        buttons.clear();
        int centerX = panel.getCenterX();
        for (int i = 0; i < buttonCount; i++) {
            MenuButton button = new MenuButton(rowIndices[i], centerX, yPositions[i]);
            button.setAction(actions[i]);
            buttons.add(button);
        }
    }

    public void update() {
        for (MenuButton button : buttons) {
            button.update();
        }
    }

    public void render(Graphics2D g) {
        panel.draw(g);
        for (MenuButton button : buttons) {
            button.draw(g);
        }
    }

    public void handleMouseMoved(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mouseMoved(e);
        }
    }

    public void handleMousePressed(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mousePressed(e);
        }
    }

    public void handleMouseReleased(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.mouseReleased(e);
        }
    }

    public void onResolutionChanged() {
        recomputeLayout();
        createButtons();
    }
}