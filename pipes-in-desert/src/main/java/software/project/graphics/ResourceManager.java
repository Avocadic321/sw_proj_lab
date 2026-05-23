package software.project.graphics;

import software.project.audio.AudioPlayer;
import software.project.utils.SpriteSheetsEnum;
import software.project.utils.SpritesEnum;

public class ResourceManager {
    private static ResourceManager instance;
    private SpriteManager spriteManager;
    private AudioPlayer audioPlayer;

    private ResourceManager() {
        spriteManager = SpriteManager.getInstance();
        audioPlayer = AudioPlayer.getInstance();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public void loadAllResources() {
        System.out.println("[INFO] Loading all resources");
        loadSprites();
        loadSpriteSheets();
        loadSounds();
        System.out.println("[INFO] Resources loaded");
    }

    private void loadSprites() {
        for (SpritesEnum sprite : SpritesEnum.values()) {
            spriteManager.loadSprite(sprite);
        }
    }

    private void loadSpriteSheets() {
        for (SpriteSheetsEnum sheet : SpriteSheetsEnum.values()) {
            spriteManager.loadSpriteSheet(sheet);
        }
    }

    private void loadSounds() {
        audioPlayer.loadEffect("button_pressed", "/audio/button_pressed.wav");
    }
}
