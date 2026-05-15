package software.project.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private final BufferedImage image;
    private final int frameWidth;
    private final int frameHeight;
    private final int cols;
    private final int rows;
    private final List<Sprite> sprites = new ArrayList<>();

    public SpriteSheet(BufferedImage image, int frameWidth, int frameHeight) {
        this.image = image;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.cols = image.getWidth() / frameWidth;
        this.rows = image.getHeight() / frameHeight;
        loadSprites();
    }

    private void loadSprites() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                BufferedImage sub = image.getSubimage(col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                sprites.add(new Sprite(sub));
            }
        }
    }

    public Sprite getSprite(int col, int row) {
        int index = row * cols + col;
        if (index < 0 || index >= sprites.size()) {
            return null;
        }
        return sprites.get(index);
    }
}
