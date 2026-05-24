package software.project.ui.layers;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.renderer.MapRenderer;
import software.project.utils.GameState;

public class PlayingLayer extends Layer {
    private final GameApplication app;
    private final GameModel model;
    private final MapRenderer renderer;

    public PlayingLayer(GameApplication app) {
        super(false, false); // non‑modal
        this.app = app;
        GameConfig config = new GameConfig();

        this.model = new GameModel(config);
        this.renderer = new MapRenderer();

        model.startGame();

        ScreenManager.getInstance().getPanel().setBackgroundPainter(
            renderer::drawLetterboxSand
        );
    }

    @Override
    public void render(Graphics2D g) {
        renderer.draw(g, model);
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
        return false;
    }
}
