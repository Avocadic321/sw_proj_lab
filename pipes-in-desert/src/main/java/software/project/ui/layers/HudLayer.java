package software.project.ui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Directions;
import software.project.map.Element;
import software.project.map.Pipe;
import software.project.map.PipeOrientation;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.ui.hud.*;
import software.project.ui.renderer.Grid;
import software.project.utils.Constants;

public class HudLayer extends Layer implements PropertyChangeListener {
    private final GameModel model;
    private final Sprite pumpSprite;
    private final Sprite pipeSprite;

    // HUD elements
    private final List<HudElement> elements = new ArrayList<>();
    private final ConnectionsElement connectMode;
    private final PickupElement pickupElement;
    private final ActionHintElement actionHintElement;
    private final ActionStatusElement actionStatusElement;
    private final InventoryElement inventoryElement;
    private final TimerElement timerElement;

    // Drag state
    private boolean dragging = false;
    private ICarriable draggedItem = null;
    private int draggedSlot = -1;
    private Point currentDragPos = null;
    private PipeOrientation draggedOrientation = PipeOrientation.VERTICAL;

    public HudLayer(GameModel model) {
        super(false, false);
        this.model = model;

        SpriteManager sm = SpriteManager.getInstance();
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        pipeSprite = pipeSheet != null ? pipeSheet.getSprite(0) : null;

        // Create HUD elements
        this.connectMode = new ConnectionsElement(model);
        this.pickupElement = new PickupElement(model);
        this.pickupElement.setPickupListener(this::onPickupPipe);
        this.actionHintElement = new ActionHintElement(model);
        this.actionStatusElement = new ActionStatusElement(model);
        this.inventoryElement = new InventoryElement(model, this::onDragStart);
        this.timerElement = new TimerElement(model);

        elements.add(timerElement);
        elements.add(inventoryElement);
        elements.add(connectMode);
        elements.add(pickupElement);
        elements.add(actionHintElement);
        elements.add(actionStatusElement);

        model.getTurnManager().addPropertyChangeListener(this);
    }

    // ========== Callbacks ==========
    private void onPickupPipe(Pipe pipe, Point mapPos) {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return;
        if (plumber.getInventory().isFull()) {
            System.out.println("Inventory is full! Cannot pick up pipe.");
            return;
        }
        if (pipe.getEnd1() != null && pipe.getEnd1().connectedTo != null) pipe.getEnd1().disconnect();
        if (pipe.getEnd2() != null && pipe.getEnd2().connectedTo != null) pipe.getEnd2().disconnect();
        model.getGameMap().removeElement(pipe);
        plumber.getInventory().add(pipe);
    }

    private void onDragStart(ICarriable item, int slot, Point screenPos) {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber)) return;
        dragging = true;
        draggedItem = item;
        draggedSlot = slot;
        currentDragPos = screenPos;
        draggedOrientation = PipeOrientation.VERTICAL;
        if (item instanceof Pipe) {
            Element current = player.getCurrentPosition();
            if (current instanceof Pipe pipe) {
                draggedOrientation = pipe.getOrientation();
            }
            pickupElement.deactivate();
            connectMode.activatePipeMode();
        } else if (item instanceof Pump) {
            pickupElement.deactivate();
            connectMode.activatePumpMode();
        }
    }

    public void togglePickupMode() {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber)) {
            System.out.println("Only plumbers can pick up pipes!");
            return;
        }
        if (pickupElement.isActive()) {
            pickupElement.deactivate();
            connectMode.deactivate();
        } else {
            connectMode.deactivate();
            pickupElement.activatePickupMode();
        }
    }

    public void rotateDraggedItem() {
        if (dragging && draggedItem instanceof Pipe) {
            draggedOrientation = (draggedOrientation == PipeOrientation.VERTICAL)
                ? PipeOrientation.HORIZONTAL : PipeOrientation.VERTICAL;
            connectMode.setDraggedOrientation(draggedOrientation);
        }
    }

    public boolean isDragging() { return dragging; }
    public ICarriable getDraggedItem() { return draggedItem; }
    public Point getCurrentDragPos() { return currentDragPos; }

    private void resetDrag() {
        dragging = false;
        draggedItem = null;
        draggedSlot = -1;
        currentDragPos = null;
        draggedOrientation = PipeOrientation.VERTICAL;
        connectMode.deactivate();
        pickupElement.deactivate();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Constants.PLAYER_ADVANCED)) resetDrag();
    }

    @Override
    public void update(float deltaTime) {
        for (HudElement e : elements) e.update(deltaTime);
        Grid.getInstance().update(model.getGameMap());
    }

    @Override
    public void render(Graphics2D g) {
        for (HudElement e : elements) e.draw(g);
        drawDragging(g);
    }

    private void drawDragging(Graphics2D g) {
        if (!dragging || draggedItem == null || currentDragPos == null) return;
        Sprite sprite = (draggedItem instanceof Pipe) ? pipeSprite : pumpSprite;
        if (sprite == null) return;

        double angle = 0;
        if (draggedItem instanceof Pipe) {
            Directions dir = connectMode.getDirectionForHoveredTile(currentDragPos);
            if (dir != null) {
                angle = (dir == Directions.NORTH || dir == Directions.SOUTH) ? 0 : 90;
            } else {
                angle = (draggedOrientation == PipeOrientation.VERTICAL) ? 0 : 90;
            }
        }

        Point drawPos = currentDragPos;
        Rectangle snapRect = connectMode.getHoveredTileBounds(currentDragPos);
        if (snapRect != null) {
            drawPos = new Point(snapRect.x + snapRect.width/2, snapRect.y + snapRect.height/2);
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        sprite.drawCentered(g, drawPos.x, drawPos.y, Grid.getInstance().getTileSize(), angle);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        for (HudElement e : elements) e.onResolutionChanged(newWidth, newHeight);
    }

    // ========== Input Forwarding ==========
    @Override
    public boolean mousePressed(MouseEvent e) {
        if (pickupElement.isActive()) {
            pickupElement.mousePressed(e);
            return true;
        }
        for (HudElement el : elements) {
            if (el.mousePressed(e)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (dragging) {
            currentDragPos = e.getPoint();
            return true;
        }
        for (HudElement el : elements) {
            if (el.mouseDragged(e)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (pickupElement.isActive()) {
            boolean pickedUp = pickupElement.tryPickupPipe(e.getPoint());
            pickupElement.deactivate();
            return pickedUp;
        }
        if (dragging) {
            Player player = model.getTurnManager().getCurrentPlayer();
            if (player instanceof Plumber plumber) {
                boolean success = false;
                if (draggedItem instanceof Pipe) {
                    success = connectMode.tryPlacePipe(draggedItem, draggedSlot, e.getPoint(), draggedOrientation);
                } else if (draggedItem instanceof Pump) {
                    success = connectMode.tryPlacePump(draggedItem, draggedSlot, e.getPoint());
                }
                resetDrag();
                return success;
            }
            resetDrag();
            return true;
        }
        for (HudElement el : elements) {
            if (el.mouseReleased(e)) return true;
        }
        return false;
    }
}