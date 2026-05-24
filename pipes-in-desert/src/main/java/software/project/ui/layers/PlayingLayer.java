package software.project.ui.layers;

import software.project.audio.AudioPlayer;
import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.core.GameState;
import software.project.map.Pump;
import software.project.models.Player;
import software.project.models.Plumber;
import software.project.models.Saboteur;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.renderer.MapRenderer;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PlayingLayer extends Layer {
    private final GameApplication app;
    private final GameModel model;
    private final MapRenderer renderer;

    public PlayingLayer(GameApplication app) {
        super(false, false); // non‑modal
        this.app = app;
        GameConfig config = new GameConfig();

        this.model = new GameModel(config);
        this.renderer = new MapRenderer(this.model);

        model.startGame();

        ScreenManager.getInstance().getPanel().setBackgroundPainter(
            renderer::drawLetterboxSand
        );
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

    private void onPlay(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F) {
            Player player = model.getTurnManager().getCurrentPlayer();
           boolean done = player.doMainAction();
            if (player instanceof Plumber && done) {
                AudioPlayer.getInstance().playEffect("pipe_repair");
            }
            if (player instanceof Saboteur && done) {
                AudioPlayer.getInstance().playEffect("pipe_break");
            }
        }
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
//        if(e.getKeyCode() == KeyEvent.VK_E) {
//            Player player = model.getTurnManager().getCurrentPlayer();
//            if(player.getCurrentPosition() instanceof Pump p) {
//               var x =  this.model.getGameMap().getAllPipes().stream().filter(f -> f.getId().equals("PIPE3")).findAny();
//               if(x.isPresent()){
//                 p.setDirection(p.getInputPipe(),x.get().getEnd1());
//               }
//            }
//        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            Pump pump = (Pump) model.getGameMap().getElement("PUMP2");
            app.pushLayer(new PumpDirectionOverlay(app, pump));
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
