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

    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public void draw(Graphics2D g, int x, int y, int targetWidth, int targetHeight) {
        g.drawImage(image, x, y, targetWidth, targetHeight, null);
    }

    public void draw(Graphics2D g, int x, int y, double angleDegrees) {
        AffineTransform original = g.getTransform();
        g.translate(x + width / 2.0, y + height / 2.0);
        g.rotate(Math.toRadians(angleDegrees));
        g.drawImage(image, -width / 2, -height / 2, null);
        g.setTransform(original);
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
