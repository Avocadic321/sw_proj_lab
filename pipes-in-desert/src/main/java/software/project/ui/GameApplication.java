package software.project.ui;

import software.project.ui.layers.Layer;
import software.project.graphics.ResourceManager;
import software.project.ui.layers.MainMenuLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * Main application window and game loop.
 * Manages a stack of {@link Layer} (screens/overlays) and forwards input/updates.
 */
public class GameApplication {
    private final int TARGET_UPS = 60;
    private final int TARGET_FPS = 60;

    public static final int DEFAULT_TILE_SIZE = 32;
    public static final float SCALE = 2f;
    public static final int MAP_WIDTH_TILES = 15;
    public static final int MAP_HEIGHT_TILES = 10;
    public static final int TILE_SIZE = (int) (DEFAULT_TILE_SIZE * SCALE);
    public static final int WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
    public static final int HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;

    private static final String TITLE = "Pipes in the Desert";

    private JFrame frame;
    private GamePanel panel;

    private final List<Layer> layerStack = new ArrayList<>();

    private Thread gameThread;
    private volatile boolean running = true;

    public GameApplication() {
        // Load all assets (sprites, buttons, sounds) before showing the window
        ResourceManager.getInstance().loadAllResources();

        initWindow();
        initLayers();
        startGameLoop();
    }

    /**
     * Creates the JFrame and the GamePanel.
     */
    private void initWindow() {
        frame = new JFrame(TITLE);
        panel = new GamePanel(this);          // top-level class, not inner
        frame.add(panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();                         // sizes to panel's preferred size
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    /**
     * Pushes the initial layer (e.g., main menu).
     */
    private void initLayers() {
        pushLayer(new MainMenuLayer(this));
    }

    public void pushLayer(Layer layer) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().onExit();
        }
        layerStack.add(layer);
        layer.onEnter();
    }

    public void popLayer() {
        if (layerStack.isEmpty()) return;
        Layer top = layerStack.removeLast();
        top.onExit();
        if (!layerStack.isEmpty()) {
            layerStack.getLast().onEnter();
        }
    }

    public void replaceLayer(Layer newLayer) {
        popLayer();
        pushLayer(newLayer);
    }

    public void clearLayers() {
        while (!layerStack.isEmpty()) popLayer();
    }


    private void startGameLoop() {
        gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        double timePerUpdate = 1_000_000_000.0 / TARGET_UPS;
        double timePerFrame  = 1_000_000_000.0 / TARGET_FPS;
        long lastTime = System.nanoTime();
        double deltaU = 0;
        double deltaF = 0;

        while (running) {
            long now = System.nanoTime();
            deltaU += (now - lastTime) / timePerUpdate;
            deltaF += (now - lastTime) / timePerFrame;
            lastTime = now;

            if (deltaU >= 1) {
                float deltaTime = (float)(timePerUpdate / 1_000_000_000.0);
                update(deltaTime);
                deltaU -= 1;
            }
            if (deltaF >= 1) {
                panel.repaint();   // triggers paintComponent -> render()
                deltaF -= 1;
            }
        }
    }

    private void update(float deltaTime) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().update(deltaTime);
        }
    }

    public void render(Graphics2D g) {
        for (Layer layer : layerStack) {
            layer.render(g);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().keyPressed(e);
        }
    }
    public void keyReleased(KeyEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().keyReleased(e);
        }
    }
    public void mousePressed(MouseEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().mousePressed(e);
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().mouseReleased(e);
        }
    }
    public void mouseMoved(MouseEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().mouseMoved(e);
        }
    }
    public void mouseDragged(MouseEvent e) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().mouseDragged(e);
        }
    }

    public void stop() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}