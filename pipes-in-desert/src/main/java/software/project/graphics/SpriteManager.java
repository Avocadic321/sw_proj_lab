package software.project.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private static SpriteManager instance;
    private final Map<String, Sprite> sprites = new HashMap<>();
    private final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private final boolean showWarnings = true;

    private SpriteManager() {
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    public void loadSprite(Sprites sprite) {
        String path = sprite.getPath();
        String key = sprite.getKey();
        BufferedImage img = ResourceLoader.loadImage(path);
        if (img == null) {
            if (showWarnings) System.err.println("[WARNING] Sprite not found: " + path + " for key: " + key);
            return;
        }
        sprites.put(key, new Sprite(img));
        if (showWarnings) System.out.println("[INFO] Loaded sprite: " + key + " from " + path);
    }

    public void loadSpriteSheet(SpriteSheets spriteSheet) {
        String key = spriteSheet.getKey();
        String path = spriteSheet.getPath();
        BufferedImage sheetImage = ResourceLoader.loadImage(path);
        if (sheetImage == null) {
            if (showWarnings) System.err.println("[ERROR] SpriteSheet not found: " + path + " for key: " + key);
            return;
        }
        System.out.println("[INFO] Loaded sprite sheet: " + key + " size=" + sheetImage.getWidth() + "x" + sheetImage.getHeight());
        SpriteSheet sheet = new SpriteSheet(sheetImage, spriteSheet.getFrameWidth(), spriteSheet.getFrameHeight());
        spriteSheets.put(key, sheet);
    }

    public Sprite getSprite(Sprites s) {
        String key = s.getKey();
        Sprite sprite = sprites.get(key);
        if (sprite == null && showWarnings) {
            System.err.println("[WARNING] Sprite not found for key: " + key);
        }
        return sprite;
    }

    public Sprite getSpriteFromSheet(SpriteSheets s, int col, int row) {
        String sheetKey = s.getKey();
        SpriteSheet sheet = spriteSheets.get(sheetKey);
        if (sheet == null) {
            if (showWarnings) {
                System.err.println("[WARNING] SpriteSheet not found for key: " + sheetKey);
            }
            return null;
        }
        Sprite sprite = sheet.getSprite(col, row);
        if (sprite == null && showWarnings) {
            System.err.println("[WARNING] Sprite not found in sheet " + sheetKey + " at col=" + col + ", row=" + row);
        }
        return sprite;
    }

    public Sprite getSpriteFromSheet(SpriteSheets s, int index) {
        String sheetKey = s.getKey();
        SpriteSheet sheet = spriteSheets.get(sheetKey);
        if (sheet == null) {
            if (showWarnings) {
                System.err.println("[WARNING] SpriteSheet not found for key: " + sheetKey);
            }
            return null;
        }
        Sprite sprite = sheet.getSprite(index);
        if (sprite == null && showWarnings) {
            System.err.println("[WARNING] Sprite not found in sheet " + sheetKey + " at index=" + index);
        }
        return sprite;
    }

    public SpriteSheet getSpriteSheet(SpriteSheets s) {
        String key = s.getKey();
        SpriteSheet sheet = spriteSheets.get(key);
        if (sheet == null && showWarnings) {
            System.err.println("[WARNING] SpriteSheet not found for key: " + key);
        }
        return sheet;
    }

    // Check if a sprite exists without printing warnings
    public boolean hasSprite(Sprites s) {
        String key = s.getKey();
        return sprites.containsKey(key) && sprites.get(key) != null;
    }

    // Check if a sprite sheet exists without printing warnings
    public boolean hasSpriteSheet(SpriteSheets s) {
        String key = s.getKey();
        return spriteSheets.containsKey(key) && spriteSheets.get(key) != null;
    }
}
