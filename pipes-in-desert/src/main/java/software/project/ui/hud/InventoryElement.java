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

    // Position constants - consistent margin from corner
    private static final int MARGIN = 15;  // Same from left and bottom

    // Slot constants - made bigger
    private static final int SLOT_SIZE = 44;      // Increased from 40
    private static final int ICON_SIZE = 40;      // Increased from 32

    // Sprite scaling - adjust these to fit your INVENTORY sprite
    private static final float INVENTORY_SPRITE_SCALE = 1.2f;  // Slightly bigger panel
    private static final int INVENTORY_BASE_WIDTH = 124;       // Original sprite width
    private static final int INVENTORY_BASE_HEIGHT = 84;       // Original sprite height

    // Position adjustments
    private static final int VERTICAL_OFFSET = 0;       // No extra offset, panel will be MARGIN from bottom
    private static final int SLOT_SPACING = 6;           // Increased spacing between slots

    // Slot offset adjustment (fine-tuning)
    private static final int SLOT_START_OFFSET_X = 20;   // Adjusted for larger slots
    private static final int SLOT_START_OFFSET_Y = 28;   // Adjusted for larger slots
    private static final int SLOT_HORIZONTAL_GAP = 10;   // Increased base gap between slots

    // Flags
    private static final boolean DRAW_SLOT_BACKGROUND = false;  // Set to true to draw gray slot backgrounds

    private final GameModel model;
    private final Sprite inventorySprite;
    private final Sprite pumpSprite;
    private final Sprite pipeSprite;
    private Rectangle panelBounds = new Rectangle();
    private Rectangle[] slotBounds = new Rectangle[0];
    private DragListener dragListener;

    // Cached scaled dimensions
    private int scaledWidth;
    private int scaledHeight;

    public InventoryElement(GameModel model, DragListener listener) {
        super(0, 0, 0, 0);
        this.model = model;
        this.dragListener = listener;
        var sm = SpriteManager.getInstance();

        // Use the dedicated INVENTORY sprite
        this.inventorySprite = sm.getSprite(Sprites.INVENTORY);
        this.pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        this.pipeSprite = pipeSheet != null ? pipeSheet.getSprite(0) : null;

        // Calculate scaled dimensions
        this.scaledWidth = (int)(INVENTORY_BASE_WIDTH * INVENTORY_SPRITE_SCALE);
        this.scaledHeight = (int)(INVENTORY_BASE_HEIGHT * INVENTORY_SPRITE_SCALE);

        recomputeLayout();
    }

    private void recomputeLayout() {
        Player current = model.getTurnManager().getCurrentPlayer();
        if (!(current instanceof Plumber plumber)) {
            width = 0; height = 0;
            return;
        }

        int slots = plumber.getInventory().getInventory().length;

        int panelW = scaledWidth;
        int panelH = scaledHeight;
        int panelX = MARGIN;  // MARGIN from left edge
        int panelY = ScreenManager.getInstance().getVirtualHeight() - MARGIN - panelH + VERTICAL_OFFSET;  // MARGIN from bottom

        panelBounds.setBounds(panelX, panelY, panelW, panelH);
        this.x = panelX;
        this.y = panelY;
        this.width = panelW;
        this.height = panelH;

        // Calculate slot positions within the fixed panel
        slotBounds = new Rectangle[slots];

        int totalGap = SLOT_HORIZONTAL_GAP + SLOT_SPACING;
        int totalSlotsWidth = slots * SLOT_SIZE + (slots - 1) * totalGap;
        int startSlotX = panelBounds.x + (panelBounds.width - totalSlotsWidth) / 2;
        int slotY = panelBounds.y + SLOT_START_OFFSET_Y;

        for (int i = 0; i < slots; i++) {
            slotBounds[i] = new Rectangle(startSlotX + i * (SLOT_SIZE + totalGap), slotY, SLOT_SIZE, SLOT_SIZE);
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

        // Draw the INVENTORY sprite
        if (inventorySprite != null) {
            inventorySprite.draw(g, panelBounds.x, panelBounds.y, scaledWidth, scaledHeight);
        }

        // Draw slots and items
        for (int i = 0; i < slotBounds.length; i++) {
            Rectangle slot = slotBounds[i];

            // Only draw gray background if flag is true
            if (DRAW_SLOT_BACKGROUND) {
                g.setColor(new Color(0, 0, 0, 90));
                g.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);
                g.setColor(new Color(255, 255, 255, 120));
                g.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);
            }

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