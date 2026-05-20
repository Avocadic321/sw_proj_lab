package software.project.graphics;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private final List<Sprite> frames;
    private final int frameDelayMs;
    private int currentFrame = 0;
    private long lastUpdate = 0;
    private boolean loop;
    private boolean playing = true;
    private boolean valid = true;

    public Animation(SpriteSheet sheet, int frameDelayMs, boolean loop) {
        this.frames = new ArrayList<>();
        this.frameDelayMs = frameDelayMs;
        this.loop = loop;

        if (sheet == null) {
            System.err.println("[ERROR] Cannot create Animation: SpriteSheet is null");
            this.valid = false;
            return;
        }

        if (!sheet.isValid()) {
            System.err.println("[ERROR] Cannot create Animation: SpriteSheet is invalid");
            this.valid = false;
            return;
        }

        // Load all frames from the sprite sheet in order
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getCols(); col++) {
                Sprite frame = sheet.getSprite(col, row);
                if (frame != null) {
                    this.frames.add(frame);
                }
            }
        }

        if (this.frames.isEmpty()) {
            System.err.println("[ERROR] Animation created from sprite sheet has no frames");
            this.valid = false;
        } else {
            System.out.println("[INFO] Animation created with " + this.frames.size() + " frames");
        }
    }

    public boolean isValid() {
        return valid && !frames.isEmpty();
    }

    public void start() {
        if (!isValid()) {
            System.err.println("[WARNING] Cannot start invalid animation");
            return;
        }
        playing = true;
        currentFrame = 0;
        lastUpdate = System.currentTimeMillis();
    }

    public void stop() {
        playing = false;
    }

    public void update() {
        if (!isValid() || !playing) return;

        long now = System.currentTimeMillis();
        if (now - lastUpdate >= frameDelayMs) {
            lastUpdate = now;
            currentFrame++;
            if (currentFrame >= frames.size()) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    playing = false;
                }
            }
        }
    }

    public Sprite getCurrentFrame() {
        if (!isValid()) return null;
        if (currentFrame >= frames.size()) return null;
        return frames.get(currentFrame);
    }

    public int getFrameCount() {
        return frames.size();
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}