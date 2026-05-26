package software.project.ui.layers;

import software.project.audio.AudioPlayer;
import software.project.core.GameModel;
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
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.components.Panel;
import software.project.ui.components.Banner;
import software.project.ui.components.GameButton;
import software.project.utils.Constants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static software.project.graphics.Sprites.SIMPLE_PANEL;

public class CisternPickupOverlay extends Layer implements PropertyChangeListener {

    private static final float OVERLAY_SCALE = 2.5f;
    private static final int BASE_PANEL_PADDING = 24;
    private static final int BASE_ITEM_SIZE = 32;
    private static final int BASE_ITEM_GAP = 10;
    private static final int BASE_GAP_ITEM_LABEL = 8;
    private static final int BASE_GAP_LABEL_BUTTONS = 20;
    private static final float BASE_TEXT_SCALE = 0.7f;
    private static final float BASE_LABEL_SCALE = 0.5f;

    private static final int BUTTON_GAP = 30;
    private static final int BUTTON_BOTTOM_MARGIN = 0;

    private final int panelPadding;
    private final int itemSize;
    private final int itemGap;
    private final int gapItemLabel;
    private final int gapLabelButtons;
    private final float textScale;
    private final float labelScale;

    private final Plumber plumber;
    private final Cistern cistern;
    private final Panel backgroundPanel;
    private final BitmapFont font;
    private Banner titleBanner;

    private Sprite pumpSprite;
    private SpriteSheet pipeSpriteSheet;
    private Sprite bannerSprite;

    private GameButton confirmButton;
    private GameButton cancelButton;
    private GameButton closeButton;

    private boolean pumpSelected;
    private boolean pipeSelected;

    private Rectangle pumpBounds;
    private Rectangle pipeBounds;

    private GameModel gameModel;
    private GameApplication app;
    private boolean hasClosed;

    private PickupListener listener;

    public interface PickupListener {
        void onConfirm(boolean tookPump, boolean tookPipe);
        void onDiscard();
    }

    public void setListener(PickupListener listener) {
        this.listener = listener;
    }

    public CisternPickupOverlay(Plumber plumber, Cistern cistern, GameModel model, GameApplication app) {
        super(true, false);
        this.plumber = plumber;
        this.cistern = cistern;
        this.gameModel = model;
        this.app = app;
        this.gameModel.getTurnManager().addPropertyChangeListener(this);

        this.panelPadding = (int) (BASE_PANEL_PADDING * OVERLAY_SCALE);
        this.itemSize = (int) (BASE_ITEM_SIZE * OVERLAY_SCALE);
        this.itemGap = (int) (BASE_ITEM_GAP * OVERLAY_SCALE);
        this.gapItemLabel = (int) (BASE_GAP_ITEM_LABEL * OVERLAY_SCALE);
        this.gapLabelButtons = (int) (BASE_GAP_LABEL_BUTTONS * OVERLAY_SCALE);
        this.textScale = BASE_TEXT_SCALE * OVERLAY_SCALE;
        this.labelScale = BASE_LABEL_SCALE * OVERLAY_SCALE;

        SpriteManager sm = SpriteManager.getInstance();
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        pipeSpriteSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        bannerSprite = sm.getSprite(Sprites.PAPER_BANNER);
        font = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MONO);

        backgroundPanel = new Panel(1.0f, 0, SIMPLE_PANEL);

        SpriteSheet btnSheet = sm.getSpriteSheet(SpriteSheets.CONFIRM_CANCEL_BUTTONS);
        int btnSize = (int) (24 * OVERLAY_SCALE);
        confirmButton = new GameButton(btnSheet, 0, 0, 0, btnSize, btnSize);
        cancelButton = new GameButton(btnSheet, 1, 0, 0, btnSize, btnSize);
        closeButton = new GameButton(btnSheet, 1, 0, 0, btnSize, btnSize);

        closeButton.setAction(() -> {
            if (hasClosed) return;
            app.popLayer();
            close();
        });

        cancelButton.setAction(() -> finish(false));
        confirmButton.setAction(() -> {
            if (!canPickup()) {
                System.out.println("[ERROR] Cannot pickup: conditions not met");
                return;
            }
            finish(true);
        });

        pumpSelected = false;
        pipeSelected = false;
        pumpBounds = new Rectangle();
        pipeBounds = new Rectangle();

        updateConfirmButtonState();
    }

    private boolean canPickup() {
        return (pumpSelected || pipeSelected) &&
            !plumber.getInventory().isFull() &&
            gameModel.getTurnManager().canUseBigAction();
    }

    private void updateConfirmButtonState() {
        confirmButton.setEnabled(canPickup());
    }

    private void finish(boolean confirmed) {
        if (confirmed) {
            if (listener != null) listener.onConfirm(pumpSelected, pipeSelected);
        } else {
            if (listener != null) listener.onDiscard();
        }
        close();
    }

    private void close() {
        if (hasClosed) return;
        this.hasClosed = true;
        this.gameModel.getTurnManager().removePropertyChangeListener(this);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            app.popLayer();
            close();
        }
        return true;
    }

    @Override
    public void onEnter() {
        pumpSelected = false;
        pipeSelected = false;
        recomputeLayout();
        updateConfirmButtonState();
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void update(float deltaTime) {
        confirmButton.update();
        cancelButton.update();
        closeButton.update();
        updateConfirmButtonState();
    }

    @Override
    public void render(Graphics2D g) {
        backgroundPanel.draw(g);

        boolean hasPump = cistern.getStoredPump() != null;
        boolean hasPipe = cistern.getStoredPipe() != null;
        boolean inventoryFull = plumber.getInventory().isFull();

        int centreX = ScreenManager.getInstance().getVirtualWidth() / 2;
        int panelTop = backgroundPanel.getY();
        int panelLeft = backgroundPanel.getX();
        int panelWidth = backgroundPanel.getWidth();
        int panelHeight = backgroundPanel.getHeight();

        // Empty case
        if (!hasPump && !hasPipe) {
            String msg = "NOTHING TO PICK UP";
            int scaledCharW = (int) (font.getCharWidth() * textScale);
            int textWidth = msg.length() * scaledCharW;
            int textHeight = (int) (font.getCharHeight() * textScale);
            int msgX = panelLeft + (panelWidth - textWidth) / 2;
            int msgY = panelTop + (panelHeight - textHeight) / 2 - textHeight;
            font.draw(g, msg, msgX, msgY, textScale);

            int btnSize = closeButton.getWidth();
            int btnY = panelTop + panelHeight - btnSize;
            closeButton.setCenter(centreX, btnY + btnSize / 2);
            closeButton.draw(g);
            return;
        }

        // Banner
        float bannerScale = (float) panelWidth / bannerSprite.getWidth();
        float bannerTextScale = textScale * 1.2f;
        titleBanner = new Banner(bannerSprite, bannerScale, "PICK UP", bannerTextScale);
        int bannerX = panelLeft;
        int bannerY = panelTop - titleBanner.getHeight() - 10;
        titleBanner.setPosition(bannerX, bannerY);
        titleBanner.draw(g);

        // Buttons glued to bottom
        int buttonHeight = confirmButton.getHeight();
        int buttonsY = panelTop + panelHeight - buttonHeight - BUTTON_BOTTOM_MARGIN;

        // Space above buttons
        int spaceAboveButtons = buttonsY - panelTop;

        // Item + label block height
        int labelHeight = (int) (font.getCharHeight() * labelScale);
        int blockHeight = itemSize + gapItemLabel + labelHeight;
        int blockStartY = panelTop + (spaceAboveButtons - blockHeight) / 2;

        int itemY = blockStartY;
        int labelY = itemY + itemSize + gapItemLabel;

        // Draw items
        int itemCount = (hasPump ? 1 : 0) + (hasPipe ? 1 : 0);
        int[] itemCentresX = new int[itemCount];
        if (itemCount == 1) {
            itemCentresX[0] = centreX;
        } else {
            int groupWidth = 2 * itemSize + itemGap;
            int groupLeft = centreX - groupWidth / 2;
            itemCentresX[0] = groupLeft + itemSize / 2;
            itemCentresX[1] = groupLeft + itemSize + itemGap + itemSize / 2;
        }

        int slot = 0;
        if (hasPump) {
            int x = itemCentresX[slot];
            pumpBounds.setBounds(x - itemSize / 2, itemY, itemSize, itemSize);
            pumpSprite.drawCentered(g, x, itemY + itemSize / 2, itemSize, 0);
            if (pumpSelected) drawSelectionGlow(g, x - itemSize / 2, itemY, itemSize, itemSize);
            drawCenteredText(g, "PUMP", x, labelY);
            slot++;
        }
        if (hasPipe) {
            int x = itemCentresX[slot];
            pipeBounds.setBounds(x - itemSize / 2, itemY, itemSize, itemSize);
            Sprite pipeSpr = pipeSpriteSheet.getSprite(0);
            pipeSpr.drawCentered(g, x, itemY + itemSize / 2, itemSize, 0);
            if (pipeSelected) drawSelectionGlow(g, x - itemSize / 2, itemY, itemSize, itemSize);
            drawCenteredText(g, "PIPE", x, labelY);
        }

        // --- INVENTORY FULL MESSAGE ---
        if (inventoryFull) {
            String msg = "INVENTORY FULL";
            int msgCharW = (int) (font.getCharWidth() * labelScale);
            int msgWidth = msg.length() * msgCharW;
            int msgHeight = (int) (font.getCharHeight() * labelScale);
            int msgX = centreX - msgWidth / 2;
            // Position slightly below the items (e.g., 20px below the label)
            int msgY = labelY + labelHeight + 20;
            // Ensure it doesn't overlap buttons (but buttons are at bottom)
            font.draw(g, msg, msgX, msgY, labelScale);
        }

        // Buttons
        int btnSize = confirmButton.getWidth();
        int totalButtonsWidth = 2 * btnSize + BUTTON_GAP;
        int startX = centreX - totalButtonsWidth / 2;
        cancelButton.setPosition(startX, buttonsY);
        confirmButton.setPosition(startX + btnSize + BUTTON_GAP, buttonsY);

        cancelButton.draw(g);
        confirmButton.draw(g);
    }

    private void drawSelectionGlow(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 215, 0, 80));
        g2.fillRoundRect(x - 2, y - 2, w + 4, h + 4, 12, 12);
        g2.setColor(new Color(255, 200, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x - 1, y - 1, w + 2, h + 2, 10, 10);
        g2.dispose();
    }

    private void drawCenteredText(Graphics2D g, String text, int cx, int y) {
        if (font == null) return;
        int scaledCharW = (int) (font.getCharWidth() * labelScale);
        int textWidth = text.length() * scaledCharW;
        font.draw(g, text, cx - textWidth / 2, y, labelScale);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        confirmButton.mousePressed(e);
        cancelButton.mousePressed(e);
        closeButton.mousePressed(e);

        Point p = e.getPoint();
        boolean changed = false;
        if (cistern.getStoredPump() != null && pumpBounds.contains(p)) {
            pumpSelected = !pumpSelected;
            AudioPlayer.getInstance().playEffect("button_pressed");
            changed = true;
        }
        if (cistern.getStoredPipe() != null && pipeBounds.contains(p)) {
            pipeSelected = !pipeSelected;
            AudioPlayer.getInstance().playEffect("button_pressed");
            changed = true;
        }
        if (changed) {
            updateConfirmButtonState();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        confirmButton.mouseReleased(e);
        cancelButton.mouseReleased(e);
        closeButton.mouseReleased(e);
        return true;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        confirmButton.mouseMoved(e);
        cancelButton.mouseMoved(e);
        closeButton.mouseMoved(e);
        return true;
    }

    private void recomputeLayout() {
        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        boolean hasPump = cistern.getStoredPump() != null;
        boolean hasPipe = cistern.getStoredPipe() != null;
        int itemCount = (hasPump ? 1 : 0) + (hasPipe ? 1 : 0);

        if (itemCount == 0) {
            String msg = "NOTHING TO PICK UP";
            int scaledCharW = (int) (font.getCharWidth() * textScale);
            int textWidth = msg.length() * scaledCharW;
            int textHeight = (int) (font.getCharHeight() * textScale);
            int btnSize = closeButton.getWidth();
            int panelW = textWidth + 4 * panelPadding;
            int panelH = textHeight + btnSize + 3 * panelPadding;
            int panelSize = Math.max(panelW, panelH);
            backgroundPanel.setPosition(screenW / 2 - panelSize / 2, screenH / 2 - panelSize / 2);
            backgroundPanel.setSize(panelSize, panelSize);
            return;
        }

        int labelCharW = (int) (font.getCharWidth() * labelScale);
        int labelCharH = (int) (font.getCharHeight() * labelScale);
        int maxLabelWidth = Math.max("PUMP".length() * labelCharW, "PIPE".length() * labelCharW);

        int contentWidth = (itemCount == 1) ? itemSize : (2 * itemSize + itemGap);
        int baseWidth = Math.max(contentWidth, maxLabelWidth) + 2 * panelPadding;
        baseWidth = Math.max(baseWidth, 250);

        int btnSize = confirmButton.getWidth();
        int contentHeight = itemSize + gapItemLabel + labelCharH;
        int baseHeight = contentHeight + 2 * panelPadding + btnSize;

        int panelSize = Math.max(baseWidth, baseHeight);
        backgroundPanel.setPosition(screenW / 2 - panelSize / 2, screenH / 2 - panelSize / 2);
        backgroundPanel.setSize(panelSize, panelSize);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Constants.PLAYER_ADVANCED) && !hasClosed) {
            close();
            app.popLayer();
        }
    }
}