package software.project.graphics;

public enum Sprites {
    MENU_BACKGROUND("menu_background", "/ui/menu_background.png"),
    MENU_PANEL("menu_panel", "/ui/menu_panel.png"),
    MENU_TITLE("menu_title", "/ui/menu_title_two.png");

    private final String key;
    private final String path;

    Sprites(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }
}
