package software.project.graphics;

import software.project.audio.AudioPlayer;
import software.project.ui.components.MenuButton;

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
        for (Sprites sprite : Sprites.values()) {
            spriteManager.loadSprite(sprite);
        }
    }

    private void loadSpriteSheets() {
        for (SpriteSheets sheet : SpriteSheets.values()) {
            spriteManager.loadSpriteSheet(sheet);
        }
    }

    private void loadSounds() {
        audioPlayer.loadEffect("button_pressed", "/audio/button_pressed.wav");
        audioPlayer.loadSong("main_theme","/audio/pipes_desert_theme.wav");
    }
}
