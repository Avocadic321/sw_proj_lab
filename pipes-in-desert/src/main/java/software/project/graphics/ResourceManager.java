package software.project.graphics;

import software.project.ui.components.MenuButton;

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
        spriteManager.loadSprite("menu_panel", "/ui/menu_panel.png");
        spriteManager.loadSprite("menu_title", "/ui/menu_title.png");
    }

    private void loadButtons() {
        spriteManager.loadSpriteSheet(
            "buttons",
            "/ui/button_atlas.png",
            MenuButton.BASE_WIDTH,
            MenuButton.BASE_HEIGHT
        );
    }

}
