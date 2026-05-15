package software.project.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private static SpriteManager instance;
    private Map<String, Sprite> sprites = new HashMap<>();
    private Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private SpriteManager() {}

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    /**
     * Loads an image from the classpath (resources folder).
     * @param key unique identifier for the sprite
     * @param path path inside resources, e.g. "/sprites/pipe.png"
     */
    public void loadSprite(String key, String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Sprite not found: " + path);
                return;
            }
            BufferedImage img = ImageIO.read(is);
            sprites.put(key, new Sprite(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSpriteSheet(String key, String path, int frameWidth, int frameHeight) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("[ERROR] SpriteSheet not found: " + path);
                return;
            }
            BufferedImage sheetImage = ImageIO.read(is);
            if (sheetImage == null) {
                System.err.println("[ERROR] Failed to read image: " + path);
                return;
            }
            System.out.println("[INFO] Loaded sprite sheet: " + key + " size=" + sheetImage.getWidth() + "x" + sheetImage.getHeight());
            SpriteSheet sheet = new SpriteSheet(sheetImage, frameWidth, frameHeight);
            spriteSheets.put(key, sheet);
        } catch (IOException e) {
            System.err.println("[ERROR] IOException loading " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Sprite getSprite(String key) {
        return sprites.get(key);
    }

    public Sprite getSpriteFromSheet(String sheetKey, int col, int row) {
        SpriteSheet sheet = spriteSheets.get(sheetKey);
        if (sheet == null) {
            return null;
        }
        return sheet.getSprite(col, row);
    }

    public SpriteSheet getSpriteSheet(String key) {
        return spriteSheets.get(key);
    }
}
