package software.project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private final GameApplication app;

    public GamePanel(GameApplication app) {
        this.app = app;
        setFocusable(true);
        setPreferredSize(new Dimension(GameApplication.WIDTH, GameApplication.HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        app.render((Graphics2D) g);
    }

    // Keyboard Events
    @Override
    public void keyPressed(KeyEvent e) {
        app.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        app.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) { /* not used */}

    // Mouse Events
    @Override
    public void mousePressed(MouseEvent e) {
        app.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        app.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        app.mouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        app.mouseDragged(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) { /* not used */}

    @Override
    public void mouseEntered(MouseEvent e) { /* not used */ }

    @Override
    public void mouseExited(MouseEvent e) { /* not used */ }
}
