package software.project.ui.layers;

import software.project.audio.AudioPlayer;
import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.core.GameState;

import software.project.map.Cistern;
import software.project.map.Element;
import software.project.map.Pump;

import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.renderer.MapRenderer;
import software.project.utils.Constants;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayingLayer extends Layer implements PropertyChangeListener {
    private final GameApplication app;
    private final GameModel model;
    private final MapRenderer renderer;

    public PlayingLayer(GameApplication app) {
        super(false, false); // non‑modal
        this.app = app;
        GameConfig config = new GameConfig();

        this.model = new GameModel(config);
        this.renderer = new MapRenderer(this.model);
        this.model.getTurnManager().addPropertyChangeListener(this);
        model.startGame();
        ScreenManager.getInstance().getPanel().setBackgroundPainter(
            renderer::drawLetterboxSand
        );
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Constants.PLAYER_ADVANCED)) {
            // remove all layers till the player layer
          //  app.popLayer();

        }
    }
    @Override
    public void onEnter() {
        super.onEnter();
    }

    @Override
    public void render(Graphics2D g) {
        renderer.draw(g);
    }

    @Override
    public void update(float deltaTime) {
        renderer.update(deltaTime);
    }

    @Override
    public void onExit() {
        super.onExit();
        ScreenManager.getInstance().getPanel().setBackgroundPainter(null);
    }

    private boolean openCisternMenu(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            Player player = model.getTurnManager().getCurrentPlayer();
            Element element = player.getCurrentPosition();
            if (element instanceof Cistern cistern && player instanceof Plumber plumber) {
                {
                  CisternPickupOverlay cisternPickupOverlay =  new CisternPickupOverlay(plumber, cistern);
                    cisternPickupOverlay.setListener(new CisternPickupOverlay.PickupListener() {
                        @Override
                        public void onConfirm(boolean tookPump, boolean tookPipe) {
                            if(tookPump) {
                                plumber.pickUpPump(cistern);
                            }
                            if(tookPipe) {
                                plumber.pickUpPipe(cistern);
                            }
                            // on saving close overlay
                            app.popLayer();
                            }

                        @Override
                        public void onDiscard() {
                            // close overlay
                            app.popLayer();
                        }
                    });
                  app.pushLayer(cisternPickupOverlay);
                }
                return true;
            }
        }
            return false;
    }

    private boolean onPlay(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F) {
            Player player = model.getTurnManager().getCurrentPlayer();
           boolean done = player.doMainAction();
            if (player instanceof Plumber && done) {
                AudioPlayer.getInstance().playEffect("pipe_repair");
            }
            if (player instanceof Saboteur && done) {
                AudioPlayer.getInstance().playEffect("pipe_break");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
            if (model.getState() == GameState.RUNNING) {
                model.pauseGame();
                PauseOverlay overlay = new PauseOverlay();
                overlay.setResumeAction(() -> {
                    model.resumeGame();
                    app.popLayer();
                });
                overlay.setQuitAction(() -> {
                    model.endGame();
                    app.clearLayers();
                    app.pushLayer(new MainMenuLayer(app));
                });
                app.pushLayer(overlay);
            }
            return true;
        }
        onPlay(e);
        openCisternMenu(e);
        if (e.getKeyCode() == KeyEvent.VK_D) {
            Element element = model.getTurnManager().getCurrentPlayer().getCurrentPosition();
            if (!(element instanceof Pump)) {
                return false;
            }

            app.pushLayer(new PumpDirectionOverlay(app, (Pump) element));
            return true;
        }
        return false;

    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        renderer.mousePressed(e);
        return true;
    }
}
