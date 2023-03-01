package it.aretesoftware.quadtree.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class SpriteEntity {

    private final Texture texture;
    private final Rectangle bounds;

    public SpriteEntity(Texture texture) {
        this.texture = texture;
        bounds = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
    }

    public void Draw(Batch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle GetBounds() {
        return bounds;
    }

}
