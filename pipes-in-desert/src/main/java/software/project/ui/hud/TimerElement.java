package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;

import java.awt.Color;
import java.awt.Graphics2D;

public class TimerElement extends HudElement {
    // Positioning constants
    private static final int MARGIN_TOP = 10;
    private static final int SCORE_SPACING = 160;      // Gap between scores
    private static final int TIMER_TEXT_OFFSET_Y = 4;   // Timer text offset inside banner (downward)

    // Banner scaling for timer
    private static final float TIMER_BANNER_SCALE = 0.6f;
    private static final float TIMER_TEXT_SCALE = 1.5f;

    // Text scaling for scores
    private static final float SCORE_TEXT_SCALE = 1.3f;
    private static final Color SCORE_PLUMBER_COLOR = new Color(70, 130, 220);
    private static final Color SCORE_SABOTEUR_COLOR = new Color(200, 70, 70);

    private final GameModel model;
    private final BitmapFont monoFont;

    private final Banner timerBanner;
    private final int goalScore;

    // Fixed width for score text (using max possible width)
    private int fixedTextWidth;
    private int scoreY;

    public TimerElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
        this.goalScore = model.getConfig().getGoalScore();
        this.monoFont = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MONO);

        Sprite scoreboardSprite = SpriteManager.getInstance().getSprite(Sprites.SCOREBOARD);
        if (scoreboardSprite == null) {
            throw new IllegalStateException("SCOREBOARD sprite missing");
        }

        // Timer uses the banner
        timerBanner = new Banner(scoreboardSprite, TIMER_BANNER_SCALE, BitmapFonts.FONT_MONO, "00:00", TIMER_TEXT_SCALE);

        // Apply text offset inside the banner
        timerBanner.setTextOffset(0, TIMER_TEXT_OFFSET_Y);

        // Calculate fixed width using the maximum possible score (goalScore has max digits)
        String maxScoreText = goalScore + "/" + goalScore;
        fixedTextWidth = (int)(maxScoreText.length() * monoFont.getCharWidth() * SCORE_TEXT_SCALE);

        reposition();
    }

    private void reposition() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int centreX = screenW / 2;
        int baseY = MARGIN_TOP;

        // Timer - centered (banner position stays the same, only text offset moves inside)
        int timerX = centreX - timerBanner.getWidth() / 2;
        timerBanner.setPosition(timerX, baseY);

        // Calculate Y position for scores (vertically centered with timer banner)
        scoreY = baseY + (timerBanner.getHeight() - (int)(monoFont.getCharHeight() * SCORE_TEXT_SCALE)) / 2;

        // Update component bounds for HudElement
        this.x = 0;
        this.y = baseY;
        this.width = screenW;
        this.height = timerBanner.getHeight();
    }

    @Override
    public void update(float deltaTime) {
        // Timer
        int timeLeft = model.getTurnManager().getTimeLeft();
        int mins = timeLeft / 60;
        int secs = timeLeft % 60;
        timerBanner.setText(String.format("%02d:%02d", mins, secs));
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw timer banner
        timerBanner.draw(g);

        // Draw scores as plain text
        if (monoFont == null) return;

        int plumberScore = model.getPlumbersTeam() == null ? 0 : model.getPlumbersTeam().getScore();
        int saboteurScore = model.getSaboteursTeam() == null ? 0 : model.getSaboteursTeam().getScore();

        String plumberText = plumberScore + "/" + goalScore;
        String saboteurText = saboteurScore + "/" + goalScore;

        int centreX = ScreenManager.getInstance().getVirtualWidth() / 2;

        // Calculate actual text widths
        int plumberActualWidth = (int)(plumberText.length() * monoFont.getCharWidth() * SCORE_TEXT_SCALE);
        int saboteurActualWidth = (int)(saboteurText.length() * monoFont.getCharWidth() * SCORE_TEXT_SCALE);

        // Use fixed width for positioning
        int plumberLeftEdge = centreX - SCORE_SPACING / 2 - fixedTextWidth;
        int plumberDrawX = plumberLeftEdge + (fixedTextWidth - plumberActualWidth);

        int saboteurLeftEdge = centreX + SCORE_SPACING / 2;
        int saboteurDrawX = saboteurLeftEdge;

        // Draw scores with colors
        g.setColor(SCORE_PLUMBER_COLOR);
        monoFont.draw(g, plumberText, plumberDrawX, scoreY, SCORE_TEXT_SCALE);

        g.setColor(SCORE_SABOTEUR_COLOR);
        monoFont.draw(g, saboteurText, saboteurDrawX, scoreY, SCORE_TEXT_SCALE);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        reposition();
    }
}