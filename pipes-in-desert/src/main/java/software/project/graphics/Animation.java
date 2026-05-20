package software.project.graphics;

import java.util.ArrayList;
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

    /**
     * Creates an animation from a sprite sheet using all frames in row-major order.
     * @param sheet the sprite sheet containing the animation frames
     * @param frameDelayMs milliseconds per frame
     * @param loop whether the animation should loop
     */
    public Animation(SpriteSheet sheet, int frameDelayMs, boolean loop) {
        this.frames = new ArrayList<>();
        this.frameDelayMs = frameDelayMs;
        this.loop = loop;

        // Load all frames from the sprite sheet in order
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getCols(); col++) {
                Sprite frame = sheet.getSprite(col, row);
                if (frame != null) {
                    this.frames.add(frame);
                }
            }
        }
    }

    /**
     * Creates an animation from a sprite sheet using only a specific range of frames.
     * @param sheet the sprite sheet containing the animation frames
     * @param startCol starting column (inclusive)
     * @param startRow starting row (inclusive)
     * @param endCol ending column (inclusive)
     * @param endRow ending row (inclusive)
     * @param frameDelayMs milliseconds per frame
     * @param loop whether the animation should loop
     */
    public Animation(SpriteSheet sheet, int startCol, int startRow, int endCol, int endRow,
                     int frameDelayMs, boolean loop) {
        this.frames = new ArrayList<>();
        this.frameDelayMs = frameDelayMs;
        this.loop = loop;

        for (int row = startRow; row <= endRow; row++) {
            int colStart = (row == startRow) ? startCol : 0;
            int colEnd = (row == endRow) ? endCol : sheet.getCols() - 1;

            for (int col = colStart; col <= colEnd; col++) {
                Sprite frame = sheet.getSprite(col, row);
                if (frame != null) {
                    this.frames.add(frame);
                }
            }
        }
    }

    /**
     * Creates an animation from a sprite sheet using specific frame indices.
     * @param sheet the sprite sheet containing the animation frames
     * @param frameIndices array of [col, row] pairs specifying which frames to use
     * @param frameDelayMs milliseconds per frame
     * @param loop whether the animation should loop
     */
    public Animation(SpriteSheet sheet, int[][] frameIndices, int frameDelayMs, boolean loop) {
        this.frames = new ArrayList<>();
        this.frameDelayMs = frameDelayMs;
        this.loop = loop;

        for (int[] indices : frameIndices) {
            int col = indices[0];
            int row = indices[1];
            Sprite frame = sheet.getSprite(col, row);
            if (frame != null) {
                this.frames.add(frame);
            }
        }
    }

    /**
     * Adds a frame to the animation.
     * @param sprite the sprite to add as a frame
     * @return this animation for method chaining
     */
    public Animation addFrame(Sprite sprite) {
        if (sprite != null) {
            this.frames.add(sprite);
        }
        return this;
    }

    /**
     * Adds multiple frames to the animation.
     * @param sprites list of sprites to add as frames
     * @return this animation for method chaining
     */
    public Animation addFrames(List<Sprite> sprites) {
        if (sprites != null) {
            this.frames.addAll(sprites);
        }
        return this;
    }

    /**
     * Adds a range of frames from a sprite sheet.
     * @param sheet the sprite sheet
     * @param startCol starting column (inclusive)
     * @param startRow starting row (inclusive)
     * @param endCol ending column (inclusive)
     * @param endRow ending row (inclusive)
     * @return this animation for method chaining
     */
    public Animation addFramesFromSheet(SpriteSheet sheet, int startCol, int startRow,
                                        int endCol, int endRow) {
        for (int row = startRow; row <= endRow; row++) {
            int colStart = (row == startRow) ? startCol : 0;
            int colEnd = (row == endRow) ? endCol : sheet.getCols() - 1;

            for (int col = colStart; col <= colEnd; col++) {
                Sprite frame = sheet.getSprite(col, row);
                if (frame != null) {
                    this.frames.add(frame);
                }
            }
        }
        return this;
    }

    /**
     * Adds all frames from a sprite sheet in row-major order.
     * @param sheet the sprite sheet
     * @return this animation for method chaining
     */
    public Animation addAllFramesFromSheet(SpriteSheet sheet) {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getCols(); col++) {
                Sprite frame = sheet.getSprite(col, row);
                if (frame != null) {
                    this.frames.add(frame);
                }
            }
        }
        return this;
    }

    /**
     * Clears all frames from the animation.
     * @return this animation for method chaining
     */
    public Animation clearFrames() {
        this.frames.clear();
        this.currentFrame = 0;
        return this;
    }

    /**
     * Returns the number of frames in the animation.
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Sets whether the animation should loop.
     */
    public void setLoop(boolean loop) {
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

    public void reset() {
        currentFrame = 0;
        lastUpdate = System.currentTimeMillis();
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

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public void setCurrentFrameIndex(int index) {
        if (index >= 0 && index < frames.size()) {
            currentFrame = index;
            lastUpdate = System.currentTimeMillis();
        }
    }
}