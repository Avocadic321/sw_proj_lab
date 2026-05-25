package software.project.ui.renderer;

import software.project.core.GameModel;
import software.project.map.Element;
import software.project.map.GameMap;
import software.project.map.Pipe;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MapRenderer {
    private final GameModel model;
    private final Grid grid;
    private final BackgroundRenderer background;
    private final ElementRenderer elements;
    private final PlayerRenderer players;
    private final LeakRenderer leakRenderer;

    // Animation timers (paused automatically when update not called)
    private float gameTime = 0f;
    private float arrowTick = 0f;

    // Movement animation
    private boolean moving = false;
    private Point moveStartCenter;
    private Point moveEndCenter;
    private float moveTimer = 0f;
    private float moveDuration = 0.2f; // seconds per tile
    private Player movingPlayer;
    private List<Element> movePath;
    private int moveStepIndex;

    // Click handling
    private final List<ClickableElement> clickableElements = new ArrayList<>();

    public MapRenderer(GameModel model) {
        this.model = model;
        this.grid = Grid.getInstance();
        this.background = new BackgroundRenderer();
        this.elements = new ElementRenderer();
        this.players = new PlayerRenderer();
        this.leakRenderer = new LeakRenderer();
    }

    public void update(float deltaTime) {
        // Always advance game time for other uses
        gameTime += deltaTime;

        // Update leak particles
        leakRenderer.update(deltaTime, model.getGameMap().getAllPipes(), grid);

        // Update fan angles for all pumps every frame
        elements.updateFanAngles(deltaTime, model.getGameMap().getAllPumps());
        elements.updateCisternItemAngles(deltaTime,model.getGameMap().getAllCisterns());

        // Arrow tick pauses during movement (optional)
        if (!moving) {
            arrowTick += deltaTime;
        }

        // Handle movement animation
        if (moving) {
            moveTimer += deltaTime;
            if (moveTimer >= moveDuration) {
                moveTimer = 0f;
                moveStepIndex++;
                if (moveStepIndex >= movePath.size()) {
                    // Animation finished
                    moving = false;
                    movingPlayer.moveTo(movePath.get(movePath.size() - 1));
                    for (Element el : movePath) el.unlockElement();
                    movePath.clear();
                    movingPlayer = null;
                } else {
                    Element from = movePath.get(moveStepIndex - 1);
                    Element to = movePath.get(moveStepIndex);
                    moveStartCenter = grid.getCellCenter(from.getX(), from.getY());
                    moveEndCenter = grid.getCellCenter(to.getX(), to.getY());
                }
            }
        }
    }

    public Element getElementAt(int screenX, int screenY) {
        for(ClickableElement ce : clickableElements) {
            if(ce.bounds.contains(screenX, screenY)) {
                return ce.element();
            }
        }
            return null;
    }
    public boolean tryPlaceItem(ICarriable item, Plumber player, Pipe pipe, Point p) {
        if(item instanceof Pump pump) {
           Point gridPoints = grid.screenToGrid(p.x,p.y);

            List<Point> points = model.getGameMap().getAdjacentEmptyPositions(model.getGameMap().getElementAt(pipe.getX(),pipe.getY()));
           Point freePoint = pipe.getFreeEndConnectionCoordinates(points);
           if(freePoint == null || gridPoints.x != freePoint.x || gridPoints.y != freePoint.y) return false;
        boolean response = player.placePump(pipe,pump,freePoint);
        if(response) {
            model.getGameMap().addElement(pump);
            player.getInventory().removeItem(pump);
        }
        }
        return false;
    }
    public void draw(Graphics2D g) {
        GameMap map = model.getGameMap();
        grid.update(model.getGameMap());
        background.drawSand(g, grid);
        background.drawGridLines(g, grid);
        elements.drawSprings(g, map.getAllSprings(), grid);

        // Draw leaks before pipes so they appear behind/under the pipes
        leakRenderer.draw(g, map.getAllPipes());
        elements.drawPipes(g, map.getAllPipes(), grid);
        elements.drawPumps(g, map.getAllPumps(), grid);
        elements.drawCisterns(g, map.getAllCisterns(), grid);

        Player current = model.getTurnManager().getCurrentPlayer();

        // Draw non‑moving players
        players.drawPlayers(g, model.getSaboteursTeam(), model.getPlumbersTeam(), current, grid);

        if (moving && movingPlayer != null && moveStartCenter != null && moveEndCenter != null) {
            float t = Math.min(1f, moveTimer / moveDuration);
            int x = (int) (moveStartCenter.x + (moveEndCenter.x - moveStartCenter.x) * t);
            int y = (int) (moveStartCenter.y + (moveEndCenter.y - moveStartCenter.y) * t);
            players.drawPlayerAt(g, movingPlayer, new Point(x, y), grid.getTileSize());
            // Draw arrow over the moving player (optional)
            if (movingPlayer == current) {
                players.drawArrow(g, new Point(x, y), (int)(grid.getTileSize() * PlayerRenderer.PLAYER_SCALE), arrowTick);
            }
        } else if (current != null && !moving) {
            players.drawCurrentPlayer(g, current, grid, arrowTick);
        }

        rebuildClickTargets(map);
    }

    public void drawLetterboxSand(Graphics2D g) {
        background.drawLetterboxSand(g, grid);
    }

    public boolean mousePressed(MouseEvent e) {
        if (moving) return false;
        Player player = model.getTurnManager().getCurrentPlayer();
        if (player == null) return false;

        for (ClickableElement ce : clickableElements) {
            if (ce.bounds().contains(e.getX(), e.getY())) {
                Element target = ce.element();
                List<Element> path = model.getGameMap().buildPathToDestination(
                    player.getCurrentPosition(), target);
                if (path.size() <= 1) return false;

                for (Element el : path) el.lockElement(player);

                moving = true;
                movingPlayer = player;
                movePath = new ArrayList<>(path);
                moveStepIndex = 1;
                Element from = movePath.get(0);
                Element to = movePath.get(1);
                moveStartCenter = grid.getCellCenter(from.getX(), from.getY());
                moveEndCenter = grid.getCellCenter(to.getX(), to.getY());
                moveTimer = 0f;
                return true;
            }
        }
        return false;
    }

    private void rebuildClickTargets(GameMap map) {
        clickableElements.clear();
        for (Element e : map.getElements()) {
            Rectangle bounds = grid.getCellBounds(e.getX(), e.getY());
            clickableElements.add(new ClickableElement(e, bounds));
        }
    }

    private record ClickableElement(Element element, Rectangle bounds) {}
}