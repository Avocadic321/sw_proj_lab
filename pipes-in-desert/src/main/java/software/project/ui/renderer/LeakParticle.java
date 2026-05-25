package software.project.ui.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class LeakParticle {
    private float x, y;
    private float vx, vy;
    private float life = 1.0f;
    private float size;

    public LeakParticle(float x, float y, float vx, float vy, float size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
    }

    public boolean update(float deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        life -= deltaTime * 1.2f;
        return life > 0;
    }

    public void draw(Graphics2D g) {
        // Clamp alpha between 0.0 and 1.0
        float alpha = life * 0.84f;
        if (alpha < 0) alpha = 0;
        if (alpha > 1) alpha = 1;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int blue = 150 + (int)(105 * life);
        if (blue > 255) blue = 255;
        if (blue < 0) blue = 0;
        g.setColor(new Color(70, 140, blue));

        int drawSize = (int)(size * (0.8f + life * 0.5f));
        if (drawSize < 1) drawSize = 1;
        g.fillOval((int)x - drawSize/2, (int)y - drawSize/2, drawSize, drawSize);
        g.setComposite(AlphaComposite.SrcOver);
    }
}