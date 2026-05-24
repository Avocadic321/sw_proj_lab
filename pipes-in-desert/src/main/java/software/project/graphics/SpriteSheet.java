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
    private boolean valid = true;

    public SpriteSheet(BufferedImage image, int frameWidth, int frameHeight) {
        this.image = image;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        if (image == null) {
            System.err.println("[ERROR] Cannot create SpriteSheet: image is null");
            this.valid = false;
            this.cols = 0;
            this.rows = 0;
            return;
        }

        this.cols = image.getWidth() / frameWidth;
        this.rows = image.getHeight() / frameHeight;

        if (cols == 0 || rows == 0) {
            System.err.println("[ERROR] Cannot create SpriteSheet: invalid dimensions. Image=" +
                                   image.getWidth() + "x" + image.getHeight() +
                                   ", Frame=" + frameWidth + "x" + frameHeight);
            this.valid = false;
            return;
        }

        loadSprites();
        System.out.println("[INFO] Created SpriteSheet with " + cols + "x" + rows +
                               " frames (" + sprites.size() + " total)");
    }

    private void loadSprites() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                try {
                    BufferedImage sub = image.getSubimage(col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                    sprites.add(new Sprite(sub));
                } catch (Exception e) {
                    System.err.println(
                        "[ERROR] Failed to extract sprite at col=" + col + ", row=" + row + ": " + e.getMessage());
                    sprites.add(null);
                }
            }
        }
    }

    public boolean isValid() {
        return valid && !sprites.isEmpty();
    }

    public Sprite getSprite(int col, int row) {
        if (!isValid()) {
            System.err.println("[WARNING] Cannot get sprite from invalid SpriteSheet");
            return null;
        }

        int index = row * cols + col;
        if (index < 0 || index >= sprites.size()) {
            System.err.println("[WARNING] Sprite index out of bounds: col=" + col +
                                   ", row=" + row + " (index=" + index + ", size=" + sprites.size() + ")");
            return null;
        }

        return sprites.get(index);
    }

    public Sprite getSprite(int index) {
        if (!isValid()) {
            System.err.println("[WARNING] Cannot get sprite from invalid SpriteSheet");
            return null;
        }

        if (index < 0 || index >= sprites.size()) {
            System.err.println("[WARNING] Sprite index out of bounds: index=" + index +
                                   ", size=" + sprites.size());
            return null;
        }

        return sprites.get(index);
    }

    public List<Sprite> getRow(int row) {
        List<Sprite> rowSprites = new ArrayList<>();
        if (!isValid()) {
            return rowSprites;
        }

        if (row < 0 || row >= rows) {
            System.err.println("[WARNING] Row out of bounds: row=" + row);
            return rowSprites;
        }

        for (int col = 0; col < cols; col++) {
            rowSprites.add(getSprite(col, row));
        }
        return rowSprites;
    }

    public List<Sprite> getColumn(int col) {
        List<Sprite> colSprites = new ArrayList<>();
        if (!isValid()) {
            return colSprites;
        }

        if (col < 0 || col >= cols) {
            System.err.println("[WARNING] Column out of bounds: col=" + col);
            return colSprites;
        }

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

    public int getTotalSprites() {
        return sprites.size();
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
}
