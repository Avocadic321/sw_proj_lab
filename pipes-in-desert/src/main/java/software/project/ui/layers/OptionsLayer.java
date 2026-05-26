package software.project.ui.layers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import software.project.audio.AudioPlayer;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;

/**
 * Options screen overlay for audio settings and music selection.
 *
 * <p>
 * Provides sliders for music and effect volumes, a file picker for custom
 * music, and reset/back controls.
 * </p>
 */
public class OptionsLayer extends Layer {
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 410;
    private static final int PANEL_RADIUS = 16;

    private static final int SLIDER_WIDTH = 360;
    private static final int SLIDER_HEIGHT = 10;
    private static final int KNOB_SIZE = 22;
    private static final int SLIDER_GAP = 70;

    private static final int LABEL_OFFSET_Y = -28;
    private static final int BACK_BUTTON_HEIGHT = 40;
    private static final int BACK_BUTTON_WIDTH = 120;
    private static final int PICK_BUTTON_HEIGHT = 36;
    private static final int PICK_BUTTON_WIDTH = 180;
    private static final int RESET_BUTTON_HEIGHT = 34;
    private static final int RESET_BUTTON_WIDTH = 180;

    private static final float TEXT_SCALE = 1.0f;
    private static final float VALUE_SCALE = 0.9f;
    private static final float TITLE_SCALE = 1.2f;

    private static final Color PANEL_FILL = new Color(20, 25, 35, 220);
    private static final Color PANEL_SHADOW = new Color(0, 0, 0, 120);
    private static final Color PANEL_STROKE = new Color(240, 220, 180, 200);
    private static final Color SLIDER_TRACK = new Color(80, 90, 110);
    private static final Color SLIDER_GROOVE = new Color(45, 55, 70);
    private static final Color MUSIC_COLOR = new Color(70, 130, 220);
    private static final Color EFFECT_COLOR = new Color(200, 70, 70);

    private final GameApplication app;
    private final AudioPlayer audioPlayer;
    private final BitmapFont font;

    private final Rectangle panelBounds = new Rectangle();
    private final Rectangle musicTrack = new Rectangle();
    private final Rectangle effectTrack = new Rectangle();
    private final Rectangle pickButton = new Rectangle();
    private final Rectangle backButton = new Rectangle();
    private final Rectangle resetButton = new Rectangle();

    private DragTarget dragTarget = DragTarget.NONE;
    private String selectedMusicLabel = "default";

    /**
     * Creates an options overlay for the provided application.
     *
     * @param app game application instance
     */
    public OptionsLayer(GameApplication app) {
        super(true, true);
        this.app = app;
        this.audioPlayer = AudioPlayer.getInstance();
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        recomputeLayout();
    }

    /**
     * Recomputes layout when the virtual resolution changes.
     */
    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    /**
     * Renders the options panel and all controls.
     */
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

        drawSlider(g, musicTrack, audioPlayer.getSongVolume(), MUSIC_COLOR, "MUSIC");
        drawSlider(g, effectTrack, audioPlayer.getEffectVolume(), EFFECT_COLOR, "EFFECTS");
        drawPickButton(g);

        drawResetButton(g);

        drawBackButton(g);
    }

    /**
     * Handles clicks on buttons and slider tracks.
     */
    @Override
    public boolean mousePressed(MouseEvent e) {
        if (backButton.contains(e.getPoint())) {
            app.popLayer();
            return true;
        }
        if (pickButton.contains(e.getPoint())) {
            pickMusicFile();
            return true;
        }
        if (resetButton.contains(e.getPoint())) {
            resetToDefaultMusic();
            return true;
        }

        if (hitSlider(musicTrack, e)) {
            dragTarget = DragTarget.MUSIC;
            updateSliderValue(musicTrack, e.getX());
            return true;
        }
        if (hitSlider(effectTrack, e)) {
            dragTarget = DragTarget.EFFECT;
            updateSliderValue(effectTrack, e.getX());
            return true;
        }
        return true;
    }

    /**
     * Updates slider values while dragging.
     */
    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (dragTarget == DragTarget.MUSIC) {
            updateSliderValue(musicTrack, e.getX());
            return true;
        }
        if (dragTarget == DragTarget.EFFECT) {
            updateSliderValue(effectTrack, e.getX());
            return true;
        }
        return false;
    }

    /**
     * Resets drag tracking on mouse release.
     */
    @Override
    public boolean mouseReleased(MouseEvent e) {
        dragTarget = DragTarget.NONE;
        return true;
    }

    /**
     * Closes the overlay on ESC.
     */
    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            app.popLayer();
            return true;
        }
        return false;
    }

    /**
     * Draws a labeled slider with a filled track and knob.
     */
    private void drawSlider(Graphics2D g, Rectangle track, float value, Color accent, String label) {
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
        drawValue(g, value, track);
    }

    private void drawLabel(Graphics2D g, String text, int x, int y) {
        drawText(g, text.toLowerCase(), x, y, TEXT_SCALE);
    }

    private void drawValue(Graphics2D g, float value, Rectangle track) {
        int percent = Math.round(value * 100f);
        String text = String.format("%03d", percent);
        int charW = (int) (font.getCharWidth() * VALUE_SCALE);
        int textW = charW * text.length();
        int x = track.x + track.width - textW;
        int y = track.y + LABEL_OFFSET_Y;
        drawText(g, text, x, y, VALUE_SCALE);
    }

    /**
     * Draws the back button.
     */
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

    /**
     * Draws the reset music button.
     */
    private void drawResetButton(Graphics2D g) {
        g.setColor(new Color(50, 60, 80));
        g.fillRoundRect(resetButton.x, resetButton.y, resetButton.width, resetButton.height, 10, 10);
        g.setColor(new Color(230, 220, 190));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(resetButton.x, resetButton.y, resetButton.width, resetButton.height, 10, 10);

        if (font != null) {
            String text = "RESET";
            int textW = (int) (font.getCharWidth() * TEXT_SCALE) * text.length();
            int textH = (int) (font.getCharHeight() * TEXT_SCALE);
            int textX = resetButton.x + (resetButton.width - textW) / 2;
            int textY = resetButton.y + (resetButton.height - textH) / 2;
            font.draw(g, text, textX, textY, TEXT_SCALE);
        }
    }

    /**
     * Draws the custom music picker button and label.
     */
    private void drawPickButton(Graphics2D g) {
        g.setColor(new Color(60, 70, 95));
        g.fillRoundRect(pickButton.x, pickButton.y, pickButton.width, pickButton.height, 10, 10);
        g.setColor(new Color(230, 220, 190));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(pickButton.x, pickButton.y, pickButton.width, pickButton.height, 10, 10);

        if (font != null) {
            String text = "choose";
            int textW = (int) (font.getCharWidth() * TEXT_SCALE) * text.length();
            int textH = (int) (font.getCharHeight() * TEXT_SCALE);
            int textX = pickButton.x + (pickButton.width - textW) / 2;
            int textY = pickButton.y + (pickButton.height - textH) / 2;
            font.draw(g, text, textX, textY, TEXT_SCALE);

            if (selectedMusicLabel != null && !selectedMusicLabel.isEmpty()) {
                int labelY = pickButton.y + pickButton.height + 18;
                drawText(g, selectedMusicLabel, pickButton.x, labelY, 0.8f);
            }
        }
    }

    private void drawTitle(Graphics2D g) {
        int titleX = panelBounds.x + 24;
        int titleY = panelBounds.y + 24;
        drawText(g, "options", titleX, titleY, TITLE_SCALE);

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

    /**
     * Computes control rectangles based on virtual resolution.
     */
    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        int panelX = (screenW - PANEL_WIDTH) / 2;
        int panelY = (screenH - PANEL_HEIGHT) / 2;
        panelBounds.setBounds(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        int sliderX = panelX + (PANEL_WIDTH - SLIDER_WIDTH) / 2;
        int musicY = panelY + 90;
        int effectY = musicY + SLIDER_GAP;
        musicTrack.setBounds(sliderX, musicY, SLIDER_WIDTH, SLIDER_HEIGHT);
        effectTrack.setBounds(sliderX, effectY, SLIDER_WIDTH, SLIDER_HEIGHT);

        int pickX = panelX + (PANEL_WIDTH - PICK_BUTTON_WIDTH) / 2;
        int pickY = effectY + 60;
        pickButton.setBounds(pickX, pickY, PICK_BUTTON_WIDTH, PICK_BUTTON_HEIGHT);

        int backX = panelX + (PANEL_WIDTH - BACK_BUTTON_WIDTH) / 2;
        int backY = panelY + PANEL_HEIGHT - BACK_BUTTON_HEIGHT - 20;
        backButton.setBounds(backX, backY, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);

        int resetX = panelX + (PANEL_WIDTH - RESET_BUTTON_WIDTH) / 2;
        int resetY = pickY + PICK_BUTTON_HEIGHT + 40;
        resetButton.setBounds(resetX, resetY, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT);
    }

    /**
     * Opens a file chooser to select a custom music track.
     */
    private void pickMusicFile() {
        FileDialog dialog = new FileDialog(ScreenManager.getInstance().getFrame(), "Select Music", FileDialog.LOAD);
        dialog.setFile("*.wav;*.mp3");
        dialog.setFilenameFilter((dir, name) -> {
            if (name == null) {
                return false;
            }
            String lower = name.toLowerCase();
            return lower.endsWith(".wav") || lower.endsWith(".mp3");
        });
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        String directory = dialog.getDirectory();
        if (fileName != null && directory != null) {
            File file = new File(directory, fileName);
            audioPlayer.playSongFromFile(file);
            selectedMusicLabel = "custom";
        }
    }

    /**
     * Restores the default soundtrack.
     */
    private void resetToDefaultMusic() {
        audioPlayer.playSong("main_theme");
        selectedMusicLabel = "default";
    }

    /**
     * Returns true if the mouse event intersects the slider hit box.
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
     * Converts a mouse x-coordinate into a volume value and applies it.
     */
    private void updateSliderValue(Rectangle track, int mouseX) {
        float value = (mouseX - track.x) / (float) track.width;
        value = Math.clamp(value, 0.0f, 1.0f);
        if (dragTarget == DragTarget.MUSIC) {
            audioPlayer.setSongVolume(value);
        } else if (dragTarget == DragTarget.EFFECT) {
            audioPlayer.setEffectVolume(value);
        }
    }

    private enum DragTarget {
        NONE,
        MUSIC,
        EFFECT
    }
}
