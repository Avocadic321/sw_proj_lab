package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.BitmapFonts;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;
import software.project.graphics.SpriteManager;
import java.awt.*;

public class ScoreElement extends HudElement {
    private static final int MARGIN = 15;
    private static final int SCORE_GAP = 10;
    private static final int OUTLINE_PADDING = 4;
    private static final int OUTLINE_RADIUS = 10;
    private static final Color PLUMBER_COLOR = new Color(70, 130, 220);
    private static final Color SABOTEUR_COLOR = new Color(200, 70, 70);

    private final Banner plumberBanner;
    private final Banner saboteurBanner;
    private final GameModel model;
    private final int goalScore;

    public ScoreElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
        this.goalScore = model.getConfig().getGoalScore();
        var scoreSprite = SpriteManager.getInstance().getSprite(Sprites.PAPER_BANNER);
        plumberBanner = new Banner(scoreSprite, 1f, BitmapFonts.FONT_MONO, "P0/XXX", 1.1f);
        saboteurBanner = new Banner(scoreSprite, 1f, BitmapFonts.FONT_MONO, "S0/XXX", 1.1f);
        reposition();
    }

    private void reposition() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int w = plumberBanner.getWidth();
        int h = plumberBanner.getHeight();
        int x = screenW - MARGIN - w;
        plumberBanner.setPosition(x, MARGIN);
        saboteurBanner.setPosition(x, MARGIN + h + SCORE_GAP);
        this.x = x;
        this.y = MARGIN;
        this.width = w;
        this.height = h * 2 + SCORE_GAP;
    }

    @Override
    public void update(float deltaTime) {
        int plumberScore = model.getPlumbersTeam() == null ? 0 : model.getPlumbersTeam().getScore();
        int saboteurScore = model.getSaboteursTeam() == null ? 0 : model.getSaboteursTeam().getScore();
        plumberBanner.setText("P" + plumberScore + "/" + goalScore);
        saboteurBanner.setText("S" + saboteurScore + "/" + goalScore);
    }

    @Override
    public void draw(Graphics2D g) {
        drawBackdrop(g, plumberBanner, PLUMBER_COLOR);
        drawBackdrop(g, saboteurBanner, SABOTEUR_COLOR);
        plumberBanner.draw(g);
        saboteurBanner.draw(g);
    }

    private void drawBackdrop(Graphics2D g, Banner banner, Color color) {
        int x = banner.getX() - OUTLINE_PADDING;
        int y = banner.getY() - OUTLINE_PADDING;
        int w = banner.getWidth() + 2 * OUTLINE_PADDING;
        int h = banner.getHeight() + 2 * OUTLINE_PADDING;
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
        g.fillRoundRect(x, y, w, h, OUTLINE_RADIUS, OUTLINE_RADIUS);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, OUTLINE_RADIUS, OUTLINE_RADIUS);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        reposition();
    }
}