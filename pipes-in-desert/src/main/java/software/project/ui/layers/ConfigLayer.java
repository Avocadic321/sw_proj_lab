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
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;

public class ConfigLayer extends Layer {
    private static final int PANEL_WIDTH = 560;
    private static final int PANEL_HEIGHT = 550;
    private static final int PANEL_RADIUS = 16;

    private static final int SLIDER_WIDTH = 360;
    private static final int SLIDER_HEIGHT = 10;
    private static final int KNOB_SIZE = 22;
    private static final int SLIDER_GAP = 70;

    private static final int LABEL_OFFSET_Y = -28;
    private static final int BACK_BUTTON_HEIGHT = 40;
    private static final int BACK_BUTTON_WIDTH = 120;
    private static final int START_BUTTON_HEIGHT = 46;
    private static final int START_BUTTON_WIDTH = 160;

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

    private final Rectangle panelBounds = new Rectangle();
    private final Rectangle plumbersTrack = new Rectangle();
    private final Rectangle saboteursTrack = new Rectangle();
    private final Rectangle goalTrack = new Rectangle();
    private final Rectangle turnTrack = new Rectangle();
    private final Rectangle harshnessTrack = new Rectangle();
    private final Rectangle startButton = new Rectangle();
    private final Rectangle backButton = new Rectangle();

    private DragTarget dragTarget = DragTarget.NONE;

    private int plumbersCount = 2;
    private int saboteursCount = 2;
    private int goalScore = 500;
    private int turnDuration = 30;
    private int harshnessIndex = 1;

    public ConfigLayer(GameApplication app) {
        super(true, true);
        this.app = app;
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        recomputeLayout();
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void render(Graphics2D g) {
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

        drawStartButton(g);
        drawBackButton(g);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (startButton.contains(e.getPoint())) {
            startGame();
            return true;
        }
        if (backButton.contains(e.getPoint())) {
            app.replaceLayer(new MainMenuLayer(app));
            return true;
        }

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

    @Override
    public boolean mouseReleased(MouseEvent e) {
        dragTarget = DragTarget.NONE;
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            app.replaceLayer(new MainMenuLayer(app));
            return true;
        }
        return false;
    }

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

    private void drawLabel(Graphics2D g, String text, int x, int y) {
        drawText(g, text.toLowerCase(), x, y, TEXT_SCALE);
    }

    private void drawValue(Graphics2D g, String text, Rectangle track) {
        int charW = (int) (font.getCharWidth() * VALUE_SCALE);
        int textW = charW * text.length();
        int x = track.x + track.width - textW;
        int y = track.y + LABEL_OFFSET_Y;
        drawText(g, text, x, y, VALUE_SCALE);
    }

    private void drawBackButton(Graphics2D g) {
        g.setColor(new Color(50, 60, 80));
        g.fillRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 10, 10);
        g.setColor(new Color(230, 220, 190));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 10, 10);

        if (font != null) {
            String text = "BACK";
            int textW = (int) (font.getCharWidth() * TEXT_SCALE) * text.length();
            int textH = (int) (font.getCharHeight() * TEXT_SCALE);
            int textX = backButton.x + (backButton.width - textW) / 2;
            int textY = backButton.y + (backButton.height - textH) / 2;
            font.draw(g, text, textX, textY, TEXT_SCALE);
        }
    }

    private void drawStartButton(Graphics2D g) {
        g.setColor(new Color(70, 85, 115));
        g.fillRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 12, 12);
        g.setColor(new Color(240, 225, 190));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 12, 12);

        if (font != null) {
            String text = "START";
            int textW = (int) (font.getCharWidth() * TEXT_SCALE) * text.length();
            int textH = (int) (font.getCharHeight() * TEXT_SCALE);
            int textX = startButton.x + (startButton.width - textW) / 2;
            int textY = startButton.y + (startButton.height - textH) / 2;
            font.draw(g, text, textX, textY, TEXT_SCALE);
        }
    }

    private void drawTitle(Graphics2D g) {
        int titleX = panelBounds.x + 24;
        int titleY = panelBounds.y + 24;
        drawText(g, "new game", titleX, titleY, TITLE_SCALE);

        g.setColor(new Color(230, 220, 190, 120));
        g.setStroke(new BasicStroke(1.5f));
        int lineY = titleY + 18;
        g.drawLine(panelBounds.x + 20, lineY, panelBounds.x + panelBounds.width - 20, lineY);
    }

    private void drawText(Graphics2D g, String text, int x, int y, float scale) {
        if (font == null || text == null) {
            return;
        }
        int charH = (int) (font.getCharHeight() * scale);
        int drawY = y - charH / 2;
        font.draw(g, text.toLowerCase(), x, drawY, scale);
    }

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

        int startX = panelX + (PANEL_WIDTH - START_BUTTON_WIDTH) / 2;
        int startY = harshnessY + 60;
        startButton.setBounds(startX, startY, START_BUTTON_WIDTH, START_BUTTON_HEIGHT);

        int backX = panelX + (PANEL_WIDTH - BACK_BUTTON_WIDTH) / 2;
        int backY = panelY + PANEL_HEIGHT - BACK_BUTTON_HEIGHT - 18;
        backButton.setBounds(backX, backY, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
    }

    private boolean hitSlider(Rectangle track, MouseEvent e) {
        Rectangle hitBox = new Rectangle(
                track.x - KNOB_SIZE / 2,
                track.y - KNOB_SIZE / 2,
                track.width + KNOB_SIZE,
                track.height + KNOB_SIZE);
        return hitBox.contains(e.getPoint());
    }

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

    private int clampStepValue(float ratio, int min, int max, int step) {
        int raw = min + Math.round(ratio * (max - min));
        int clamped = Math.clamp(raw, min, max);
        int snapped = (int) (Math.round((clamped - min) / (float) step) * step) + min;
        return Math.clamp(snapped, min, max);
    }

    private float normalize(int value, int min, int max) {
        if (max <= min) {
            return 0f;
        }
        return (value - min) / (float) (max - min);
    }

    private String getHarshnessLabel() {
        return switch (harshnessIndex) {
            case 0 -> "light";
            case 2 -> "heavy";
            default -> "medium";
        };
    }

    private GameConfig.Harshness getHarshnessValue() {
        return switch (harshnessIndex) {
            case 0 -> GameConfig.Harshness.LIGHT;
            case 2 -> GameConfig.Harshness.HEAVY;
            default -> GameConfig.Harshness.MEDIUM;
        };
    }

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
