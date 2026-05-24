package software.project.ui.layers;

import software.project.graphics.BitmapFont;
import software.project.graphics.BitmapFonts;
import software.project.graphics.ResourceManager;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Cistern;
import software.project.models.Plumber;
import software.project.ui.ScreenManager;
import software.project.ui.components.Panel;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import static software.project.graphics.Sprites.SIMPLE_PANEL;


import software.project.ui.components.GameButton;



import java.awt.*;

public class CisternPickupOverlay extends Layer {

    private static final float OVERLAY_SCALE = 2.5f;
    private static final int BASE_PANEL_PADDING = 20;
    private static final int BASE_ITEM_SIZE = 32;
    private static final int BASE_ITEM_GAP = 10;
    private static final float BASE_TEXT_SCALE = 0.8f;
    private static final float BASE_LABEL_SCALE = 0.6f;

    private final int panelPadding;
    private final int itemSize;
    private final int itemGap;
    private final float textScale;
    private final float labelScale;

    private final Plumber plumber;
    private final Cistern cistern;
    private final Panel backgroundPanel;
    private final BitmapFont font;

    private Sprite pumpSprite;
    private Sprite pipeSprite;

    // UI elements
    private GameButton confirmButton;
    private GameButton discardButton;

    // Selection state
    private boolean pumpSelected;
    private boolean pipeSelected;

    // Bounds for click detection
    private Rectangle pumpBounds;
    private Rectangle pipeBounds;

    // Callbacks
    private PickupListener listener;

    public interface PickupListener {
        /** Called when the player confirms the selection. */
        void onConfirm(boolean tookPump, boolean tookPipe);
        /** Called when the player discards/cancels. */
        void onDiscard();
    }

    public void setListener(PickupListener listener) {
        this.listener = listener;
    }

    public CisternPickupOverlay(Plumber plumber, Cistern cistern) {
        super(true, false); // blocks input to layers below
        this.plumber = plumber;
        this.cistern = cistern;

        // Scale UI elements
        this.panelPadding = (int) (BASE_PANEL_PADDING * OVERLAY_SCALE);
        this.itemSize = (int) (BASE_ITEM_SIZE * OVERLAY_SCALE);
        this.itemGap = (int) (BASE_ITEM_GAP * OVERLAY_SCALE);
        this.textScale = BASE_TEXT_SCALE * OVERLAY_SCALE;
        this.labelScale = BASE_LABEL_SCALE * OVERLAY_SCALE;

        SpriteManager sm = SpriteManager.getInstance();
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        pipeSprite = sm.getSprite(Sprites.SPRING_PIPE);
        font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MONO);

        backgroundPanel = new Panel(1.0f, 0, SIMPLE_PANEL);

        // Create Confirm and Discard buttons (using your button sprite sheet)
        SpriteSheet btnSheet = sm.getSpriteSheet(SpriteSheets.BUTTONS); // adjust sheet name if needed
        int btnSize = (int) (24 * OVERLAY_SCALE);
        confirmButton = new GameButton(btnSheet, 0, 0, 0, btnSize, btnSize);
        discardButton = new GameButton(btnSheet, 1, 0, 0, btnSize, btnSize);

        confirmButton.setAction(() -> finish(true));
        discardButton.setAction(() -> finish(false));

        pumpSelected = false;
        pipeSelected = false;
        pumpBounds = new Rectangle();
        pipeBounds = new Rectangle();
    }

    private void finish(boolean confirmed) {
        if (confirmed) {
            if (listener != null) {
                listener.onConfirm(pumpSelected, pipeSelected);
            }
        } else {
            if (listener != null) {
                listener.onDiscard();
            }
        }
        // Close the overlay – assuming the layer manager is a stack; override with your actual close mechanism
        close();
    }

    /**
     * Call this when the overlay should be removed from the layer stack.
     * Adjust according to your LayerManager implementation.
     */
    private void close() {
        // Typical pattern: get the layer manager and pop this layer
        // Example: LayerManager.getInstance().popLayer();
        // For now, we'll call onExit() which you can override to signal removal.
        onExit();
    }

    public void onExit() {
        // To be overridden or used by your layer system.
    }

    @Override
    public void onEnter() {
        // Reset selections every time the overlay opens
        pumpSelected = false;
        pipeSelected = false;
        recomputeLayout();
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void update(float deltaTime) {
        confirmButton.update();
        discardButton.update();
    }

    @Override
    public void render(Graphics2D g) {
        backgroundPanel.draw(g);

        boolean hasPump = cistern.getStoredPump() != null;
        boolean hasPipe = cistern.getStoredPipe() != null;

        int centreX = ScreenManager.getInstance().getVirtualWidth() / 2;
        int centreY = ScreenManager.getInstance().getVirtualHeight() / 2;

        // No items to pick up
        if (!hasPump && !hasPipe) {
            drawCenteredTextLarge(g, "NOTHING TO PICK UP", centreX, centreY);
            // Optionally show a single "OK" button to close
            // For simplicity, we'll just close after a short time or let the caller handle.
            // You could also add an "OK" button here.
            return;
        }

        // Calculate positions for the items
        int itemCount = (hasPump ? 1 : 0) + (hasPipe ? 1 : 0);
        int[] itemCentresX = new int[itemCount];
        if (itemCount == 1) {
            itemCentresX[0] = centreX;
        } else {
            int groupWidth = 2 * itemSize + itemGap;
            int groupLeft = centreX - groupWidth / 2 + itemSize / 2;
            itemCentresX[0] = groupLeft;
            itemCentresX[1] = groupLeft + itemSize + itemGap;
        }

        int itemTop = centreY - itemSize / 2;
        int labelY = itemTop + itemSize + panelPadding;
        int btnY = labelY + (int) (font.getCharHeight() * labelScale) + panelPadding;

        int slot = 0;
        if (hasPump) {
            int x = itemCentresX[slot];
            pumpBounds.setBounds(x - itemSize / 2, itemTop, itemSize, itemSize);
            // Draw pump
            pumpSprite.drawCentered(g, x, centreY, itemSize, 0);
            // Highlight if selected
            if (pumpSelected) {
                drawSelectionBorder(g, x - itemSize / 2, itemTop, itemSize, itemSize);
            }
            drawCenteredText(g, "PUMP", x, labelY);
            slot++;
        }
        if (hasPipe) {
            int x = itemCentresX[slot];
            pipeBounds.setBounds(x - itemSize / 2, itemTop, itemSize, itemSize);
            pipeSprite.drawCentered(g, x, centreY, itemSize, 0);
            if (pipeSelected) {
                drawSelectionBorder(g, x - itemSize / 2, itemTop, itemSize, itemSize);
            }
            drawCenteredText(g, "PIPE", x, labelY);
        }

        // Draw buttons
        int btnSize = confirmButton.getWidth();
        int btnGap = (int) (8 * OVERLAY_SCALE);
        confirmButton.setCenter(centreX - btnSize / 2 - btnGap, btnY + btnSize / 2);
        discardButton.setCenter(centreX + btnSize / 2 + btnGap, btnY + btnSize / 2);
        confirmButton.draw(g);
        discardButton.draw(g);
    }

    private void drawSelectionBorder(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1));
    }

    private void drawCenteredText(Graphics2D g, String text, int cx, int y) {
        if (font == null) return;
        int scaledCharW = (int) (font.getCharWidth() * labelScale);
        int textWidth = text.length() * scaledCharW;
        font.draw(g, text, cx - textWidth / 2, y, labelScale);
    }

    private void drawCenteredTextLarge(Graphics2D g, String text, int cx, int y) {
        if (font == null) return;
        int scaledCharW = (int) (font.getCharWidth() * textScale);
        int textWidth = text.length() * scaledCharW;
        font.draw(g, text, cx - textWidth / 2, y, textScale);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        confirmButton.mousePressed(e);
        discardButton.mousePressed(e);

        // Toggle selections
        Point p = e.getPoint();
        if (cistern.getStoredPump() != null && pumpBounds.contains(p)) {
            pumpSelected = !pumpSelected;
            // Optional: play a sound
            // AudioPlayer.getInstance().playEffect("ui_select");
            return true;
        }
        if (cistern.getStoredPipe() != null && pipeBounds.contains(p)) {
            pipeSelected = !pipeSelected;
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        confirmButton.mouseReleased(e);
        discardButton.mouseReleased(e);
        return true;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        confirmButton.mouseMoved(e);
        discardButton.mouseMoved(e);
        return true;
    }

    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        boolean hasPump = cistern.getStoredPump() != null;
        boolean hasPipe = cistern.getStoredPipe() != null;
        int itemCount = (hasPump ? 1 : 0) + (hasPipe ? 1 : 0);

        int labelH = (int) (font.getCharHeight() * labelScale);
        int btnSize = confirmButton.getWidth(); // both buttons same size

        int panelW = 2 * itemSize + itemGap + 2 * panelPadding;
        // If only one item, we can reduce width a bit, but keeping symmetric is fine
        if (itemCount == 1) {
            panelW = itemSize + 2 * panelPadding;
        }

        int panelH = itemSize + labelH + panelPadding + btnSize + panelPadding + panelPadding;

        backgroundPanel.setPosition(screenW / 2 - panelW / 2, screenH / 2 - panelH / 2);
        backgroundPanel.setSize(panelW, panelH);
    }
}