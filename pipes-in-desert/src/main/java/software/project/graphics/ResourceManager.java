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
        spriteManager.loadSprite("menu_background", "/ui/menu_background.png");
        spriteManager.loadSprite("menu_panel", "/ui/menu_panel.png");
        spriteManager.loadSprite("menu_title", "/ui/menu_title_two.png");
    }

    private void loadSpriteSheets() {
        spriteManager.loadSpriteSheet("menu_animation", "/ui/menu_background_atlas.jpg", 1280, 720);
        spriteManager.loadSpriteSheet("buttons", "/ui/button_atlas.png", MenuButton.BASE_WIDTH, MenuButton.BASE_HEIGHT);
    }

    private void loadSounds() {
        audioPlayer.loadEffect("button_pressed", "/audio/button_pressed.wav");
    }
}