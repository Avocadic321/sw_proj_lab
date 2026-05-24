package software.project.graphics;

public enum BitmapFonts {
    FONT_MONO(SpriteSheets.FONT_MONO, "abcdefghijklmnopqrstuvwxyz0123456789-+x/=()#@!?.,;'$"),
    FONT_MAIN(SpriteSheets.FONT_MAIN, "abcdefghijklmnopqrstuvwxyz0123456789");

    private final SpriteSheets sheet;
    private final String mapping;

    BitmapFonts(SpriteSheets sheet, String mapping) {
        this.sheet = sheet;
        this.mapping = mapping;
    }

    public SpriteSheets getSheet() {
        return sheet;
    }

    public String getMapping() {
        return mapping;
    }
}