package software.project.ui;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.renderer.BackgroundPainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private BufferedImage virtualBuffer;
    private int lastBufferW = -1;
    private int lastBufferH = -1;
    private GameApplication app;

    BackgroundPainter backgroundPainter;

    public GamePanel() {
        setFocusable(true);
        setBackground(new Color(20, 30, 50));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setApp(GameApplication app) {
        this.app = app;
    }

    public void setBackgroundPainter(BackgroundPainter painter) {
        this.backgroundPainter = painter;
    }

    private void ensureBuffer() {
        int w = ScreenManager.GAME_WIDTH; // fixed
        int h = ScreenManager.GAME_HEIGHT;
        if (virtualBuffer == null || lastBufferW != w || lastBufferH != h) {
            virtualBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            lastBufferW = w;
            lastBufferH = h;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundPainter != null) {
            backgroundPainter.paint(g2d);
        }

        ensureBuffer();
        renderVirtual();
        drawVirtualBuffer(g2d);
    }

    private void renderVirtual() {
        Graphics2D g = virtualBuffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, virtualBuffer.getWidth(), virtualBuffer.getHeight());
        if (app != null)
            app.render(g);
        g.dispose();
    }

    private void drawVirtualBuffer(Graphics2D screenG) {
        int panelW = getWidth();
        int panelH = getHeight();
        int bufW = virtualBuffer.getWidth();
        int bufH = virtualBuffer.getHeight();

        if (ScreenManager.getInstance().getScalingMode() == ScreenManager.ScalingMode.STRETCH) {
            screenG.drawImage(virtualBuffer, 0, 0, panelW, panelH, null);
        } else { // LETTERBOX – preserve aspect ratio
            double scaleX = (double) panelW / bufW;
            double scaleY = (double) panelH / bufH;
            double scale = Math.min(scaleX, scaleY);
            int drawW = (int) (bufW * scale);
            int drawH = (int) (bufH * scale);
            int offsetX = (panelW - drawW) / 2;
            int offsetY = (panelH - drawH) / 2;
            screenG.drawImage(virtualBuffer, offsetX, offsetY, drawW, drawH, null);
        }
    }

    // Input transformation – convert screen coordinates to virtual coordinates
    private Point transformScreenToVirtual(int screenX, int screenY) {
        int panelW = getWidth();
        int panelH = getHeight();
        int bufW = virtualBuffer.getWidth();
        int bufH = virtualBuffer.getHeight();

        if (ScreenManager.getInstance().getScalingMode() == ScreenManager.ScalingMode.STRETCH) {
            double scaleX = (double) panelW / bufW;
            double scaleY = (double) panelH / bufH;
            return new Point((int) (screenX / scaleX), (int) (screenY / scaleY));
        } else {
            double scaleX = (double) panelW / bufW;
            double scaleY = (double) panelH / bufH;
            double scale = Math.min(scaleX, scaleY);
            int drawW = (int) (bufW * scale);
            int drawH = (int) (bufH * scale);
            int offsetX = (panelW - drawW) / 2;
            int offsetY = (panelH - drawH) / 2;
            int virtualX = (int) ((screenX - offsetX) / scale);
            int virtualY = (int) ((screenY - offsetY) / scale);
            return new Point(
                    Math.max(0, Math.min(virtualX, bufW - 1)),
                    Math.max(0, Math.min(virtualY, bufH - 1)));
        }
    }

    private MouseEvent transformMouseEvent(MouseEvent e) {
        Point p = transformScreenToVirtual(e.getX(), e.getY());
        return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(),
                e.getModifiersEx(), p.x, p.y, e.getClickCount(),
                e.isPopupTrigger(), e.getButton());
    }

    // Input forwarding
    @Override
    public void keyPressed(KeyEvent e) {
        if (app != null)
            app.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (app != null)
            app.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (app != null)
            app.mousePressed(transformMouseEvent(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (app != null)
            app.mouseReleased(transformMouseEvent(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (app != null)
            app.mouseMoved(transformMouseEvent(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (app != null)
            app.mouseDragged(transformMouseEvent(e));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
