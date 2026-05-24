package software.project.ui.layers;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;
import software.project.ui.components.GameButton;
import software.project.ui.components.Panel;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import static software.project.graphics.Sprites.SIMPLE_PANEL;

public class PumpDirectionOverlay extends Layer {

    // ---- Global scale factor ----
    private static final float OVERLAY_SCALE = 2.5f;

    // ---- Base sizes (at scale = 1.0) ----
    private static final int BASE_BUTTON_SIZE = 24;
    private static final int BASE_GAP = 5;          // gap between pump outer edge and button inner edge
    private static final int BASE_PANEL_PADDING = 20;
    private static final float PUMP_SIZE_MULTIPLIER = 1.6f;  // pump size = button size * this
    private static final float BASE_TEXT_SCALE = 0.8f;       // text scale at OVERLAY_SCALE = 1.0

    // ---- Derived sizes (computed from OVERLAY_SCALE) ----
    private final int buttonSize;
    private final int gap;
    private final int panelPadding;
    private final int pumpSize;
    private final int distanceFromCenter;  // from pump centre to button centre
    private final float textScale;         // scaled with OVERLAY_SCALE

    private final Sprite pumpSprite;
    private final SpriteSheet arrowSheet;
    private final Sprite bannerSprite;     // keep for banner resizing
    private final Panel backgroundPanel;
    private Banner titleBanner;            // recreated when needed

    private GameButton topButton, rightButton, bottomButton, leftButton;
    private int centreX, centreY;
    private boolean isInputPage = true;
    private String selectedInput = null;

    private DirectionCallback callback;

    public interface DirectionCallback {
        void onDirectionSelected(String input, String output);
    }

    public PumpDirectionOverlay() {
        super(true, false);
        SpriteManager sm = SpriteManager.getInstance();

        // Compute scaled sizes
        this.buttonSize = (int)(BASE_BUTTON_SIZE * OVERLAY_SCALE);
        this.gap = (int)(BASE_GAP * OVERLAY_SCALE);
        this.panelPadding = (int)(BASE_PANEL_PADDING * OVERLAY_SCALE);
        this.pumpSize = (int)(buttonSize * PUMP_SIZE_MULTIPLIER);
        this.textScale = BASE_TEXT_SCALE * OVERLAY_SCALE;
        // Distance from pump centre to button centre = pump radius + gap + button radius
        this.distanceFromCenter = pumpSize/2 + gap + buttonSize/2;

        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        arrowSheet = sm.getSpriteSheet(SpriteSheets.ARROW_BUTTONS);
        if (arrowSheet == null) throw new IllegalStateException("Arrow buttons sheet missing");

        backgroundPanel = new Panel(1.0f, 0, SIMPLE_PANEL);
        bannerSprite = sm.getSprite(Sprites.PAPER_BANNER);
        if (bannerSprite == null) throw new IllegalStateException("Banner sprite missing");
        // Create banner with temporary scale; will be resized later
        titleBanner = new Banner(bannerSprite, 1.0f, "INPUT", textScale);

        createInputPage();
    }

    private void createInputPage() {
        topButton = new GameButton(arrowSheet, 2, 0, 0, buttonSize, buttonSize);
        rightButton = new GameButton(arrowSheet, 3, 0, 0, buttonSize, buttonSize);
        bottomButton = new GameButton(arrowSheet, 0, 0, 0, buttonSize, buttonSize);
        leftButton = new GameButton(arrowSheet, 1, 0, 0, buttonSize, buttonSize);

        topButton.setAction(() -> selectInput("UP"));
        rightButton.setAction(() -> selectInput("RIGHT"));
        bottomButton.setAction(() -> selectInput("DOWN"));
        leftButton.setAction(() -> selectInput("LEFT"));
    }

    private void selectInput(String input) {
        selectedInput = input;
        isInputPage = false;
        titleBanner.setText("OUTPUT");
        createOutputPage();
        recomputeLayout();
    }

    private void createOutputPage() {
        topButton = new GameButton(arrowSheet, 0, 0, 0, buttonSize, buttonSize);
        rightButton = new GameButton(arrowSheet, 1, 0, 0, buttonSize, buttonSize);
        bottomButton = new GameButton(arrowSheet, 2, 0, 0, buttonSize, buttonSize);
        leftButton = new GameButton(arrowSheet, 3, 0, 0, buttonSize, buttonSize);

        switch (selectedInput) {
            case "UP":    topButton.setEnabled(false); break;
            case "RIGHT": rightButton.setEnabled(false); break;
            case "DOWN":  bottomButton.setEnabled(false); break;
            case "LEFT":  leftButton.setEnabled(false); break;
        }

        topButton.setAction(() -> finish("UP"));
        rightButton.setAction(() -> finish("RIGHT"));
        bottomButton.setAction(() -> finish("DOWN"));
        leftButton.setAction(() -> finish("LEFT"));
    }

    private void finish(String output) {
        if (callback != null) {
            callback.onDirectionSelected(selectedInput, output);
        }
    }

    public void setCallback(DirectionCallback callback) {
        this.callback = callback;
    }

    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();
        centreX = screenW / 2;
        centreY = screenH / 2;

        if (topButton != null) {
            topButton.setCenter(centreX, centreY - distanceFromCenter);
            rightButton.setCenter(centreX + distanceFromCenter, centreY);
            bottomButton.setCenter(centreX, centreY + distanceFromCenter);
            leftButton.setCenter(centreX - distanceFromCenter, centreY);
        }

        // Panel dimensions
        int contentWidth = 2 * distanceFromCenter + buttonSize + 2 * panelPadding;
        int contentHeight = 2 * distanceFromCenter + buttonSize + 2 * panelPadding;
        int panelX = centreX - contentWidth/2;
        int panelY = centreY - contentHeight/2;
        backgroundPanel.setPosition(panelX, panelY);
        backgroundPanel.setSize(contentWidth, contentHeight);

        // Banner: make its width equal to panel width, maintain aspect ratio
        float bannerScale = (float)contentWidth / bannerSprite.getWidth();
        // Recreate banner with new scale and the scaled text size
        titleBanner = new Banner(bannerSprite, bannerScale, titleBanner.getText(), textScale);
        // Position banner centered above panel
        int bannerX = panelX + (contentWidth - titleBanner.getWidth())/2;
        int bannerY = panelY - titleBanner.getHeight() - 10;
        titleBanner.setPosition(bannerX, bannerY);
    }

    @Override
    public void onEnter() {
        isInputPage = true;
        selectedInput = null;
        createInputPage();
        recomputeLayout();
        titleBanner.setText("INPUT");
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void update(float deltaTime) {
        if (topButton != null) {
            topButton.update();
            rightButton.update();
            bottomButton.update();
            leftButton.update();
        }
    }

    @Override
    public void render(Graphics2D g) {
        backgroundPanel.draw(g);
        if (pumpSprite != null) {
            pumpSprite.drawCentered(g, centreX, centreY, pumpSize, 0);
        }
        if (topButton != null) {
            topButton.draw(g);
            rightButton.draw(g);
            bottomButton.draw(g);
            leftButton.draw(g);
        }
        titleBanner.draw(g);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (topButton != null) {
            topButton.mousePressed(e);
            rightButton.mousePressed(e);
            bottomButton.mousePressed(e);
            leftButton.mousePressed(e);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (topButton != null) {
            topButton.mouseReleased(e);
            rightButton.mouseReleased(e);
            bottomButton.mouseReleased(e);
            leftButton.mouseReleased(e);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (topButton != null) {
            topButton.mouseMoved(e);
            rightButton.mouseMoved(e);
            bottomButton.mouseMoved(e);
            leftButton.mouseMoved(e);
        }
        return true;
    }
}