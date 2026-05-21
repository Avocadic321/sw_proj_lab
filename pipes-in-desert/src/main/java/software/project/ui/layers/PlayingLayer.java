package software.project.ui.layers;

import software.project.core.GameConfig;
import software.project.core.GameModel;
import software.project.ui.renderer.MapRenderer;

import java.awt.*;

public class PlayingLayer extends Layer {
    private final GameModel model;
    private final MapRenderer renderer;

    public PlayingLayer() {
        super(false, false); // non‑modal
        GameConfig config = new GameConfig();

        this.model = new GameModel(config);
        this.renderer = new MapRenderer();

        model.startGame();
    }

    @Override
    public void render(Graphics2D g) {
        renderer.draw(g, model);
    }
}