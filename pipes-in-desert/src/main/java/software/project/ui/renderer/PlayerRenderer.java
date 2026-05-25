package software.project.ui.renderer;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.Sprites;
import software.project.map.Element;
import software.project.models.Player;
import software.project.models.Saboteur;
import software.project.models.Team;

import java.awt.*;

public class PlayerRenderer {
    public static final float PLAYER_SCALE = 0.6f;

    private final SpriteManager sm = SpriteManager.getInstance();

    /**
     * Draws all players except the current one.
     */
    public void drawPlayers(Graphics2D g, Team saboteurs, Team plumbers, Player currentPlayer, Grid grid) {
        int playerSize = (int) (grid.getTileSize() * PLAYER_SCALE);
        Sprite sabSprite = sm.getSprite(Sprites.SABOTEUR);
        Sprite plumSprite = sm.getSprite(Sprites.PLUMBER);

        // Draw saboteurs
        for (Player p : saboteurs.getPlayers()) {
            if (p == currentPlayer) continue;
            Element pos = p.getCurrentPosition();
            if (pos == null) continue;
            Point center = grid.getCellCenter(pos.getX(), pos.getY());
            sabSprite.drawCentered(g, center.x, center.y, playerSize, 0);
        }

        // Draw plumbers
        for (Player p : plumbers.getPlayers()) {
            if (p == currentPlayer) continue;
            Element pos = p.getCurrentPosition();
            if (pos == null) continue;
            Point center = grid.getCellCenter(pos.getX(), pos.getY());
            plumSprite.drawCentered(g, center.x, center.y, playerSize, 0);
        }
    }

    /**
     * Draws a player at an arbitrary centre point (used for movement animation).
     */
    public void drawPlayerAt(Graphics2D g, Player player, Point center, int tileSize) {
        int playerSize = (int) (tileSize * PLAYER_SCALE);
        boolean isSaboteur = player instanceof Saboteur;
        Sprite sprite = isSaboteur ? sm.getSprite(Sprites.SABOTEUR) : sm.getSprite(Sprites.PLUMBER);
        if (sprite != null) {
            sprite.drawCentered(g, center.x, center.y, playerSize, 0);
        }
    }

    /**
     * Draws the current player with an arrow above.
     */
    public void drawCurrentPlayer(Graphics2D g, Player currentPlayer, Grid grid, float arrowTick) {
        if (currentPlayer == null) return;
        Element pos = currentPlayer.getCurrentPosition();
        if (pos == null) return;

        int playerSize = (int) (grid.getTileSize() * PLAYER_SCALE);
        Point center = grid.getCellCenter(pos.getX(), pos.getY());

        boolean isSaboteur = currentPlayer instanceof Saboteur;
        Sprite sprite = isSaboteur ? sm.getSprite(Sprites.SABOTEUR) : sm.getSprite(Sprites.PLUMBER);
        if (sprite != null) {
            sprite.drawCentered(g, center.x, center.y, playerSize, 0);
        }

        drawArrow(g, center, playerSize, arrowTick);
    }

    /**
     * Draws a bouncing arrow above a player.
     */
    public void drawArrow(Graphics2D g, Point center, int playerSize, float arrowTick) {
        int bounceOffset = (int) (Math.sin(arrowTick * 6.0) * 6 + 6);
        int tipX = center.x;
        int tipY = center.y - playerSize / 2 - 6 - bounceOffset;
        int arrowW = playerSize / 4;
        int arrowH = playerSize / 4;

        int[] xPoints = {tipX, tipX + arrowW, tipX - arrowW};
        int[] yPoints = {tipY, tipY - arrowH, tipY - arrowH};

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 220, 0));
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(xPoints, yPoints, 3);
    }
}