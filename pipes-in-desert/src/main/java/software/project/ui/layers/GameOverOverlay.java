package software.project.ui.layers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import software.project.audio.AudioPlayer;
import software.project.core.GameModel;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.Sprites;
import software.project.models.Team;
import software.project.models.Teams;
import software.project.ui.ScreenManager;

public class GameOverOverlay extends Layer {
    private static final int PANEL_WIDTH = 520;
    private static final int PANEL_HEIGHT = 420;
    private static final int PANEL_RADIUS = 18;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 46;

    private static final float TITLE_SCALE = 1.4f;
    private static final float WINNER_SCALE = 1.15f;
    private static final float SCORE_SCALE = 1.0f;
    private static final float BUTTON_TEXT_SCALE = 1.0f;
    private static final int SCORE_SEGMENT_GAP = 16;
    private static final int SCORE_BAR_HEIGHT = 6;
    private static final int SCORE_BAR_OFFSET = 6;

    private static final Color PANEL_FILL = new Color(20, 25, 35, 130);
    private static final Color PANEL_SHADOW = new Color(0, 0, 0, 120);
    private static final Color PANEL_STROKE = new Color(240, 220, 180, 200);

    private static final Color BUTTON_FILL = new Color(60, 70, 95);
    private static final Color BUTTON_HOVER = new Color(80, 90, 120);
    private static final Color BUTTON_STROKE = new Color(230, 220, 190);
    private static final Color PLUMBER_COLOR = new Color(70, 130, 220);
    private static final Color SABOTEUR_COLOR = new Color(200, 70, 70);

    private final BitmapFont titleFont;
    private final BitmapFont scoreFont;
    private final Sprite winnerSprite;
    private final String winnerText;
    private final String plumberScoreText;
    private final String saboteurScoreText;

    private final Rectangle panelBounds = new Rectangle();
    private final Rectangle buttonBounds = new Rectangle();
    private boolean buttonHover;

    private Runnable mainMenuAction = () -> {
    };

    public GameOverOverlay(GameModel model) {
        super(true, true);
        this.titleFont = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        this.scoreFont = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MONO);

        int plumberScore = getScore(model == null ? null : model.getPlumbersTeam());
        int saboteurScore = getScore(model == null ? null : model.getSaboteursTeam());
        int goal = model == null ? 0 : model.getConfig().getGoalScore();

        Teams winner = plumberScore >= saboteurScore ? Teams.PLUMBERS : Teams.SABOTEURS;
        SpriteManager sm = SpriteManager.getInstance();
        this.winnerSprite = sm.getSprite(winner == Teams.PLUMBERS ? Sprites.PLUMBER : Sprites.SABOTEUR);
        this.winnerText = winner == Teams.PLUMBERS ? "plumbers win" : "saboteurs win";
        this.plumberScoreText = String.format("P%d/%d", plumberScore, goal);
        this.saboteurScoreText = String.format("S%d/%d", saboteurScore, goal);

        recomputeLayout();
    }

    public void setMainMenuAction(Runnable mainMenuAction) {
        this.mainMenuAction = mainMenuAction == null ? () -> {
        } : mainMenuAction;
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void render(Graphics2D g) {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, screenW, screenH);

        g.setColor(PANEL_SHADOW);
        g.fillRoundRect(panelBounds.x + 6, panelBounds.y + 8, panelBounds.width, panelBounds.height,
                PANEL_RADIUS, PANEL_RADIUS);
        g.setColor(PANEL_FILL);
        g.fillRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height,
                PANEL_RADIUS, PANEL_RADIUS);
        g.setColor(PANEL_STROKE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height,
                PANEL_RADIUS, PANEL_RADIUS);

        int centerX = panelBounds.x + panelBounds.width / 2;
        int titleY = panelBounds.y + 32;
        int winnerY = titleY + 36;
        int scoreY = buttonBounds.y - 28;

        drawCenteredText(g, titleFont, "game over", centerX, titleY, TITLE_SCALE);
        drawCenteredText(g, titleFont, winnerText, centerX, winnerY, WINNER_SCALE);

        int spriteAreaTop = winnerY + 20;
        int spriteAreaBottom = scoreY - 10;
        int spriteCenterY = (spriteAreaTop + spriteAreaBottom) / 2;
        int spriteSize = Math.min(panelBounds.width / 2, spriteAreaBottom - spriteAreaTop);
        spriteSize = Math.max(spriteSize, 64);
        if (winnerSprite != null) {
            winnerSprite.drawCentered(g, centerX, spriteCenterY, spriteSize, 0);
        }

        drawScoreLine(g, centerX, scoreY);

        g.setColor(buttonHover ? BUTTON_HOVER : BUTTON_FILL);
        g.fillRoundRect(buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, 10, 10);
        g.setColor(BUTTON_STROKE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, 10, 10);
        drawCenteredText(g, titleFont, "main menu", centerX,
                buttonBounds.y + buttonBounds.height / 2, BUTTON_TEXT_SCALE);
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        buttonHover = buttonBounds.contains(e.getPoint());
        return true;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (buttonBounds.contains(e.getPoint())) {
            AudioPlayer.getInstance().playEffect("button_pressed");
            mainMenuAction.run();
            return true;
        }
        return true;
    }

    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        int panelX = (screenW - PANEL_WIDTH) / 2;
        int panelY = (screenH - PANEL_HEIGHT) / 2;
        panelBounds.setBounds(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        int buttonX = panelX + (PANEL_WIDTH - BUTTON_WIDTH) / 2;
        int buttonY = panelY + PANEL_HEIGHT - BUTTON_HEIGHT - 20;
        buttonBounds.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    private void drawCenteredText(Graphics2D g, BitmapFont font, String text, int centerX, int y, float scale) {
        if (font == null || text == null) {
            return;
        }
        int textW = (int) (font.getCharWidth() * scale) * text.length();
        int textH = (int) (font.getCharHeight() * scale);
        int drawX = centerX - textW / 2;
        int drawY = y - textH / 2;
        font.draw(g, text, drawX, drawY, scale);
    }

    private void drawScoreLine(Graphics2D g, int centerX, int y) {
        if (scoreFont == null || plumberScoreText == null || saboteurScoreText == null) {
            return;
        }

        int charW = (int) (scoreFont.getCharWidth() * SCORE_SCALE);
        int charH = (int) (scoreFont.getCharHeight() * SCORE_SCALE);
        int plumberW = plumberScoreText.length() * charW;
        int saboteurW = saboteurScoreText.length() * charW;
        int totalW = plumberW + SCORE_SEGMENT_GAP + saboteurW;
        int startX = centerX - totalW / 2;
        int drawY = y - charH / 2;

        scoreFont.draw(g, plumberScoreText, startX, drawY, SCORE_SCALE);
        scoreFont.draw(g, saboteurScoreText, startX + plumberW + SCORE_SEGMENT_GAP, drawY, SCORE_SCALE);

        int barY = drawY + charH + SCORE_BAR_OFFSET;
        g.setColor(PLUMBER_COLOR);
        g.fillRoundRect(startX, barY, plumberW, SCORE_BAR_HEIGHT, 6, 6);
        g.setColor(SABOTEUR_COLOR);
        g.fillRoundRect(startX + plumberW + SCORE_SEGMENT_GAP, barY, saboteurW, SCORE_BAR_HEIGHT, 6, 6);
    }

    private int getScore(Team team) {
        return team == null ? 0 : team.getScore();
    }
}
