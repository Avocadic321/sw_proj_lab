package software.project.graphics;

public enum Sprites {
    MENU_BACKGROUND("menu_background", "/ui/background/menu_background.png"),
    MENU_PANEL("menu_panel", "/ui/panels/menu_panel.png"),
    MENU_TITLE("menu_title", "/ui/titles/menu_title_two.png"),

    PUMP_FAN("pump_fan", "/map/pump_fan.png"),
    PUMP_STATIC("pump", "/map/pump.png"),

    SPRING("spring", "/map/spring3.png"),
    SPRING_PIPE("spring_pipe", "/map/spring_pipe.png"),

    PLUMBER("plumber", "/entity/plumber.png"),
    SABOTEUR("saboteur", "/entity/saboteur.png"),

    GRASS("grass", "/map/grass.png"),

    PAPER_BANNER("paper_banner", "/ui/banners/paper_banner.png"),
    TIMER_BANNER("timer_banner", "/ui/banners/timer_banner.png"),

    SIMPLE_PANEL("simple_panel", "/ui/panels/simple_panel.png");

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
