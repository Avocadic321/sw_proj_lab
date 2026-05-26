package software.project.ui.layers;

import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Directions;
import software.project.map.Element;
import software.project.map.Pipe;
import software.project.map.PipeEnd;
import software.project.map.PipeOrientation;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.ui.hud.*;
import software.project.ui.renderer.Grid;
import software.project.utils.Constants;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class HudLayer extends Layer implements PropertyChangeListener {
    private final GameModel model;
    private final List<HudElement> elements = new ArrayList<>();
    private final ConnectionsElement connectMode;
    private final InventoryElement inventory;
    private final PickupElement pickupElement;

    // Drag state
    private boolean dragging = false;
    private ICarriable draggedItem = null;
    private int draggedSlot = -1;
    private Point currentDragPos = null;
    private PipeOrientation draggedOrientation = PipeOrientation.VERTICAL;
    private final Sprite pumpSprite;
    private final Sprite pipeSprite;

    public HudLayer(GameModel model) {
        super(false, false);
        this.model = model;
        this.connectMode = new ConnectionsElement(model);
        this.inventory = new InventoryElement(model, this::onDragStart);
        this.pickupElement = new PickupElement(model);
        this.pickupElement.setPickupListener(this::onPickupPipe);

        elements.add(new TimerElement(model));
        elements.add(new ScoreElement(model));
        elements.add(inventory);
        elements.add(connectMode);
        elements.add(pickupElement);
        elements.add(new ActionHintElement(model));

        var sm = SpriteManager.getInstance();
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        pipeSprite = pipeSheet != null ? pipeSheet.getSprite(0) : null;

        model.getTurnManager().addPropertyChangeListener(this);
    }

    // ========== Pipe Pickup ==========
    private void onPickupPipe(Pipe pipe, Point mapPos) {
        Player player = model.getTurnManager().getCurrentPlayer();
        if (!(player instanceof Plumber plumber)) return;

        if (plumber.getInventory().isFull()) {
            System.out.println("Inventory is full! Cannot pick up pipe.");
            return;
        }

        // Disconnect pipe from both ends
        PipeEnd end1 = pipe.getEnd1();
        PipeEnd end2 = pipe.getEnd2();

        if (end1 != null && end1.connectedTo != null) {
            end1.disconnect();
        }
        if (end2 != null && end2.connectedTo != null) {
            end2.disconnect();
        }

        // Remove from map
        model.getGameMap().removeElement(pipe);

        // Add to inventory
        plumber.getInventory().add(pipe);

        System.out.println("Picked up pipe " + pipe.getId() + " at (" + mapPos.x + ", " + mapPos.y + ")");
    }

    public void togglePickupMode() {
        if (pickupElement.isActive()) {
            pickupElement.deactivate();
            connectMode.deactivate();
        } else {
            // Deactivate other modes first
            connectMode.deactivate();
            pickupElement.activatePickupMode();
        }
    }

    // ========== Drag Start ==========
    private void onDragStart(ICarriable item, int slot, Point screenPos) {
        dragging = true;
        draggedItem = item;
        draggedSlot = slot;
        currentDragPos = screenPos;
        draggedOrientation = PipeOrientation.VERTICAL;

        if (item instanceof Pipe) {
            // Get standing pipe orientation if applicable
            Player player = model.getTurnManager().getCurrentPlayer();
            Element current = player.getCurrentPosition();
            if (current instanceof Pipe pipe) {
                draggedOrientation = pipe.getOrientation();
            }
            // Deactivate pickup mode if active
            pickupElement.deactivate();
            connectMode.activatePipeMode();  // Shows green squares
        } else if (item instanceof Pump) {
            pickupElement.deactivate();
            connectMode.activatePumpMode();  // Shows blue squares
        }
    }

    public void resetDrag() {
        dragging = false;
        draggedItem = null;
        draggedSlot = -1;
        currentDragPos = null;
        draggedOrientation = PipeOrientation.VERTICAL;
        connectMode.deactivate();
        // Don't deactivate pickup mode here - it's separate
    }

    public boolean isDragging() { return dragging; }
    public ICarriable getDraggedItem() { return draggedItem; }
    public Point getCurrentDragPos() { return currentDragPos; }

    public void rotateDraggedItem() {
        if (dragging && draggedItem instanceof Pipe) {
            draggedOrientation = (draggedOrientation == PipeOrientation.VERTICAL)
                ? PipeOrientation.HORIZONTAL : PipeOrientation.VERTICAL;
            // No need to call anything on connectMode - orientation is only for rendering
        }
    }

    public PipeOrientation getDraggedPipeOrientation() { return draggedOrientation; }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Constants.PLAYER_ADVANCED)) {
            resetDrag();
            pickupElement.deactivate();
            connectMode.deactivate();
        }
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

        // Get the direction for the hovered tile (if any)
        Directions dir = null;
        if (draggedItem instanceof Pipe) {
            dir = connectMode.getDirectionForHoveredTile(currentDragPos);
        }

        // Calculate rotation angle based on direction
        double angle = 0;
        if (draggedItem instanceof Pipe) {
            if (dir != null) {
                // When snapping, rotate based on direction to the target
                angle = (dir == Directions.NORTH || dir == Directions.SOUTH) ? 0 : 90;
            } else {
                // When not snapping, use the dragged orientation (for rotation key)
                angle = (draggedOrientation == PipeOrientation.VERTICAL) ? 0 : 90;
            }
        }

        // Snapping
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

    // Input forwarding
    @Override
    public boolean mousePressed(MouseEvent e) {
        // Check pickup mode first - if active, it should consume the event
        if (pickupElement.isActive()) {
            // Let pickup element handle and consume the event
            for (HudElement el : elements) {
                if (el.mousePressed(e)) {
                    return true;  // Consumed, don't pass to renderer
                }
            }
            // Even if no specific element was clicked, pickup mode should block movement
            return true;
        }

        // Then check other elements
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
        // Handle pickup mode first
        if (pickupElement.isActive()) {
            boolean pickedUp = pickupElement.tryPickupPipe(e.getPoint());
            pickupElement.deactivate();
            if (pickedUp) {
                return true;  // Consumed
            }
            return true;  // Consumed even if no pickup (prevents movement)
        }

        // Handle dragging placement
        if (dragging) {
            boolean success = false;
            if (draggedItem instanceof Pipe) {
                success = connectMode.tryPlacePipe(draggedItem, draggedSlot, e.getPoint(), draggedOrientation);
            } else if (draggedItem instanceof Pump) {
                success = connectMode.tryPlacePump(draggedItem, draggedSlot, e.getPoint());
            }
            resetDrag();
            if (success) {
                return true;
            }
        }

        for (HudElement el : elements) {
            if (el.mouseReleased(e)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        for (HudElement el : elements) {
            if (el.mouseMoved(e)) return true;
        }
        return false;
    }
}