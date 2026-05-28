package software.project.graphics;

import java.awt.image.BufferedImage;

public enum BitmapFonts {
    FONT_MONO(SpriteSheets.FONT_MONO, "abcdefghijklmnopqrstuvwxyz1234567890-+x/=()#@!?.,:'$"),
    FONT_MAIN(SpriteSheets.FONT_MAIN, "abcdefghijklmnopqrstuvwxyz1234567890");

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

    /**
     * Loads the font sheet image using ResourceLoader.
     * @return BufferedImage or null if not found
     */
    public BufferedImage loadFontImage() {
        return ResourceLoader.loadImage(sheet.getPath());
    }

    /**
     * Loads individual glyph images from the font sheet.
     * @return array of Images, or null if the font sheet couldn't be loaded
     */
    public java.awt.Image[] loadGlyphs() {
        BufferedImage fontImage = loadFontImage();
        if (fontImage == null) return null;

        int glyphs = mapping.length();
        java.awt.Image[] glyphImages = new java.awt.Image[glyphs];
        int frameWidth = sheet.getFrameWidth();
        int frameHeight = sheet.getFrameHeight();

        int cols = fontImage.getWidth() / frameWidth;

        for (int i = 0; i < glyphs; i++) {
            int srcX = (i % cols) * frameWidth;
            int srcY = (i / cols) * frameHeight;
            glyphImages[i] = fontImage.getSubimage(srcX, srcY, frameWidth, frameHeight);
        }

        return glyphImages;
    }
}