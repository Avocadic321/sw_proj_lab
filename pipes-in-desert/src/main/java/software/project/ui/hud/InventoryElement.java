package software.project.ui.hud;

import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.ui.ScreenManager;
import java.awt.*;
import java.awt.event.MouseEvent;

public class InventoryElement extends HudElement {
    public interface DragListener {
        void onDragStart(ICarriable item, int slotIndex, Point screenPos);
    }

    private static final int MARGIN = 15;
    private static final int PANEL_PADDING = 8;
    private static final int SLOT_SIZE = 80;
    private static final int ICON_SIZE = 50;

    private final GameModel model;
    private final Sprite panelSprite;
    private final Sprite pumpSprite;
    private final Sprite pipeSprite;
    private Rectangle panelBounds = new Rectangle();
    private Rectangle[] slotBounds = new Rectangle[0];
    private DragListener dragListener;

    public InventoryElement(GameModel model, DragListener listener) {
        super(0, 0, 0, 0);
        this.model = model;
        this.dragListener = listener;
        var sm = SpriteManager.getInstance();
        panelSprite = sm.getSprite(Sprites.SIMPLE_PANEL);
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        pipeSprite = pipeSheet != null ? pipeSheet.getSprite(0) : null;
        recomputeLayout();
    }

    private void recomputeLayout() {
        Player current = model.getTurnManager().getCurrentPlayer();
        if (!(current instanceof Plumber plumber)) {
            width = 0; height = 0;
            return;
        }
        int slots = plumber.getInventory().getInventory().length;
        int panelW = slots * SLOT_SIZE + 2 * PANEL_PADDING;
        int panelH = SLOT_SIZE + 2 * PANEL_PADDING;
        int panelX = MARGIN;
        int panelY = ScreenManager.getInstance().getVirtualHeight() - MARGIN - panelH;
        panelBounds.setBounds(panelX, panelY, panelW, panelH);
        this.x = panelX;
        this.y = panelY;
        this.width = panelW;
        this.height = panelH;

        slotBounds = new Rectangle[slots];
        int slotX = panelBounds.x + PANEL_PADDING;
        int slotY = panelBounds.y + PANEL_PADDING;
        for (int i = 0; i < slots; i++) {
            slotBounds[i] = new Rectangle(slotX, slotY, SLOT_SIZE, SLOT_SIZE);
            slotX += SLOT_SIZE;
        }
    }

    @Override
    public void update(float deltaTime) {
        Player current = model.getTurnManager().getCurrentPlayer();
        if (current instanceof Plumber plumber) {
            int slots = plumber.getInventory().getInventory().length;
            if (slots != slotBounds.length) recomputeLayout();
        } else {
            if (slotBounds.length > 0) recomputeLayout();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Player current = model.getTurnManager().getCurrentPlayer();
        if (!(current instanceof Plumber plumber)) return;

        if (panelSprite != null) {
            panelSprite.draw(g, panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        } else {
            g.setColor(new Color(10, 10, 10, 140));
            g.fillRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, 12, 12);
            g.setColor(new Color(230, 210, 160, 180));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, 12, 12);
        }

        for (int i = 0; i < slotBounds.length; i++) {
            Rectangle slot = slotBounds[i];
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);
            g.setColor(new Color(255, 255, 255, 120));
            g.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);

            ICarriable item = plumber.getInventory().get(i);
            if (item instanceof software.project.map.Pump) {
                drawIcon(g, pumpSprite, slot);
            } else if (item instanceof software.project.map.Pipe) {
                drawIcon(g, pipeSprite, slot);
            }
        }
    }

    private void drawIcon(Graphics2D g, Sprite sprite, Rectangle slot) {
        if (sprite == null) return;
        int cx = slot.x + slot.width / 2;
        int cy = slot.y + slot.height / 2;
        sprite.drawCentered(g, cx, cy, ICON_SIZE, 0);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        Player current = model.getTurnManager().getCurrentPlayer();
        if (!(current instanceof Plumber plumber)) return false;
        for (int i = 0; i < slotBounds.length; i++) {
            if (slotBounds[i].contains(e.getPoint())) {
                ICarriable item = plumber.getInventory().get(i);
                if (item != null && dragListener != null) {
                    dragListener.onDragStart(item, i, e.getPoint());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }
}