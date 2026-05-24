package software.project.ui.renderer;

import java.awt.Graphics2D;

@FunctionalInterface
public interface BackgroundPainter {
    void paint(Graphics2D g);
}
