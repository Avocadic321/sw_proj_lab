package software.project.ui.layers;

import software.project.core.GameModel;
import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.map.Element;
import software.project.map.Pipe;
import software.project.map.PipeOrientation;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
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
    private final ConnectModeElement connectMode;
    private final InventoryElement inventory;

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
        this.connectMode = new ConnectModeElement(model);
        this.inventory = new InventoryElement(model, this::onDragStart);
        elements.add(new TimerElement(model));
        elements.add(new ScoreElement(model));
        elements.add(inventory);
        elements.add(connectMode);
        elements.add(new ActionHintElement(model));

        var sm = SpriteManager.getInstance();
        pumpSprite = sm.getSprite(Sprites.PUMP_STATIC);
        SpriteSheet pipeSheet = sm.getSpriteSheet(SpriteSheets.PIPE_NORMAL);
        pipeSprite = pipeSheet != null ? pipeSheet.getSprite(0) : null;

        model.getTurnManager().addPropertyChangeListener(this);
    }

    private void onDragStart(ICarriable item, int slot, Point screenPos) {
        dragging = true;
        draggedItem = item;
        draggedSlot = slot;
        currentDragPos = screenPos;
        draggedOrientation = PipeOrientation.VERTICAL;

        if (item instanceof Pipe) {
            // Determine initial orientation from current pipe if standing on one
            Player player = model.getTurnManager().getCurrentPlayer();
            Element current = player.getCurrentPosition();
            if (current instanceof Pipe pipe) {
                draggedOrientation = pipe.getOrientation();
            }
            connectMode.setPipeMode(true, draggedOrientation);
        } else if (item instanceof Pump) {
            connectMode.setPumpMode(true);
        }
    }

    public void resetDrag() {
        dragging = false;
        draggedItem = null;
        draggedSlot = -1;
        currentDragPos = null;
        draggedOrientation = PipeOrientation.VERTICAL;
    }

    public boolean isDragging() { return dragging; }
    public ICarriable getDraggedItem() { return draggedItem; }
    public Point getCurrentDragPos() { return currentDragPos; }
    public void rotateDraggedItem() {
        if (dragging && draggedItem instanceof Pipe) {
            draggedOrientation = (draggedOrientation == PipeOrientation.VERTICAL)
                ? PipeOrientation.HORIZONTAL : PipeOrientation.VERTICAL;
            connectMode.setDraggedOrientation(draggedOrientation);
        }
    }
    public PipeOrientation getDraggedPipeOrientation() { return draggedOrientation; }
    public void toggleConnectMode() { connectMode.setActive(!connectMode.isActive()); }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Constants.PLAYER_ADVANCED)) {
            resetDrag();
            connectMode.setActive(false);
        }
    }

    @Override
    public void update(float deltaTime) {
        for (HudElement e : elements) e.update(deltaTime);
        Grid.getInstance().update(model.getGameMap()); // for dragging placement
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

        // Rotation: 0 for vertical, 90° for horizontal
        double angle = (draggedOrientation == PipeOrientation.VERTICAL) ? 0 : 90;

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
        if (dragging) {
            boolean success = false;
            if (draggedItem instanceof Pipe && connectMode.isPipeMode()) {
                success = connectMode.tryPlacePipe(draggedItem, draggedSlot, e.getPoint(), draggedOrientation);
            } else if (draggedItem instanceof Pump && connectMode.isPumpMode()) {
                success = connectMode.tryPlacePump(draggedItem, draggedSlot, e.getPoint());
            }
            if (success) {
                resetDrag();
                return true;
            }
        }
        resetDrag();
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