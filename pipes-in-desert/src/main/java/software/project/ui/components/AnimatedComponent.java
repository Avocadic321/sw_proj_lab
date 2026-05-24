package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.Animation;
import software.project.graphics.SpriteSheet;

public class AnimatedComponent extends Component {
    private Animation animation;

    public AnimatedComponent(int x, int y, int width, int height, Animation animation) {
        super(x, y, width, height);
        this.animation = animation;
        if (this.animation != null && !this.animation.isPlaying()) {
            this.animation.start();
        }
    }

    public AnimatedComponent(
        int x,
        int y,
        int width,
        int height,
        SpriteSheet sheet,
        int frameDelayMs,
        boolean loop
    ) {
        super(x, y, width, height);
        if (sheet != null && sheet.isValid()) {
            this.animation = new Animation(sheet, frameDelayMs, loop);
            if (this.animation.isValid() && !this.animation.isPlaying()) {
                this.animation.start();
            }
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        if (this.animation != null && !this.animation.isPlaying()) {
            this.animation.start();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (animation != null && animation.getCurrentFrame() != null) {
            animation.getCurrentFrame().draw(g, x, y, width, height);
        }
    }

    @Override
    public void update() {
        if (animation != null) {
            animation.update();
        }
    }
}