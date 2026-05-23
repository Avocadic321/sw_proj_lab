package software.project.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    private BufferedImage image;
    private final int width;
    private final int height;

    public Sprite(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    // Draw at top-left (x, y) with original size
    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    // Draw at top-left (x, y) with scaling
    public void draw(Graphics2D g, int x, int y, int targetWidth, int targetHeight) {
        g.drawImage(image, x, y, targetWidth, targetHeight, null);
    }

    // Draw rotated around its centre, using original size, top-left (x, y)
    public void draw(Graphics2D g, int x, int y, double angleDegrees) {
        AffineTransform original = g.getTransform();
        g.translate(x + width / 2.0, y + height / 2.0);
        g.rotate(Math.toRadians(angleDegrees));
        g.drawImage(image, -width / 2, -height / 2, null);
        g.setTransform(original);
    }

    // Draw scaled and rotated, centred at (centerX, centerY)
    public void drawCentered(Graphics2D g, int centerX, int centerY, int targetWidth, int targetHeight, double angleDegrees) {
        AffineTransform original = g.getTransform();
        g.translate(centerX, centerY);
        g.rotate(Math.toRadians(angleDegrees));
        g.drawImage(image, -targetWidth / 2, -targetHeight / 2, targetWidth, targetHeight, null);
        g.setTransform(original);
    }

    // Convenience for square scaling
    public void drawCentered(Graphics2D g, int centerX, int centerY, int targetSize, double angleDegrees) {
        drawCentered(g, centerX, centerY, targetSize, targetSize, angleDegrees);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}