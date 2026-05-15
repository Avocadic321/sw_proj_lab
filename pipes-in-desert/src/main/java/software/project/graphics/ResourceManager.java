package software.project.graphics;

import software.project.ui.components.MenuButton;

import java.awt.*;

public class ResourceManager {
    private static ResourceManager instance;
    private SpriteManager spriteManager;

    private ResourceManager() {
        spriteManager = SpriteManager.getInstance();
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
        loadButtons();
        System.out.println("[INFO] Resources loaded");
    }

    public void loadSprites() {
        spriteManager.loadSprite("menu_background", "/ui/menu_background.png");
    }

    private SpriteSheet buttonSheet;

    private void loadButtons() {
        SpriteManager sm = SpriteManager.getInstance();
        sm.loadSpriteSheet(
            "buttons",
            "/ui/button_atlas.png",
            MenuButton.BUTTON_WIDTH,
            MenuButton.BUTTON_HEIGHT
        );
    }

}
