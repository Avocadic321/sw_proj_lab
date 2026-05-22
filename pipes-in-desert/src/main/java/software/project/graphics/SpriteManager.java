package software.project.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteManager {
    private static SpriteManager instance;
    private Map<String, Sprite> sprites = new HashMap<>();
    private Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private boolean showWarnings = true;

    private SpriteManager() {
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    /**
     * Loads an image from the classpath (resources folder).
     * 
     * @param key  unique identifier for the sprite
     * @param path path inside resources, e.g. "/sprites/pipe.png"
     */
    public void loadSprite(String key, String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                if (showWarnings) {
                    System.err.println("[WARNING] Sprite not found: " + path + " for key: " + key);
                }
                return;
            }
            BufferedImage img = ImageIO.read(is);
            sprites.put(key, new Sprite(img));
            if (showWarnings) {
                System.out.println("[INFO] Loaded sprite: " + key + " from " + path);
            }
        } catch (IOException e) {
            if (showWarnings) {
                System.err.println("[ERROR] Failed to load sprite: " + key + " from " + path);
                e.printStackTrace();
            }
        }
    }

    public void loadSpriteSheet(String key, String path, int frameWidth, int frameHeight) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                if (showWarnings) {
                    System.err.println("[ERROR] SpriteSheet not found: " + path + " for key: " + key);
                }
                return;
            }
            BufferedImage sheetImage = ImageIO.read(is);
            if (sheetImage == null) {
                if (showWarnings) {
                    System.err.println("[ERROR] Failed to read image: " + path + " for key: " + key);
                }
                return;
            }
            System.out.println("[INFO] Loaded sprite sheet: " + key + " size=" + sheetImage.getWidth() + "x"
                    + sheetImage.getHeight());
            SpriteSheet sheet = new SpriteSheet(sheetImage, frameWidth, frameHeight);
            spriteSheets.put(key, sheet);
        } catch (IOException e) {
            if (showWarnings) {
                System.err.println("[ERROR] IOException loading " + path + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Sprite getSprite(String key) {
        Sprite sprite = sprites.get(key);
        if (sprite == null && showWarnings) {
            System.err.println("[WARNING] Sprite not found for key: " + key);
        }
        return sprite;
    }

    public Sprite getSpriteFromSheet(String sheetKey, int col, int row) {
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

    public Sprite getSpriteFromSheet(String sheetKey, int index) {
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

    public SpriteSheet getSpriteSheet(String key) {
        SpriteSheet sheet = spriteSheets.get(key);
        if (sheet == null && showWarnings) {
            System.err.println("[WARNING] SpriteSheet not found for key: " + key);
        }
        return sheet;
    }

    // Check if a sprite exists without printing warnings
    public boolean hasSprite(String key) {
        return sprites.containsKey(key) && sprites.get(key) != null;
    }

    // Check if a sprite sheet exists without printing warnings
    public boolean hasSpriteSheet(String key) {
        return spriteSheets.containsKey(key) && spriteSheets.get(key) != null;
    }
}
