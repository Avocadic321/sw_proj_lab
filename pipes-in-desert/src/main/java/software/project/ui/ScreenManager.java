package software.project.ui;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ScreenManager {
    public static final int GAME_WIDTH = 1280;
    public static final int GAME_HEIGHT = 800;
    private static ScreenManager instance;
    // Listeners for resolution changes
    private final List<ResolutionListener> listeners = new ArrayList<>();
    // Virtual resolution (game coordinates)
    private final int virtualWidth = GAME_WIDTH;
    private final int virtualHeight = GAME_HEIGHT;
    private JFrame frame;
    private GamePanel panel;
    private ScalingMode scalingMode = ScalingMode.LETTERBOX;

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void createWindow(String title, int initialWidth, int initialHeight) {
        frame = new JFrame(title);
        panel = new GamePanel();
        frame.add(panel);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(480, 360));
        frame.setSize(initialWidth, initialHeight);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateVirtualResolution();
            }
        });
        updateVirtualResolution();
        panel.requestFocusInWindow();
    }

    private void updateVirtualResolution() {
        // Notify all listeners
        for (ResolutionListener listener : listeners) {
            listener.onResolutionChanged(virtualWidth, virtualHeight);
        }
    }

    public void addResolutionListener(ResolutionListener listener) {
        listeners.add(listener);
    }

    public void removeResolutionListener(ResolutionListener listener) {
        listeners.remove(listener);
    }

    public ScalingMode getScalingMode() {
        return scalingMode;
    }

    public void setScalingMode(ScalingMode mode) {
        this.scalingMode = mode;
        updateVirtualResolution();
        panel.repaint();
    }

    public void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                   .getDefaultScreenDevice();
        if (frame.isUndecorated()) {
            device.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
        } else {
            frame.dispose();
            frame.setUndecorated(true);
            frame.setVisible(true);
            device.setFullScreenWindow(frame);
        }
        panel.requestFocusInWindow();
        updateVirtualResolution();
    }

    public JFrame getFrame() {
        return frame;
    }

    public GamePanel getPanel() {
        return panel;
    }

    public int getVirtualWidth() {
        return virtualWidth;
    }

    public int getVirtualHeight() {
        return virtualHeight;
    }

    // Scaling mode (LETTERBOX = black bars, STRETCH = fill)
    public enum ScalingMode {
        LETTERBOX, STRETCH
    }

    public interface ResolutionListener {
        void onResolutionChanged(int newVirtualWidth, int newVirtualHeight);
    }
}
