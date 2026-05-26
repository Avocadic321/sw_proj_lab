package software.project.ui.layers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import software.project.core.GameConfig;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.components.GeneralButton;

/**
 * Configuration overlay for choosing player counts and match settings before
 * starting a new game.
 */
public class ConfigLayer extends Layer {
    private static final int PANEL_WIDTH = 560;
    private static final int PANEL_HEIGHT = 550;
    private static final int PANEL_RADIUS = 16;

    private static final int SLIDER_WIDTH = 360;
    private static final int SLIDER_HEIGHT = 10;
    private static final int KNOB_SIZE = 22;
    private static final int SLIDER_GAP = 70;

    private static final int LABEL_OFFSET_Y = -28;
    private static final float TEXT_SCALE = 1.0f;
    private static final float VALUE_SCALE = 0.9f;
    private static final float TITLE_SCALE = 1.25f;

    private static final Color PANEL_FILL = new Color(20, 25, 35, 230);
    private static final Color PANEL_SHADOW = new Color(0, 0, 0, 120);
    private static final Color PANEL_STROKE = new Color(240, 220, 180, 200);
    private static final Color SLIDER_TRACK = new Color(80, 90, 110);
    private static final Color SLIDER_GROOVE = new Color(45, 55, 70);
    private static final Color PLUMBER_COLOR = new Color(70, 140, 230);
    private static final Color SABOTEUR_COLOR = new Color(210, 80, 80);
    private static final Color GOAL_COLOR = new Color(230, 180, 80);
    private static final Color TURN_COLOR = new Color(90, 180, 130);
    private static final Color HARSHNESS_COLOR = new Color(220, 130, 70);

    private static final int MIN_TEAM = GameConfig.MIN_PLAYERS;
    private static final int MAX_TEAM = GameConfig.MAX_PLAYERS;
    private static final int MIN_GOAL = 50;
    private static final int MAX_GOAL = GameConfig.MAX_GOAL_SCORE;
    private static final int MIN_TURN = GameConfig.MIN_TURN_DURATION;
    private static final int MAX_TURN = GameConfig.MAX_TURN_DURATION;

    private final GameApplication app;
    private final BitmapFont font;
    private final SpriteSheet buttonSheet;
    private final Sprite sandSprite;

    private final Rectangle panelBounds = new Rectangle();
    private final Rectangle plumbersTrack = new Rectangle();
    private final Rectangle saboteursTrack = new Rectangle();
    private final Rectangle goalTrack = new Rectangle();
    private final Rectangle turnTrack = new Rectangle();
    private final Rectangle harshnessTrack = new Rectangle();

    private GeneralButton startButton;
    private GeneralButton backButton;

    private DragTarget dragTarget = DragTarget.NONE;

    private int plumbersCount = 2;
    private int saboteursCount = 2;
    private int goalScore = 500;
    private int turnDuration = 30;
    private int harshnessIndex = 1;

    /**
     * Creates the configuration layer for the provided application.
     *
     * @param app game application instance used to launch or leave the setup screen
     */
    public ConfigLayer(GameApplication app) {
        super(true, true);
        this.app = app;
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        this.buttonSheet = SpriteManager.getInstance().getSpriteSheet(SpriteSheets.GENERAL_BUTTONS);

        SpriteSheet borderSheet = SpriteManager.getInstance().getSpriteSheet(SpriteSheets.MAP_BORDER);
        this.sandSprite = (borderSheet != null) ? borderSheet.getSprite(1, 1) : SpriteManager.getInstance().getSprite(Sprites.GRASS);

        if (buttonSheet == null) {
            throw new IllegalStateException("GENERAL_BUTTON sheet missing");
        }

        recomputeLayout();
    }

    /**
     * Recomputes the panel and control layout when the virtual resolution changes.
     *
     * @param newWidth  new virtual width
     * @param newHeight new virtual height
     */
    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    /**
     * Draws sand background filling the entire screen.
     */
    private void drawSandBackground(Graphics2D g) {
        int vw = ScreenManager.getInstance().getVirtualWidth();
        int vh = ScreenManager.getInstance().getVirtualHeight();
        int tileSize = 64; // Use a fixed tile size for background sand

        int totalCols = (int) Math.ceil((double) vw / tileSize);
        int totalRows = (int) Math.ceil((double) vh / tileSize);

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = col * tileSize;
                int y = row * tileSize;
                if (sandSprite != null) {
                    sandSprite.draw(g, x, y, tileSize, tileSize);
                } else {
                    g.setColor(new Color(180, 150, 110));
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }

    /**
     * Dynamically creates or updates buttons based on current layout.
     * Both buttons use the same fixed size (140px width, 46px height).
     */
    private void createButtons() {
        int centerX = panelBounds.x + panelBounds.width / 2;
        int buttonGap = 20;
        int buttonY = harshnessTrack.y + 80;
        int buttonWidth = 140;
        int buttonHeight = 46;

        // Create both buttons with same dimensions
        startButton = new GeneralButton(buttonSheet, 0, 0, 0, "START", 1.0f, 1.0f);
        backButton = new GeneralButton(buttonSheet, 0, 0, 0, "BACK", 1.0f, 1.0f);

        // Force same size for both buttons
        startButton.setSize(buttonWidth, buttonHeight);
        backButton.setSize(buttonWidth, buttonHeight);

        // Position buttons side by side
        int totalWidth = buttonWidth + buttonGap + buttonWidth;
        int startX = centerX - totalWidth / 2;

        startButton.setPosition(startX, buttonY);
        backButton.setPosition(startX + buttonWidth + buttonGap, buttonY);

        startButton.setAction(this::startGame);
        backButton.setAction(() -> app.replaceLayer(new MainMenuLayer(app)));
    }

    /**
     * Renders the configuration panel, sliders, and action buttons.
     *
     * @param g graphics context used for drawing the layer
     */
    @Override
    public void render(Graphics2D g) {
        // Draw sand background first
        drawSandBackground(g);

        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, screenW, screenH);

        g.setColor(PANEL_SHADOW);
        g.fillRoundRect(panelBounds.x + 6, panelBounds.y + 8, panelBounds.width, panelBounds.height,
                        PANEL_RADIUS, PANEL_RADIUS);
        g.setColor(PANEL_FILL);
        g.fillRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, PANEL_RADIUS,
                        PANEL_RADIUS);
        g.setColor(PANEL_STROKE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, PANEL_RADIUS,
                        PANEL_RADIUS);

        drawTitle(g);

        drawSlider(g, plumbersTrack, normalize(plumbersCount, MIN_TEAM, MAX_TEAM), PLUMBER_COLOR, "PLUMBERS",
                   Integer.toString(plumbersCount));
        drawSlider(g, saboteursTrack, normalize(saboteursCount, MIN_TEAM, MAX_TEAM), SABOTEUR_COLOR, "SABOTEURS",
                   Integer.toString(saboteursCount));
        drawSlider(g, goalTrack, normalize(goalScore, MIN_GOAL, MAX_GOAL), GOAL_COLOR, "GOAL SCORE",
                   Integer.toString(goalScore));
        drawSlider(g, turnTrack, normalize(turnDuration, MIN_TURN, MAX_TURN), TURN_COLOR, "TURN SECONDS",
                   Integer.toString(turnDuration));
        drawSlider(g, harshnessTrack, normalize(harshnessIndex, 0, 2), HARSHNESS_COLOR, "HARSHNESS",
                   getHarshnessLabel());

        if (startButton != null && backButton != null) {
            startButton.draw(g);
            backButton.draw(g);
        }
    }

    /**
     * Handles mouse presses on buttons and slider tracks.
     *
     * @param e mouse press event from the UI system
     * @return always {@code true} because the configuration overlay consumes the press
     */
    @Override
    public boolean mousePressed(MouseEvent e) {
        // Let buttons process the press first
        if (startButton != null) startButton.mousePressed(e);
        if (backButton != null) backButton.mousePressed(e);

        if (hitSlider(plumbersTrack, e)) {
            dragTarget = DragTarget.PLUMBERS;
            updateSliderValue(plumbersTrack, e.getX());
            return true;
        }
        if (hitSlider(saboteursTrack, e)) {
            dragTarget = DragTarget.SABOTEURS;
            updateSliderValue(saboteursTrack, e.getX());
            return true;
        }
        if (hitSlider(goalTrack, e)) {
            dragTarget = DragTarget.GOAL;
            updateSliderValue(goalTrack, e.getX());
            return true;
        }
        if (hitSlider(turnTrack, e)) {
            dragTarget = DragTarget.TURN;
            updateSliderValue(turnTrack, e.getX());
            return true;
        }
        if (hitSlider(harshnessTrack, e)) {
            dragTarget = DragTarget.HARSHNESS;
            updateSliderValue(harshnessTrack, e.getX());
            return true;
        }

        return true;
    }

    /**
     * Updates the currently dragged slider while the mouse is moving.
     *
     * @param e mouse drag event from the UI system
     * @return {@code true} when a slider is being adjusted; otherwise {@code false}
     */
    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (dragTarget != DragTarget.NONE) {
            Rectangle track = switch (dragTarget) {
                case PLUMBERS -> plumbersTrack;
                case SABOTEURS -> saboteursTrack;
                case GOAL -> goalTrack;
                case TURN -> turnTrack;
                case HARSHNESS -> harshnessTrack;
                default -> null;
            };
            if (track != null) {
                updateSliderValue(track, e.getX());
            }
            return true;
        }
        return false;
    }

    /**
     * Clears the current slider drag target when the mouse button is released.
     * Also forwards the release event to the buttons.
     *
     * @param e mouse release event from the UI system
     * @return always {@code true} because the overlay consumes the release
     */
    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (startButton != null) startButton.mouseReleased(e);
        if (backButton != null) backButton.mouseReleased(e);
        dragTarget = DragTarget.NONE;
        return true;
    }

    /**
     * Forwards mouse movement events to the buttons.
     *
     * @param e mouse moved event
     * @return always {@code true}
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (startButton != null) startButton.mouseMoved(e);
        if (backButton != null) backButton.mouseMoved(e);
        return true;
    }

    /**
     * Handles keyboard shortcuts for leaving the configuration screen.
     *
     * @param e key press event from the UI system
     * @return {@code true} when the escape key is handled; otherwise {@code false}
     */
    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            app.replaceLayer(new MainMenuLayer(app));
            return true;
        }
        return false;
    }

    /**
     * Draws a slider track, its fill, knob, label, and current value.
     *
     * @param g       graphics context used for drawing
     * @param track   slider bounds
     * @param value   normalized slider value in the range {@code [0, 1]}
     * @param accent  accent color used for the fill and knob
     * @param label   slider label text
     * @param display formatted value text shown on the right side
     */
    private void drawSlider(Graphics2D g, Rectangle track, float value, Color accent, String label, String display) {
        g.setColor(SLIDER_GROOVE);
        g.fillRoundRect(track.x, track.y, track.width, track.height, 8, 8);
        g.setColor(SLIDER_TRACK);
        g.fillRoundRect(track.x + 1, track.y + 1, track.width - 2, track.height - 2, 8, 8);

        int fillWidth = (int) (track.width * value);
        g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 180));
        g.fillRoundRect(track.x, track.y, fillWidth, track.height, 6, 6);

        int knobX = track.x + fillWidth - KNOB_SIZE / 2;
        int knobY = track.y + (track.height - KNOB_SIZE) / 2;
        g.setColor(accent);
        g.fillRoundRect(knobX, knobY, KNOB_SIZE, KNOB_SIZE, 8, 8);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(knobX, knobY, KNOB_SIZE, KNOB_SIZE, 8, 8);

        drawLabel(g, label, track.x, track.y + LABEL_OFFSET_Y);
        drawValue(g, display, track);
    }

    /**
     * Draws a lowercase label above a slider.
     *
     * @param g    graphics context used for drawing
     * @param text label text
     * @param x    x-coordinate of the label anchor
     * @param y    y-coordinate of the label anchor
     */
    private void drawLabel(Graphics2D g, String text, int x, int y) {
        drawText(g, text.toLowerCase(), x, y, TEXT_SCALE);
    }

    /**
     * Draws the current slider value aligned to the right edge of the track.
     *
     * @param g     graphics context used for drawing
     * @param text  value text to render
     * @param track slider bounds used for alignment
     */
    private void drawValue(Graphics2D g, String text, Rectangle track) {
        int charW = (int) (font.getCharWidth() * VALUE_SCALE);
        int textW = charW * text.length();
        int x = track.x + track.width - textW;
        int y = track.y + LABEL_OFFSET_Y;
        drawText(g, text, x, y, VALUE_SCALE);
    }

    /**
     * Draws the panel title and separator line.
     *
     * @param g graphics context used for drawing
     */
    private void drawTitle(Graphics2D g) {
        int titleX = panelBounds.x + 24;
        int titleY = panelBounds.y + 24;
        drawText(g, "new game", titleX, titleY, TITLE_SCALE);

        g.setColor(new Color(230, 220, 190, 120));
        g.setStroke(new BasicStroke(1.5f));
        int lineY = titleY + 18;
        g.drawLine(panelBounds.x + 20, lineY, panelBounds.x + panelBounds.width - 20, lineY);
    }

    /**
     * Draws text using the configured bitmap font and scale.
     *
     * @param g     graphics context used for drawing
     * @param text  text to render
     * @param x     left anchor for the text
     * @param y     vertical anchor for the text
     * @param scale font scale to apply while drawing
     */
    private void drawText(Graphics2D g, String text, int x, int y, float scale) {
        if (font == null || text == null) {
            return;
        }
        int charH = (int) (font.getCharHeight() * scale);
        int drawY = y - charH / 2;
        font.draw(g, text.toLowerCase(), x, drawY, scale);
    }

    /**
     * Recomputes all bounds used by the overlay controls and creates buttons.
     */
    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        int panelX = (screenW - PANEL_WIDTH) / 2;
        int panelY = (screenH - PANEL_HEIGHT) / 2;
        panelBounds.setBounds(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        int sliderX = panelX + (PANEL_WIDTH - SLIDER_WIDTH) / 2;
        int plumbersY = panelY + 90;
        int saboteursY = plumbersY + SLIDER_GAP;
        int goalY = saboteursY + SLIDER_GAP;
        int turnY = goalY + SLIDER_GAP;
        int harshnessY = turnY + SLIDER_GAP;
        plumbersTrack.setBounds(sliderX, plumbersY, SLIDER_WIDTH, SLIDER_HEIGHT);
        saboteursTrack.setBounds(sliderX, saboteursY, SLIDER_WIDTH, SLIDER_HEIGHT);
        goalTrack.setBounds(sliderX, goalY, SLIDER_WIDTH, SLIDER_HEIGHT);
        turnTrack.setBounds(sliderX, turnY, SLIDER_WIDTH, SLIDER_HEIGHT);
        harshnessTrack.setBounds(sliderX, harshnessY, SLIDER_WIDTH, SLIDER_HEIGHT);

        // Create buttons dynamically based on new layout
        createButtons();
    }

    /**
     * Tests whether the mouse event is close enough to a slider for dragging.
     *
     * @param track slider bounds to test
     * @param e     mouse event to evaluate
     * @return {@code true} when the event falls within the slider hit box
     */
    private boolean hitSlider(Rectangle track, MouseEvent e) {
        Rectangle hitBox = new Rectangle(
            track.x - KNOB_SIZE / 2,
            track.y - KNOB_SIZE / 2,
            track.width + KNOB_SIZE,
            track.height + KNOB_SIZE);
        return hitBox.contains(e.getPoint());
    }

    /**
     * Updates the active slider value based on the mouse x-position.
     *
     * @param track  slider bounds
     * @param mouseX horizontal mouse position in panel coordinates
     */
    private void updateSliderValue(Rectangle track, int mouseX) {
        float value = (mouseX - track.x) / (float) track.width;
        value = Math.clamp(value, 0.0f, 1.0f);

        switch (dragTarget) {
            case PLUMBERS -> plumbersCount = clampStepValue(value, MIN_TEAM, MAX_TEAM, 1);
            case SABOTEURS -> saboteursCount = clampStepValue(value, MIN_TEAM, MAX_TEAM, 1);
            case GOAL -> goalScore = clampStepValue(value, MIN_GOAL, MAX_GOAL, 10);
            case TURN -> turnDuration = clampStepValue(value, MIN_TURN, MAX_TURN, 1);
            case HARSHNESS -> harshnessIndex = clampStepValue(value, 0, 2, 1);
            default -> {
            }
        }
    }

    /**
     * Converts a normalized ratio into a stepped integer value.
     *
     * @param ratio normalized value in the range {@code [0, 1]}
     * @param min   minimum allowed value
     * @param max   maximum allowed value
     * @param step  snapping increment
     * @return clamped and snapped integer within the configured bounds
     */
    private int clampStepValue(float ratio, int min, int max, int step) {
        int raw = min + Math.round(ratio * (max - min));
        int clamped = Math.clamp(raw, min, max);
        int snapped = (int) (Math.round((clamped - min) / (float) step) * step) + min;
        return Math.clamp(snapped, min, max);
    }

    /**
     * Normalizes an integer value to the range {@code [0, 1]}.
     *
     * @param value current value
     * @param min   minimum bound of the value range
     * @param max   maximum bound of the value range
     * @return normalized ratio, or zero when the range is degenerate
     */
    private float normalize(int value, int min, int max) {
        if (max <= min) {
            return 0f;
        }
        return (value - min) / (float) (max - min);
    }

    /**
     * Returns the display label for the currently selected harshness.
     *
     * @return light, medium, or heavy depending on the harshness index
     */
    private String getHarshnessLabel() {
        return switch (harshnessIndex) {
            case 0 -> "light";
            case 2 -> "heavy";
            default -> "medium";
        };
    }

    /**
     * Converts the current harshness index into a configuration enum value.
     *
     * @return configured harshness level for the new game
     */
    private GameConfig.Harshness getHarshnessValue() {
        return switch (harshnessIndex) {
            case 0 -> GameConfig.Harshness.LIGHT;
            case 2 -> GameConfig.Harshness.HEAVY;
            default -> GameConfig.Harshness.MEDIUM;
        };
    }

    /**
     * Builds a game configuration from the selected options and starts play.
     */
    private void startGame() {
        GameConfig config = new GameConfig();
        config.setPlumberCount(plumbersCount);
        config.setSaboteurCount(saboteursCount);
        config.setGoalScore(goalScore);
        config.setTurnDurationSeconds(turnDuration);
        config.setHarshness(getHarshnessValue());
        app.replaceLayer(new PlayingLayer(app, config));
    }

    private enum DragTarget {
        NONE,
        PLUMBERS,
        SABOTEURS,
        GOAL,
        TURN,
        HARSHNESS
    }
}