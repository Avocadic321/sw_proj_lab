package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.map.*;
import software.project.models.*;
import software.project.ui.ScreenManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ActionHintElement extends HudElement {
    private static final int MARGIN = 15;
    private static final int SCORE_GAP = 10;
    private static final int HINT_KEY_SIZE = 40;
    private static final int HINT_GAP = 8;
    private static final int HINT_PADDING = 8;
    private static final float HINT_TEXT_SCALE = 1f;
    private static final Color PLUMBER_COLOR = new Color(70, 130, 220);
    private static final Color SABOTEUR_COLOR = new Color(200, 70, 70);

    private final GameModel model;
    private final BitmapFont font;
    private List<Hint> currentHints = new ArrayList<>();

    public ActionHintElement(GameModel model) {
        super(0, 0, 0, 0);
        this.model = model;
        this.font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
    }

    @Override
    public void update(float deltaTime) {
        Player current = model.getTurnManager().getCurrentPlayer();
        currentHints = buildHints(current);
    }

    @Override
    public void draw(Graphics2D g) {
        if (currentHints.isEmpty() || font == null) return;

        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        int textH = (int)(font.getCharHeight() * HINT_TEXT_SCALE);
        int blockH = Math.max(HINT_KEY_SIZE, textH) + 2 * HINT_PADDING;
        int maxTextW = 0;
        for (Hint hint : currentHints) {
            int tw = (int)(font.getCharWidth() * HINT_TEXT_SCALE) * hint.action.length();
            if (tw > maxTextW) maxTextW = tw;
        }
        int blockW = HINT_KEY_SIZE + HINT_GAP + maxTextW + 2 * HINT_PADDING;
        int totalH = currentHints.size() * blockH + (currentHints.size() - 1) * HINT_GAP;
        int x = screenW - MARGIN - blockW;
        int y = Math.max(120, (screenH - totalH) / 2);

        for (int i = 0; i < currentHints.size(); i++) {
            Hint hint = currentHints.get(i);
            int blockY = y + i * (blockH + HINT_GAP);
            Color accent = hint.teamColor;
            Color keyFill = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120);
            Color keyStroke = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 220);

            int keyX = x + HINT_PADDING;
            int keyY = blockY + HINT_PADDING + (blockH - 2*HINT_PADDING - HINT_KEY_SIZE)/2;
            g.setColor(keyFill);
            g.fillRoundRect(keyX, keyY, HINT_KEY_SIZE, HINT_KEY_SIZE, 6, 6);
            g.setColor(keyStroke);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(keyX, keyY, HINT_KEY_SIZE, HINT_KEY_SIZE, 6, 6);

            int keyTextX = keyX + (HINT_KEY_SIZE - (int)(font.getCharWidth() * HINT_TEXT_SCALE))/2;
            int keyTextY = keyY + (HINT_KEY_SIZE - textH)/2;
            font.draw(g, hint.key, keyTextX, keyTextY, HINT_TEXT_SCALE);

            int actionX = keyX + HINT_KEY_SIZE + HINT_GAP;
            int actionY = blockY + HINT_PADDING + (blockH - 2*HINT_PADDING - textH)/2;
            font.draw(g, hint.action, actionX, actionY, HINT_TEXT_SCALE);
        }
    }

    private List<Hint> buildHints(Player current) {
        List<Hint> hints = new ArrayList<>();
        if (current == null) return hints;
        Element pos = current.getCurrentPosition();
        if (pos == null) return hints;

        boolean isPlumber = current instanceof Plumber;
        boolean isSaboteur = current instanceof Saboteur;
        Color teamColor = isPlumber ? PLUMBER_COLOR : SABOTEUR_COLOR;

        hints.add(new Hint("s", "skip turn", teamColor));

        if (isPlumber) {
            if (pos instanceof Cistern) {
                hints.add(new Hint("q", "pickup", teamColor));
            }
            if (pos instanceof Pump pump) {
                if (pump.isBroken()) hints.add(new Hint("f", "repair", teamColor));
                else hints.add(new Hint("d", "direction", teamColor));
            }
            if (pos instanceof Pipe pipe && pipe.isBroken()) {
                hints.add(new Hint("f", "repair", teamColor));
            }
            if (pos instanceof Pipe pipe && ((Plumber) current).canSplit(pipe) != null) {
                hints.add(new Hint("m", "split pipe", teamColor));
            }
        } else if (isSaboteur) {
            if (pos instanceof Pipe pipe && !pipe.isBroken()) {
                hints.add(new Hint("f", "sabotage", teamColor));
            }
            if (pos instanceof Pump pump) {
                hints.add(new Hint("d", "direction", teamColor));
                if (!pump.isBroken()) hints.add(new Hint("f", "sabotage", teamColor));
            }
        }
        return hints;
    }

    private record Hint(String key, String action, Color teamColor) {}
}