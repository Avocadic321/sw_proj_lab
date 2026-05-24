package software.project.graphics;

import software.project.ui.components.MenuButton;

public enum SpriteSheets {
    MENU_ANIMATION("menu_animation", "/ui/background/menu_background_atlas.jpg", 1280, 720),
    BUTTONS("buttons", "/ui/buttons/button_atlas.png", MenuButton.BASE_WIDTH, MenuButton.BASE_HEIGHT),
    PIPE_NORMAL("pipe_normal", "/map/pipe_normal.png", 64, 64),
    PIPE_BROKEN("pipe_broken", "/map/pipe_broken.png", 64, 64),
    CISTERN("cistern", "/map/cistern.png", 32, 32),
    PUMP("pump", "/map/pump_atlas.png", 64, 64),

    MAP_BORDER("map_border", "/map/map_border.png", 32, 32),

    FONT_MONO("font_mono", "/ui/fonts/bitmap/font_mono.png", 12, 12),
    FONT_MAIN("font_main", "/ui/fonts/bitmap/font_main.png", 16, 16),

    ARROW_BUTTONS("buttons_dirs", "/ui/buttons/button_arrows.png", 32, 32),
    CONFIRM_CANCEL_BUTTONS("confirm_cancel_buttons", "/ui/buttons/button_confirm_cancel.png", 32, 32);

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
