package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.Animation;

public class AnimatedComponent extends Component {
    private Animation animation;

    public AnimatedComponent(int x, int y, int width, int height, Animation animation) {
        super(x, y, width, height);
        this.animation = animation;
        if (this.animation != null && !this.animation.isPlaying()) {
            this.animation.start();
        }
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        if (this.animation != null && !this.animation.isPlaying()) {
            this.animation.start();
        }
    }

    public Animation getAnimation() {
        return animation;
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