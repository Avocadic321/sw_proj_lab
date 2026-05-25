package software.project.ui.layers;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

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
import software.project.map.Element;
import software.project.map.Pipe;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Inventory;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.ui.ScreenManager;
import software.project.ui.components.Banner;
import software.project.ui.renderer.Grid;
import software.project.utils.Constants;

public class HudLayer extends Layer implements PropertyChangeListener {
    private static final int MARGIN = 15;
    private static final int SCORE_GAP = 10;

    private static final float TIMER_BANNER_SCALE = 1f;
    private static final float SCORE_BANNER_SCALE = 1f;
    private static final float TIMER_TEXT_SCALE = 1.1f;
    private static final float SCORE_TEXT_SCALE = 1.1f;

    private static final int PANEL_PADDING = 8;
    private static final int SLOT_SIZE = 80;
    private static final int ICON_SIZE = 50;

    private static final int SCORE_OUTLINE_PADDING = 4;
    private static final int SCORE_OUTLINE_RADIUS = 10;

    private static final int HINT_KEY_SIZE = 40;
    private static final int HINT_GAP = 8;
    private static final int HINT_PADDING = 8;
    private static final float HINT_TEXT_SCALE = 1f;

    private static final Color PLUMBER_COLOR = new Color(70, 130, 220);
    private static final Color SABOTEUR_COLOR = new Color(200, 70, 70);

    private final GameModel model;
    private final BitmapFont hudFont;

    private final Banner timerBanner;
    private final Banner plumberScoreBanner;
    private final Banner saboteurScoreBanner;

    private final Sprite inventoryPanelSprite;
    private final Sprite pumpSprite;
    private final SpriteSheet pipeSheet;
    private Grid grid;

    private final int goalScore;

    private int inventorySlots = -1;
    private final Rectangle inventoryPanelBounds = new Rectangle();
    private Rectangle[] slotBounds = new Rectangle[0];


    private boolean dragging = false;
    private ICarriable draggedItem = null;
    private int draggedSlot = -1;
    private Point dragStart = null;
    private Point currentDragPos = null;

    public boolean isDragging() { return dragging; }
    public ICarriable getDraggedItem() { return draggedItem; }
    public Point getCurrentDragPos() { return currentDragPos; }
    public void resetDrag() {
        dragging = false;
        draggedItem = null;
        draggedSlot = -1;
        dragStart = null;
        currentDragPos = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Constants.PLAYER_ADVANCED)) {
            resetDrag();
        }
    }

    public HudLayer(GameModel model) {
        super(false, false);
        this.model = model;

        this.goalScore = model.getConfig().getGoalScore();

        SpriteManager sm = SpriteManager.getInstance();
        Sprite timerSprite = sm.getSprite(Sprites.TIMER_BANNER);
        Sprite scoreSprite = sm.getSprite(Sprites.PAPER_BANNER);
        this.inventoryPanelSprite = sm.getSprite(Sprites.SIMPLE_PANEL);
        this.pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        this.pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);

        timerBanner = new Banner(timerSprite, TIMER_BANNER_SCALE, BitmapFonts.FONT_MAIN, "T0000", TIMER_TEXT_SCALE);
        plumberScoreBanner = new Banner(scoreSprite, SCORE_BANNER_SCALE, BitmapFonts.FONT_MONO, "P0/XXX",
                SCORE_TEXT_SCALE);
        saboteurScoreBanner = new Banner(scoreSprite, SCORE_BANNER_SCALE, BitmapFonts.FONT_MONO, "S0/XXX",
                SCORE_TEXT_SCALE);
        hudFont = ResourceManager.getInstance().getFont(BitmapFonts.FONT_MAIN);
        this.model.getTurnManager().addPropertyChangeListener(this);
        recomputeLayout();
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        recomputeLayout();
    }

    @Override
    public void update(float deltaTime) {
        int timeLeft = model.getTurnManager().getTimeLeft();
        timerBanner.setText(formatTime(timeLeft));

        int plumberScore = model.getPlumbersTeam() == null ? 0 : model.getPlumbersTeam().getScore();
        int saboteurScore = model.getSaboteursTeam() == null ? 0 : model.getSaboteursTeam().getScore();
        plumberScoreBanner.setText("P" + plumberScore + "/" + goalScore);
        saboteurScoreBanner.setText("S" + saboteurScore + "/" + goalScore);

        Player current = model.getTurnManager().getCurrentPlayer();
        if (current instanceof Plumber plumber) {
            updateInventoryLayout(plumber.getInventory());
        }
    }

    @Override
    public void render(Graphics2D g) {
        timerBanner.draw(g);
        drawScoreBackdrop(g, plumberScoreBanner, PLUMBER_COLOR);
        drawScoreBackdrop(g, saboteurScoreBanner, SABOTEUR_COLOR);
        plumberScoreBanner.draw(g);
        saboteurScoreBanner.draw(g);

        Player current = model.getTurnManager().getCurrentPlayer();
        if (current instanceof Plumber plumber) {
            drawInventory(g, plumber.getInventory());
        }
        drawActionHints(g, current);
        grid = new Grid();
        grid.computeFromMap(model.getGameMap());
        drawDragging(g, grid);
    }

    private void drawPossiblePumpConnections(Graphics2D g, Grid grid) {
        List<Pipe> pipes = model.getGameMap().getAllPipes();
        for(Pipe pipe: pipes) {
            List<Point> points = model.getGameMap().getAdjacentEmptyPositions(model.getGameMap().getElementAt(pipe.getX(), pipe.getY()));
            Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
            if(freePoint == null) continue;

            // Draw highlight on that tile
            Rectangle tileRect = grid.getCellBounds(freePoint.x, freePoint.y);
            if (tileRect != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setColor(new Color(0, 200, 0, 100));
                g2.fillRect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
                g2.dispose();
            }
        }
    }

    private void drawDragging(Graphics2D g, Grid grid) {
        Player player = model.getTurnManager().getCurrentPlayer();
        if(dragging && draggedItem != null && currentDragPos != null && player instanceof Plumber) {
            Sprite sprite = getSpriteForItem(draggedItem);
            if(sprite != null) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));
                sprite.drawCentered(g,currentDragPos.x,currentDragPos.y,grid.getTileSize(),0);
            }
            drawPossiblePumpConnections(g, grid);
        }
    }

    private Sprite getSpriteForItem(ICarriable item) {
        if (item instanceof Pump) return pumpSprite;
        if (item instanceof Pipe) return pipeSheet == null ? null : pipeSheet.getSprite(0);
        return null;
    }

    private void recomputeLayout() {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        timerBanner.setPosition(MARGIN, MARGIN);

        int scoreW = plumberScoreBanner.getWidth();
        int scoreH = plumberScoreBanner.getHeight();
        int scoreX = virtualW - MARGIN - scoreW;
        plumberScoreBanner.setPosition(scoreX, MARGIN);
        saboteurScoreBanner.setPosition(scoreX, MARGIN + scoreH + SCORE_GAP);

        int panelW = SLOT_SIZE + (2 * PANEL_PADDING);
        int panelH = SLOT_SIZE + (2 * PANEL_PADDING);
        int panelX = MARGIN;
        int panelY = virtualH - MARGIN - panelH;
        inventoryPanelBounds.setBounds(panelX, panelY, panelW, panelH);

        if (inventorySlots > 0) {
            updateInventoryLayout(panelX, inventorySlots);
        }
    }

    private void updateInventoryLayout(Inventory inventory) {
        int slots = inventory.getInventory().length;
        if (slots != inventorySlots) {
            inventorySlots = slots;
            updateInventoryLayout(inventoryPanelBounds.x, inventorySlots);
        }
    }

    private void updateInventoryLayout(int panelX, int slots) {
        int panelW = slots * SLOT_SIZE + (2 * PANEL_PADDING);
        int panelH = SLOT_SIZE + (2 * PANEL_PADDING);
        int panelYAdjusted = ScreenManager.getInstance().getVirtualHeight() - MARGIN - panelH;
        inventoryPanelBounds.setBounds(panelX, panelYAdjusted, panelW, panelH);

        slotBounds = new Rectangle[slots];
        int slotX = inventoryPanelBounds.x + PANEL_PADDING;
        int slotY = inventoryPanelBounds.y + PANEL_PADDING;
        for (int i = 0; i < slots; i++) {
            slotBounds[i] = new Rectangle(slotX, slotY, SLOT_SIZE, SLOT_SIZE);
            slotX += SLOT_SIZE;
        }
    }

    private void drawInventory(Graphics2D g, Inventory inventory) {
        if (inventoryPanelSprite != null) {
            inventoryPanelSprite.draw(g, inventoryPanelBounds.x, inventoryPanelBounds.y, inventoryPanelBounds.width,
                    inventoryPanelBounds.height);
        } else {
            g.setColor(new Color(10, 10, 10, 140));
            g.fillRoundRect(inventoryPanelBounds.x, inventoryPanelBounds.y, inventoryPanelBounds.width,
                    inventoryPanelBounds.height, 12, 12);
            g.setColor(new Color(230, 210, 160, 180));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(inventoryPanelBounds.x, inventoryPanelBounds.y, inventoryPanelBounds.width,
                    inventoryPanelBounds.height, 12, 12);
        }

        for (int i = 0; i < slotBounds.length; i++) {
            Rectangle slot = slotBounds[i];
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);
            g.setColor(new Color(255, 255, 255, 120));
            g.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 8, 8);

            ICarriable item = inventory.get(i);
            if (item instanceof Pump) {
                drawIcon(g, pumpSprite, slot);
            } else if (item instanceof Pipe) {
                drawIcon(g, pipeSheet == null ? null : pipeSheet.getSprite(0), slot);
            }
        }
    }

    private void drawIcon(Graphics2D g, Sprite sprite, Rectangle slot) {
        if (sprite == null) {
            return;
        }
        int centerX = slot.x + slot.width / 2 + 1; // +1 for a slight right adjustment
        int centerY = slot.y + slot.height / 2;
        sprite.drawCentered(g, centerX, centerY, ICON_SIZE, 0);
    }

    private String formatTime(int timeLeftSeconds) {
        int mins = Math.max(0, timeLeftSeconds) / 60;
        int secs = Math.max(0, timeLeftSeconds) % 60;
        return String.format("T%02d%02d", mins, secs);
    }

    private void drawScoreBackdrop(Graphics2D g, Banner banner, Color color) {
        int x = banner.getX() - SCORE_OUTLINE_PADDING;
        int y = banner.getY() - SCORE_OUTLINE_PADDING;
        int w = banner.getWidth() + (2 * SCORE_OUTLINE_PADDING);
        int h = banner.getHeight() + (2 * SCORE_OUTLINE_PADDING);

        Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 70);
        Color stroke = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
        g.setColor(fill);
        g.fillRoundRect(x, y, w, h, SCORE_OUTLINE_RADIUS, SCORE_OUTLINE_RADIUS);
        g.setColor(stroke);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, SCORE_OUTLINE_RADIUS, SCORE_OUTLINE_RADIUS);
    }

    private void drawActionHints(Graphics2D g, Player current) {
        List<ActionHint> hints = getActionHints(current);
        if (hints.isEmpty() || hudFont == null) {
            return;
        }

        int screenW = ScreenManager.getInstance().getVirtualWidth();
        int screenH = ScreenManager.getInstance().getVirtualHeight();

        int textH = (int) (hudFont.getCharHeight() * HINT_TEXT_SCALE);
        int blockH = Math.max(HINT_KEY_SIZE, textH) + (2 * HINT_PADDING);
        int maxTextW = 0;
        for (ActionHint hint : hints) {
            int textW = (int) (hudFont.getCharWidth() * HINT_TEXT_SCALE) * hint.action.length();
            if (textW > maxTextW) {
                maxTextW = textW;
            }
        }
        int blockW = HINT_KEY_SIZE + HINT_GAP + maxTextW + (2 * HINT_PADDING);
        int totalHeight = (blockH * hints.size()) + (HINT_GAP * (hints.size() - 1));
        int x = screenW - MARGIN - blockW;
        int scoreBottom = saboteurScoreBanner.getY() + saboteurScoreBanner.getHeight() + SCORE_GAP;
        int baseY = Math.max(scoreBottom, (screenH - totalHeight) / 2);

        for (int i = 0; i < hints.size(); ++i) {
            ActionHint hint = hints.get(i);
            int y = baseY + i * (blockH + HINT_GAP);
            Color accent = hint.teamColor;
            Color keyFill = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120);
            Color keyStroke = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 220);

            int keyX = x + HINT_PADDING;
            int keyY = y + HINT_PADDING + (blockH - (2 * HINT_PADDING) - HINT_KEY_SIZE) / 2;
            g.setColor(keyFill);
            g.fillRoundRect(keyX, keyY, HINT_KEY_SIZE, HINT_KEY_SIZE, 6, 6);
            g.setColor(keyStroke);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(keyX, keyY, HINT_KEY_SIZE, HINT_KEY_SIZE, 6, 6);

            int keyTextX = keyX + (HINT_KEY_SIZE - (int) (hudFont.getCharWidth() * HINT_TEXT_SCALE)) / 2;
            int keyTextY = keyY + (HINT_KEY_SIZE - textH) / 2;
            hudFont.draw(g, hint.key, keyTextX, keyTextY, HINT_TEXT_SCALE);

            int actionX = keyX + HINT_KEY_SIZE + HINT_GAP;
            int actionY = y + HINT_PADDING + (blockH - (2 * HINT_PADDING) - textH) / 2;
            hudFont.draw(g, hint.action, actionX, actionY, HINT_TEXT_SCALE);
        }
    }

    private List<ActionHint> getActionHints(Player current) {
        List<ActionHint> hints = new ArrayList<>();
        if (current == null) {
            return hints;
        }
        Element position = current.getCurrentPosition();
        if (position == null) {
            return hints;
        }

        boolean isPlumber = current instanceof Plumber;
        boolean isSaboteur = current instanceof Saboteur;
        Color teamColor = isPlumber ? PLUMBER_COLOR : SABOTEUR_COLOR;

        hints.add(new ActionHint("s", "skip turn", teamColor));

        if (isPlumber) {
            if (position instanceof Cistern) {
                hints.add(new ActionHint("q", "pickup", teamColor));
            }
            if (position instanceof Pump pump) {
                if (pump.isBroken()) {
                    hints.add(new ActionHint("f", "repair", teamColor));
                } else {
                    hints.add(new ActionHint("d", "direction", teamColor));
                }
            }
            if (position instanceof Pipe pipe && pipe.isBroken()) {
                hints.add(new ActionHint("f", "repair", teamColor));
            }
            return hints;
        }

        if (isSaboteur) {
            if (position instanceof Pipe pipe && !pipe.isBroken()) {
                hints.add(new ActionHint("f", "sabotage", teamColor));
            }
            if (position instanceof Pump pump) {
                hints.add(new ActionHint("d", "direction", teamColor));
                if (!pump.isBroken()) {
                    hints.add(new ActionHint("f", "sabotage", teamColor));
                }
            }
        }

        return hints;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        Player player = model.getTurnManager().getCurrentPlayer();
        if(inventorySlots > 0 && slotBounds != null && player instanceof Plumber plumber) {
            for(int i = 0; i < slotBounds.length; i++) {
                if(slotBounds[i].contains(e.getPoint())) {
                    ICarriable item = plumber.getInventory().get(i);
                    if(item == null) continue;
                    dragging = true;
                    draggedItem = item;
                    draggedSlot = i;
                    dragStart = e.getPoint();
                    currentDragPos = e.getPoint();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if(dragging) {
            currentDragPos = e.getPoint();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            return true;
        }
        return false;
    }

    public Rectangle[] getSlotBounds() {
        return slotBounds;
    }

    private static final class ActionHint {
        private final String key;
        private final String action;
        private final Color teamColor;

        private ActionHint(String key, String action, Color teamColor) {
            this.key = key;
            this.action = action;
            this.teamColor = teamColor;
        }
    }
}