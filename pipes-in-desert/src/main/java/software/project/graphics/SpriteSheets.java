package software.project.graphics;

import software.project.ui.components.MenuButton;

public enum SpriteSheets {
    MENU_ANIMATION("menu_animation", "/ui/menu_background_atlas.jpg", 1280, 720),
    BUTTONS("buttons", "/ui/button_atlas.png", MenuButton.BASE_WIDTH, MenuButton.BASE_HEIGHT);

    private final String key;
    private final String path;
    private final int frameWidth;
    private final int frameHeight;

    SpriteSheets(String key, String path, int frameWidth, int frameHeight) {
        this.key = key;
        this.path = path;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
}
