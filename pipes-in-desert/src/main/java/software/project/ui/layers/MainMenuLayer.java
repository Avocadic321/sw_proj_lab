package software.project.ui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import software.project.graphics.Sprite;
import software.project.graphics.SpriteManager;
import software.project.graphics.SpriteSheet;
import software.project.graphics.SpriteSheets;
import software.project.graphics.Sprites;
import software.project.ui.GameApplication;
import software.project.ui.ScreenManager;
import software.project.ui.components.AnimatedComponent;
import software.project.ui.components.ImageComponent;
import software.project.ui.components.Menu;

public class MainMenuLayer extends Layer {

    private static final int ANIMATION_FRAME_DELAY_MS = 33;
    private static final boolean ANIMATED = true;

    private static final double TOP_MARGIN_PERCENT = 0.23;
    private static final double BOTTOM_MARGIN_PERCENT = 0.07;
    private static final double INNER_PADDING_PERCENT = 0.03;

    private static final float MENU_SCALE_FACTOR = 0.65f;
    private static final float TITLE_SCALE_FACTOR = 0.4f;
    private static final int MENU_VERTICAL_OFFSET = 50;
    private static final int TITLE_VERTICAL_OFFSET = 50;

    private static final int[] BUTTON_ROW_INDICES = { 0, 1, 3, 2 };

    private final GameApplication app;
    private final Menu menu;
    private ImageComponent titleComponent;
    private AnimatedComponent backgroundComponent;

    public MainMenuLayer(GameApplication app) {
        this.app = app;
        loadSprites();
        if (ANIMATED) {
            loadBackgroundAnimation();
        }
        // Create the menu
        menu = new Menu(
                MENU_SCALE_FACTOR,
                MENU_VERTICAL_OFFSET,
                BUTTON_ROW_INDICES,
                TOP_MARGIN_PERCENT,
                BOTTOM_MARGIN_PERCENT,
                INNER_PADDING_PERCENT);

        menu.setAction(0, () -> this.app.replaceLayer(new ConfigLayer(this.app)));
        menu.setAction(1, () -> this.app.pushLayer(new OptionsLayer(this.app)));
        menu.setAction(2, () -> System.out.println("CREDITS clicked"));
        menu.setAction(3, () -> System.exit(0));
    }

    private void loadSprites() {
        SpriteManager sm = SpriteManager.getInstance();
        Sprite titleSprite = sm.getSprite(Sprites.MENU_TITLE);
        recomputeLayout(titleSprite);
    }

    private void loadBackgroundAnimation() {
        SpriteManager sm = SpriteManager.getInstance();
        SpriteSheet animationSheet = sm.getSpriteSheet(SpriteSheets.MENU_ANIMATION);

        if (animationSheet != null && animationSheet.isValid()) {
            int vw = ScreenManager.getInstance().getVirtualWidth();
            int vh = ScreenManager.getInstance().getVirtualHeight();
            backgroundComponent = new AnimatedComponent(
                    0, 0, vw, vh,
                    animationSheet,
                    ANIMATION_FRAME_DELAY_MS,
                    true // loop
            );
        }
    }

    private void recomputeLayout(Sprite titleSprite) {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();

        if (titleSprite != null) {
            int titleWidth = (int) (titleSprite.getWidth() * TITLE_SCALE_FACTOR);
            int titleHeight = (int) (titleSprite.getHeight() * TITLE_SCALE_FACTOR);
            int titleX = (virtualW - titleWidth) / 2;
            int titleY = TITLE_VERTICAL_OFFSET;

            titleComponent = new ImageComponent(titleX, titleY, titleWidth, titleHeight, titleSprite);
        }

        if (menu != null) {
            menu.onResolutionChanged();
        }
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        SpriteManager sm = SpriteManager.getInstance();
        Sprite titleSprite = sm.getSprite(Sprites.MENU_TITLE);
        recomputeLayout(titleSprite);
        // Update background component size on resolution change
        if (backgroundComponent != null) {
            backgroundComponent.setSize(newWidth, newHeight);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (backgroundComponent != null) {
            backgroundComponent.update();
        }
        if (menu != null) {
            menu.update();
        }
    }

    @Override
    public void render(Graphics2D g) {
        int virtualW = ScreenManager.getInstance().getVirtualWidth();
        int virtualH = ScreenManager.getInstance().getVirtualHeight();

        if (backgroundComponent != null) {
            backgroundComponent.draw(g);
        } else if (SpriteManager.getInstance().getSprite(Sprites.MENU_BACKGROUND) != null) {
            SpriteManager.getInstance()
                    .getSprite(Sprites.MENU_BACKGROUND)
                    .draw(g, 0, 0, virtualW, virtualH);
        } else {
            g.setColor(new Color(20, 30, 50));
            g.fillRect(0, 0, virtualW, virtualH);
        }

        if (titleComponent != null) {
            titleComponent.draw(g);
        }

        if (menu != null) {
            menu.render(g);
        }
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (menu != null) {
            menu.handleMouseMoved(e);
        }
        return true;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (menu != null) {
            menu.handleMousePressed(e);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (menu != null) {
            menu.handleMouseReleased(e);
        }
        return true;
    }
}