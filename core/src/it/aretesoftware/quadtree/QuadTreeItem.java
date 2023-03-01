package it.aretesoftware.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class QuadTreeItem<T> implements Pool.Poolable {

    private T object;
    private final Rectangle objectBounds = new Rectangle();

    public void init(T object, Rectangle objectBounds) {
        this.object = object;
        this.objectBounds.set(objectBounds);
    }

    @Override
    public void reset() {
        objectBounds.set(0, 0, 0, 0);
        object = null;
    }

    public T GetObject() {
        return object;
    }

    public Rectangle GetObjectBounds() {
        return objectBounds;
    }
}
