package software.project.graphics;

import java.util.List;

public class Animation {
    private final List<Sprite> frames;
    private final int frameDelayMs;   // milliseconds per frame
    private int currentFrame = 0;
    private long lastUpdate = 0;
    private boolean loop;
    private boolean playing = true;

    public Animation(List<Sprite> frames, int frameDelayMs, boolean loop) {
        this.frames = frames;
        this.frameDelayMs = frameDelayMs;
        this.loop = loop;
    }

    public void start() {
        playing = true;
        currentFrame = 0;
        lastUpdate = System.currentTimeMillis();
    }

    public void stop() {
        playing = false;
    }

    public void update() {
        if (!playing || frames.isEmpty()) return;
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
        if (frames.isEmpty() || currentFrame >= frames.size()) return null;
        return frames.get(currentFrame);
    }

    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }
}