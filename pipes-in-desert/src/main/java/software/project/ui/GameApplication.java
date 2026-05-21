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
    private static final int TARGET_UPS = 60;
    private static final int TARGET_FPS = 60;
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
        startMainLoop();
    }

    private void initLayers() {
        pushLayer(new MainMenuLayer(this));
    }

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

    @Override
    public void onResolutionChanged(int newVirtualWidth, int newVirtualHeight) {
        if (!layerStack.isEmpty()) {
            layerStack.getLast().onResolutionChanged(newVirtualWidth, newVirtualHeight);
        }
    }

    private void startMainLoop() {
        gameThread = new Thread(this::runMainLoop);
        gameThread.start();
    }

    private void runMainLoop() {
        final float DT = 1f / TARGET_UPS;
        final long NANO_PER_UPDATE = (long)(DT * 1_000_000_000);
        final long NANO_PER_FRAME = TARGET_FPS > 0 ? 1_000_000_000 / TARGET_FPS : 0;

        long lastUpdateTime = System.nanoTime();
        long lastRenderTime = System.nanoTime();
        long accumulator = 0;

        while (running) {
            long now = System.nanoTime();
            long elapsed = Math.min(now - lastUpdateTime, NANO_PER_UPDATE * 5);
            lastUpdateTime = now;
            accumulator += elapsed;

            int updates = 0;
            while (accumulator >= NANO_PER_UPDATE && updates < 5) {
                update(DT);
                accumulator -= NANO_PER_UPDATE;
                updates++;
            }

            long nowRender = System.nanoTime();
            if (TARGET_FPS == 0 || (nowRender - lastRenderTime) >= NANO_PER_FRAME) {
                ScreenManager.getInstance().getPanel().repaint();
                lastRenderTime = nowRender;
            } else {
                // Yield CPU when neither update nor render is due
                long sleepNs = NANO_PER_FRAME - (nowRender - lastRenderTime);
                if (sleepNs > 0) {
                    long sleepMs = sleepNs / 1_000_000;
                    int sleepNsRem = (int)(sleepNs % 1_000_000);
                    try {
                        Thread.sleep(sleepMs, sleepNsRem);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private void update(float deltaTime) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            layer.update(deltaTime);
            if (layer.blocksUpdate()) {
                break;  // stop updating layers below
            }
        }
    }

    public void render(Graphics2D g) {
        for (Layer layer : layerStack) {
            layer.render(g);
        }
    }

    public void keyPressed(KeyEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.keyPressed(e)) break;
            if (layer.blocksInput()) break;
        }
    }

    public void keyReleased(KeyEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.keyReleased(e)) break;
            if (layer.blocksInput()) break;
        }
    }

    public void mousePressed(MouseEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.mousePressed(e)) break;
            if (layer.blocksInput()) break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.mouseReleased(e)) break;
            if (layer.blocksInput()) break;
        }
    }

    public void mouseMoved(MouseEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.mouseMoved(e)) break;
            if (layer.blocksInput()) break;
        }
    }

    public void mouseDragged(MouseEvent e) {
        for (int i = layerStack.size() - 1; i >= 0; i--) {
            Layer layer = layerStack.get(i);
            if (layer.mouseDragged(e)) break;
            if (layer.blocksInput()) break;
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