package software.project.graphics;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Central utility for loading all types of resources from the classpath.
 * All methods return null on failure and log errors.
 */
public final class ResourceLoader {
    private static final boolean SHOW_WARNINGS = true;

    private ResourceLoader() {}

    /**
     * Loads an image from the given classpath resource.
     * @param path resource path (e.g. "/ui/logo.png")
     * @return BufferedImage or null if not found/error
     */
    public static BufferedImage loadImage(String path) {
        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) return null;
            ImageIO.setUseCache(false);
            return ImageIO.read(is);
        } catch (IOException e) {
            logError("image", path, e);
            return null;
        }
    }

    /**
     * Loads a TrueType font from the given classpath resource.
     * @param path resource path (e.g. "/fonts/OpenSans.ttf")
     * @param size initial font size
     * @return Font or null if not found/error
     */
    public static Font loadTrueTypeFont(String path, float size) {
        try (InputStream is = getResourceAsStream(path)) {
            if (is == null) return null;
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            logError("font", path, e);
            return null;
        }
    }

    /**
     * Returns a URL for a resource (useful for JavaFX Media).
     * @param path resource path (e.g. "/ui/credits/credits.mp4")
     * @return URL or null if not found
     */
    public static URL getResourceUrl(String path) {
        URL url = ResourceLoader.class.getResource(path);
        if (url == null && SHOW_WARNINGS) {
            System.err.println("[WARN] Resource URL not found: " + path);
        }
        return url;
    }

    /**
     * Opens an InputStream for a classpath resource.
     * @param path resource path
     * @return InputStream (must be closed by caller) or null
     */
    public static InputStream getResourceAsStream(String path) {
        InputStream is = ResourceLoader.class.getResourceAsStream(path);
        if (is == null && SHOW_WARNINGS) {
            System.err.println("[WARN] Resource stream not found: " + path);
        }
        return is;
    }

    /**
     * Loads an audio input stream (for javax.sound). Caller must close.
     * @param path resource path (e.g. "/audio/click.wav")
     * @return AudioInputStream or null
     */
    public static AudioInputStream loadAudioStream(String path) {
        try {
            InputStream is = getResourceAsStream(path);
            if (is == null) return null;
            return AudioSystem.getAudioInputStream(is);
        } catch (UnsupportedAudioFileException | IOException e) {
            logError("audio", path, e);
            return null;
        }
    }

    private static void logError(String type, String path, Exception e) {
        if (SHOW_WARNINGS) {
            System.err.println("[ERROR] Failed to load " + type + " from " + path + ": " + e.getMessage());
        }
    }
}