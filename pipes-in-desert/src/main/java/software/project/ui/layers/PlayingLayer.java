package software.project.ui.layers;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import software.project.audio.AudioPlayer;
import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.core.GameState;
import software.project.map.Cistern;
import software.project.map.Element;
import software.project.map.Pipe;
import software.project.map.Pump;
import software.project.map.interfaces.ICarriable;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.renderer.MapRenderer;

public class PlayingLayer extends Layer {
    private final GameApplication app;
    private final GameModel model;
    private final MapRenderer renderer;
    private final HudLayer hudLayer;

    private boolean gameOverShown = false;

    private final Map<Integer, Runnable> keyBindings = new HashMap<>();

    public PlayingLayer(GameApplication app) {
        this(app, new GameConfig());
    }

    public PlayingLayer(GameApplication app, GameConfig config) {
        super(false, false);
        this.app = app;
        this.model = new GameModel(config);
        this.renderer = new MapRenderer(this.model);
        this.hudLayer = new HudLayer(this.model);

        model.startGame();
        ScreenManager.getInstance().getPanel().setBackgroundPainter(renderer::drawLetterboxSand);
        initKeyBindings();
    }

    private void initKeyBindings() {
        // Pause
        keyBindings.put(KeyEvent.VK_ESCAPE, this::togglePause);
        keyBindings.put(KeyEvent.VK_P, this::togglePause);

        // Skip turn
        keyBindings.put(KeyEvent.VK_S, () -> model.getTurnManager().endTurn());

        // Player action (repair/sabotage)
        keyBindings.put(KeyEvent.VK_F, this::performPlayerAction);

        // Open pump direction overlay
        keyBindings.put(KeyEvent.VK_D, this::openPumpOverlay);

        // Open cistern pickup overlay (plumber only)
        keyBindings.put(KeyEvent.VK_Q, this::openCisternOverlay);
    }

    private void togglePause() {
        if (model.getState() == GameState.RUNNING) {
            model.pauseGame();
            PauseOverlay overlay = new PauseOverlay();
            overlay.setResumeAction(() -> {
                model.resumeGame();
                app.popLayer();
            });
            overlay.setOptionsAction(() -> app.pushLayer(new OptionsLayer(app)));
            overlay.setQuitAction(() -> {
                model.endGame();
                app.clearLayers();
                model.getTurnManager().removePropertyChangeListener(this.hudLayer);
                app.pushLayer(new MainMenuLayer(app));
            });
            app.pushLayer(overlay);
        }
    }

    private void performPlayerAction() {
        Player player = model.getTurnManager().getCurrentPlayer();
        boolean done = player.doMainAction();
        if (player instanceof Plumber && done) {
            AudioPlayer.getInstance().playEffect("pipe_repair");
        }
        if (player instanceof Saboteur && done) {
            AudioPlayer.getInstance().playEffect("pipe_break");
        }
    }

    private void openPumpOverlay() {
        Player player = model.getTurnManager().getCurrentPlayer();
        Element element = player.getCurrentPosition();
        if (element instanceof Pump pump) {
            app.pushLayer(new PumpDirectionOverlay(app, pump));
        }
    }

    private void openCisternOverlay() {
        Player player = model.getTurnManager().getCurrentPlayer();
        Element element = player.getCurrentPosition();
        if (!(element instanceof Cistern cistern) || !(player instanceof Plumber plumber)) {
            return;
        }

        CisternPickupOverlay overlay = new CisternPickupOverlay(plumber, cistern, model, app);
        overlay.setListener(new CisternPickupOverlay.PickupListener() {
            @Override
            public void onConfirm(boolean tookPump, boolean tookPipe) {
                if (tookPump) plumber.pickUpPump(cistern);
                if (tookPipe) plumber.pickUpPipe(cistern);
                if(tookPipe || tookPump) {
                    AudioPlayer.getInstance().playEffect("equip_effect");
                }
                app.popLayer();
            }

            @Override
            public void onDiscard() {
                app.popLayer();
            }
        });
        app.pushLayer(overlay);
    }

    @Override
    public void render(Graphics2D g) {
        renderer.draw(g);
        hudLayer.render(g);
    }

    @Override
    public void update(float deltaTime) {
        if (!gameOverShown && model.getState() == GameState.FINALIZED) {
            model.pauseGame();
            gameOverShown = true;
            GameOverOverlay overlay = new GameOverOverlay(model);
            overlay.setMainMenuAction(() -> {
                app.clearLayers();
                app.pushLayer(new MainMenuLayer(app));
            });
            app.pushLayer(overlay);
            return;
        }
        renderer.update(deltaTime);
        hudLayer.update(deltaTime);
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        hudLayer.onResolutionChanged(newWidth, newHeight);
    }

    @Override
    public void onExit() {
        super.onExit();
        ScreenManager.getInstance().getPanel().setBackgroundPainter(null);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        Runnable action = keyBindings.get(e.getKeyCode());
        if (action != null) {
            action.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if(hudLayer.isDragging()) {
            hudLayer.mouseDragged(e);
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseReleased(MouseEvent e) {
        if(hudLayer.isDragging()) {

            Player player = model.getTurnManager().getCurrentPlayer();
            Element element = player.getCurrentPosition();
            Point screenPoint = e.getPoint();
            if(player instanceof Plumber plumber && element instanceof Pipe pipe){
                ICarriable draggedItem = hudLayer.getDraggedItem();
                if(draggedItem != null) {
                    renderer.tryPlaceItem(draggedItem,plumber,pipe,screenPoint);
                }
            }
            hudLayer.resetDrag();
            return true;
        }
        hudLayer.mouseReleased(e);

        return true;
    }
    @Override
    public boolean mousePressed(MouseEvent e) {
        return hudLayer.mousePressed(e) || renderer.mousePressed(e);

    }
}