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

    public Sprite getSprite(int index) {
        if (index < 0 || index >= sprites.size()) {
            return null;
        }
        return sprites.get(index);
    }

    public List<Sprite> getRow(int row) {
        List<Sprite> rowSprites = new ArrayList<>();
        if (row < 0 || row >= rows) return rowSprites;

        for (int col = 0; col < cols; col++) {
            rowSprites.add(getSprite(col, row));
        }
        return rowSprites;
    }

    public List<Sprite> getColumn(int col) {
        List<Sprite> colSprites = new ArrayList<>();
        if (col < 0 || col >= cols) return colSprites;

        for (int row = 0; row < rows; row++) {
            colSprites.add(getSprite(col, row));
        }
        return colSprites;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getTotalSprites() {
        return sprites.size();
    }
}
