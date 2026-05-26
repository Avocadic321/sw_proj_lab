package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.ui.ScreenManager;
import java.awt.Graphics2D;

public class ActionStatusElement extends HudElement {
    private static final int MARGIN = 15;
    private static final float TEXT_SCALE = 0.9f;

    private final GameModel model;
    private final BitmapFont font;

    public ActionStatusElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
    }

    @Override
    public void draw(Graphics2D g) {
        if (font == null) return;

        boolean smallLeft = model.getTurnManager().canUseSmallAction();
        boolean bigLeft = model.getTurnManager().canUseBigAction();
        String text = "Move is " + (smallLeft ? "available" : "used")
            + "  Action is " + (bigLeft ? "available" : "used");

        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();
        int textW = (int)(font.getCharWidth() * TEXT_SCALE) * text.length();
        int textH = (int)(font.getCharHeight() * TEXT_SCALE);
        int x = screenW - MARGIN - textW;
        int y = screenH - MARGIN - textH;

        font.draw(g, text, x, y, TEXT_SCALE);
    }
}