package software.project.ui;

import software.project.ui.layers.Layer;
import software.project.graphics.ResourceManager;
import software.project.ui.layers.MainMenuLayer;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameApplication implements ScreenManager.ResolutionListener {
    private final int TARGET_UPS = 60;
    private final int TARGET_FPS = 60;
    private final List<Layer> layerStack = new ArrayList<>();
    private Thread gameThread;
    private volatile boolean running = true;

    public GameApplication() {
        // Load all assets before showing the window
        ResourceManager.getInstance().loadAllResources();

        // Create the window and set up screen manager
        ScreenManager sm = ScreenManager.getInstance();
        sm.createWindow("Pipes in the Desert", 800, 600);
        sm.getPanel().setApp(this);          // give GamePanel a reference to this
        sm.addResolutionListener(this);      // listen for resolution changes

        // Push the initial layer
        initLayers();

        // Start the game loop
        startGameLoop();
    }

    private void initLayers() {
        pushLayer(new MainMenuLayer(this));   // pass this reference for layer navigation
    }

    // --- Layer stack management ---
    public void pushLayer(Layer layer) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().onExit();
        }
        layerStack.add(layer);
        layer.onEnter();
        // Immediately notify of current resolution
        ScreenManager sm = ScreenManager.getInstance();
        layer.onResolutionChanged(sm.getVirtualWidth(), sm.getVirtualHeight());
    }

    public void popLayer() {
        if (layerStack.isEmpty()) return;
        Layer top = layerStack.removeLast();
        top.onExit();
        if (!layerStack.isEmpty()) {
            Layer newTop = layerStack.getLast();
            newTop.onEnter();
            ScreenManager sm = ScreenManager.getInstance();
            newTop.onResolutionChanged(sm.getVirtualWidth(), sm.getVirtualHeight());
        }
    }

    public void replaceLayer(Layer newLayer) {
        popLayer();
        pushLayer(newLayer);
    }

    public void clearLayers() {
        while (!layerStack.isEmpty()) popLayer();
    }

    // --- Resolution listener ---
    @Override
    public void onResolutionChanged(int newVirtualWidth, int newVirtualHeight) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().onResolutionChanged(newVirtualWidth, newVirtualHeight);
        }
    }

    // --- Game loop ---
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
                ScreenManager.getInstance().getPanel().repaint();
                deltaF -= 1;
            }
        }
    }

    private void update(float deltaTime) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().update(deltaTime);
        }
    }

    // --- Rendering (called from GamePanel) ---
    public void render(Graphics2D g) {
        for (Layer layer : layerStack) {
            layer.render(g);
        }
    }

    // --- Input forwarding (called from GamePanel after transformation) ---
    public void keyPressed(KeyEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().keyReleased(e);
    }

    public void mousePressed(MouseEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().mouseReleased(e);
    }

    public void mouseMoved(MouseEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (!layerStack.isEmpty()) layerStack.getLast().mouseDragged(e);
    }

    // --- Shutdown ---
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