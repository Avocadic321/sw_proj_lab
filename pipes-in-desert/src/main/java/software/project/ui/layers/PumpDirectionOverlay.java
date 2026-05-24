package software.project.ui.layers;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Directions;
import software.project.map.Pump;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;
import software.project.ui.components.GameButton;
import software.project.ui.components.Panel;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static software.project.graphics.Sprites.SIMPLE_PANEL;

public class PumpDirectionOverlay extends Layer {

    private static final float OVERLAY_SCALE = 2.5f;
    private static final boolean DRAGGABLE = true;   // set to false to disable dragging

    private static final int BASE_BUTTON_SIZE = 24;
    private static final int BASE_GAP = 5;
    private static final int BASE_PANEL_PADDING = 20;
    private static final float PUMP_SIZE_MULTIPLIER = 1.6f;
    private static final float BASE_TEXT_SCALE = 0.8f;
    private static final int BASE_BOTTOM_BUTTONS_SPACING = 32;
    private static final int BASE_BOTTOM_MARGIN = 0;

    private final int buttonSize;
    private final int gap;
    private final int panelPadding;
    private final int pumpSize;
    private final int distanceFromCenter;
    private final float textScale;
    private final int bottomButtonsSpacing;
    private final int bottomMargin;

    private final Sprite pumpSprite;
    private final SpriteSheet arrowSheet;
    private final SpriteSheet confirmCancelSheet;
    private final Sprite bannerSprite;
    private final Panel backgroundPanel;
    private Banner titleBanner;

    private final Map<Directions, GameButton> directionButtons = new EnumMap<>(Directions.class);
    private final Set<Directions> availableDirections;

    private GameButton confirmButton;
    private GameButton cancelButton;

    private Directions selectedInput = null;
    private Directions selectedOutput = null;
    private boolean isInputPage = true;

    private final GameApplication app;
    private final Pump pump;

    // Dragging state
    private boolean dragging = false;
    private int dragStartX, dragStartY;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public PumpDirectionOverlay(GameApplication app, Pump pump) {
        super(true, false);
        this.app = app;
        this.pump = pump;
        this.availableDirections = pump.getAvailableDirections();

        SpriteManager sm = SpriteManager.getInstance();

        this.buttonSize = (int)(BASE_BUTTON_SIZE * OVERLAY_SCALE);
        this.gap = (int)(BASE_GAP * OVERLAY_SCALE);
        this.panelPadding = (int)(BASE_PANEL_PADDING * OVERLAY_SCALE);
        this.pumpSize = (int)(buttonSize * PUMP_SIZE_MULTIPLIER);
        this.textScale = BASE_TEXT_SCALE * OVERLAY_SCALE;
        this.bottomButtonsSpacing = (int)(BASE_BOTTOM_BUTTONS_SPACING * OVERLAY_SCALE);
        this.bottomMargin = (int)(BASE_BOTTOM_MARGIN * OVERLAY_SCALE);
        this.distanceFromCenter = pumpSize/2 + gap + buttonSize/2;

        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        arrowSheet = sm.getSpriteSheet(SpriteSheets.ARROW_BUTTONS);
        confirmCancelSheet = sm.getSpriteSheet(SpriteSheets.CONFIRM_CANCEL_BUTTONS);
        if (arrowSheet == null) throw new IllegalStateException("Arrow buttons sheet missing");
        if (confirmCancelSheet == null) throw new IllegalStateException("Confirm/cancel sheet missing");

        backgroundPanel = new Panel(1.0f, 0, SIMPLE_PANEL);
        bannerSprite = sm.getSprite(Sprites.PAPER_BANNER);
        if (bannerSprite == null) throw new IllegalStateException("Banner sprite missing");
        titleBanner = new Banner(bannerSprite, 1.0f, "INPUT", textScale);

        createInputPage();
        createBottomButtons();
    }

    // ---------- Sprite row mappings ----------
    private int inwardRow(Directions dir) {
        switch (dir) {
            case NORTH: return 2;
            case EAST:  return 3;
            case SOUTH: return 0;
            case WEST:  return 1;
            default: throw new IllegalArgumentException();
        }
    }

    private int outwardRow(Directions dir) {
        switch (dir) {
            case NORTH: return 0;
            case EAST:  return 1;
            case SOUTH: return 2;
            case WEST:  return 3;
            default: throw new IllegalArgumentException();
        }
    }

    // ---------- Pages ----------
    private void createInputPage() {
        directionButtons.clear();
        for (Directions dir : availableDirections) {
            GameButton btn = new GameButton(arrowSheet, inwardRow(dir), 0, 0, buttonSize, buttonSize);
            btn.setAction(() -> selectInput(dir));
            directionButtons.put(dir, btn);
        }
    }

    private void selectInput(Directions input) {
        selectedInput = input;
        isInputPage = false;
        titleBanner.setText("OUTPUT");
        createOutputPage();
        recomputeLayout();
    }

    private void createOutputPage() {
        directionButtons.clear();

        for (Directions dir : availableDirections) {
            boolean isInputSide = (dir == selectedInput);
            int row = isInputSide ? inwardRow(dir) : outwardRow(dir);
            GameButton btn = new GameButton(arrowSheet, row, 0, 0, buttonSize, buttonSize);
            btn.setEnabled(!isInputSide);
            if (!isInputSide) {
                btn.setAction(() -> selectOutput(dir, btn));
            }
            directionButtons.put(dir, btn);
        }

        selectedOutput = null;
        confirmButton.setEnabled(false);
    }

    private void selectOutput(Directions output, GameButton clickedButton) {
        if (selectedOutput == output) return;
        if (selectedOutput != null) {
            directionButtons.get(selectedOutput).setEnabled(true);
        }
        clickedButton.setEnabled(false);
        selectedOutput = output;
        confirmButton.setEnabled(true);
    }

    // ---------- Bottom buttons ----------
    private void createBottomButtons() {
        confirmButton = new GameButton(confirmCancelSheet, 0, 0, 0, buttonSize, buttonSize);
        cancelButton = new GameButton(confirmCancelSheet, 1, 0, 0, buttonSize, buttonSize);

        cancelButton.setAction(() -> {
            if (isInputPage) {
                app.popLayer();
            } else {
                resetToInputPage();
            }
        });

        confirmButton.setAction(() -> {
            if (selectedInput != null && selectedOutput != null) {
                pump.setDirection(selectedInput, selectedOutput);
                System.out.println("Confirm: " + selectedInput + " -> " + selectedOutput);
                app.popLayer();
            }
        });

        confirmButton.setEnabled(false);
    }

    private void resetToInputPage() {
        isInputPage = true;
        selectedInput = null;
        selectedOutput = null;
        createInputPage();
        createBottomButtons();
        recomputeLayout();
        titleBanner.setText("INPUT");
        confirmButton.setEnabled(false);
    }

    // ---------- Layout ----------
    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();
        int centreX = screenW / 2 + dragOffsetX;
        int centreY = screenH / 2 + dragOffsetY;

        // Position direction buttons
        for (Map.Entry<Directions, GameButton> entry : directionButtons.entrySet()) {
            Directions dir = entry.getKey();
            GameButton btn = entry.getValue();
            switch (dir) {
                case NORTH -> btn.setCenter(centreX, centreY - distanceFromCenter);
                case EAST  -> btn.setCenter(centreX + distanceFromCenter, centreY);
                case SOUTH -> btn.setCenter(centreX, centreY + distanceFromCenter);
                case WEST  -> btn.setCenter(centreX - distanceFromCenter, centreY);
            }
        }

        int contentWidth = 2 * distanceFromCenter + buttonSize + 2 * panelPadding;
        int contentHeight = 2 * distanceFromCenter + buttonSize + 2 * panelPadding;
        int panelX = centreX - contentWidth/2;
        int panelY = centreY - contentHeight/2;
        backgroundPanel.setPosition(panelX, panelY);
        backgroundPanel.setSize(contentWidth, contentHeight);

        float bannerScale = (float)contentWidth / bannerSprite.getWidth();
        titleBanner = new Banner(bannerSprite, bannerScale, titleBanner.getText(), textScale);
        int bannerX = panelX + (contentWidth - titleBanner.getWidth())/2;
        int bannerY = panelY - titleBanner.getHeight() - 10;
        titleBanner.setPosition(bannerX, bannerY);

        int buttonsTotalWidth = 2 * buttonSize + bottomButtonsSpacing;
        int startX = centreX - buttonsTotalWidth / 2;
        int bottomY = panelY + contentHeight - buttonSize - bottomMargin;
        cancelButton.setPosition(startX, bottomY);
        confirmButton.setPosition(startX + buttonSize + bottomButtonsSpacing, bottomY);
    }

    // ---------- Dragging logic ----------
    @Override
    public boolean mousePressed(MouseEvent e) {
        // Check if the click is on any direction button
        for (GameButton btn : directionButtons.values()) {
            if (btn.getBounds().contains(e.getX(), e.getY())) {
                btn.mousePressed(e);
                return true;
            }
        }
        // Check confirm/cancel buttons
        if (confirmButton.getBounds().contains(e.getX(), e.getY())) {
            confirmButton.mousePressed(e);
            return true;
        }
        if (cancelButton.getBounds().contains(e.getX(), e.getY())) {
            cancelButton.mousePressed(e);
            return true;
        }
        // If no button was clicked and dragging is enabled, start dragging on background panel
        if (DRAGGABLE && backgroundPanel.getBounds().contains(e.getX(), e.getY())) {
            dragging = true;
            dragStartX = e.getX();
            dragStartY = e.getY();
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (dragging && DRAGGABLE) {
            int dx = e.getX() - dragStartX;
            int dy = e.getY() - dragStartY;
            if (dx != 0 || dy != 0) {
                dragOffsetX += dx;
                dragOffsetY += dy;
                dragStartX = e.getX();
                dragStartY = e.getY();
                recomputeLayout();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            return true;
        }
        // Normal button release handling
        for (GameButton btn : directionButtons.values()) btn.mouseReleased(e);
        confirmButton.mouseReleased(e);
        cancelButton.mouseReleased(e);
        return true;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (!dragging) {
            for (GameButton btn : directionButtons.values()) btn.mouseMoved(e);
            confirmButton.mouseMoved(e);
            cancelButton.mouseMoved(e);
        }
        return true;
    }

    // ---------- Layer overrides ----------
    @Override
    public void onEnter() {
        resetToInputPage();
        dragOffsetX = 0;
        dragOffsetY = 0;
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void update(float deltaTime) {
        for (GameButton btn : directionButtons.values()) btn.update();
        confirmButton.update();
        cancelButton.update();
    }

    @Override
    public void render(Graphics2D g) {
        backgroundPanel.draw(g);
        pumpSprite.drawCentered(g, ScreenManager.getInstance().getVirtualWidth()/2 + dragOffsetX,
                                ScreenManager.getInstance().getVirtualHeight()/2 + dragOffsetY, pumpSize, 0);
        for (GameButton btn : directionButtons.values()) btn.draw(g);
        titleBanner.draw(g);
        confirmButton.draw(g);
        cancelButton.draw(g);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_D) {
            app.popLayer();
            return true;
        }
        return false;
    }
}