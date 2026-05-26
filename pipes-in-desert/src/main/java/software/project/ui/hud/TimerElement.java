package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.BitmapFonts;
import software.project.graphics.Sprites;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;
import software.project.graphics.SpriteManager;

import java.awt.Graphics2D;

public class TimerElement extends HudElement {
    private static final int MARGIN = 15;
    private final Banner banner;
    private final GameModel model;

    public TimerElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
        var sprite = SpriteManager.getInstance().getSprite(Sprites.TIMER_BANNER);
        banner = new Banner(sprite, 1f, BitmapFonts.FONT_MAIN, "T0000", 1.1f);
        reposition();
    }

    private void reposition() {
        banner.setPosition(MARGIN, MARGIN);
        this.x = banner.getX();
        this.y = banner.getY();
        this.width = banner.getWidth();
        this.height = banner.getHeight();
    }

    @Override
    public void update(float deltaTime) {
        int timeLeft = model.getTurnManager().getTimeLeft();
        int mins = timeLeft / 60;
        int secs = timeLeft % 60;
        banner.setText(String.format("T%02d%02d", mins, secs));
    }

    @Override
    public void draw(Graphics2D g) {
        banner.draw(g);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        reposition();
    }
}