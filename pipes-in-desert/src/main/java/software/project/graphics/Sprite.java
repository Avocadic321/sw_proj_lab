package software.project.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {
    private BufferedImage image;
    private int width, height;

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

    public BufferedImage getImage() { return image; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
