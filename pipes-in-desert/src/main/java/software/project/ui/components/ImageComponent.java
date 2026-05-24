package software.project.ui.components;

import java.awt.Graphics2D;

import software.project.graphics.Sprite;

public class ImageComponent extends Component {
    private Sprite sprite;

    public ImageComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ImageComponent(int x, int y, int width, int height, Sprite sprite) {
        super(x, y, width, height);
        this.sprite = sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            sprite.draw(g, x, y, width, height);
        }
    }

    @Override
    public void update() {
        // Image components don't need state updates
    }
}