package software.project.graphics;

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
        loadSprites();
    }

    public void loadSprites() {
        //spriteManager.loadSprite("pipe", "/map/pipe.png");
    }
}
