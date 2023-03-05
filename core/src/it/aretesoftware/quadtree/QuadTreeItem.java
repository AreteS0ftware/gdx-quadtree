package it.aretesoftware.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * The object that gets inserted into the {@link QuadTreeRoot} and by extension, the {@link QuadTree}.
 * Allows typecasting to any class, thus making the system easy to use in any game.
 * Implements the {@link Pool.Poolable} interface for efficient memory usage.
 * @author Aret3Dev */
public class QuadTreeItem<T> implements Pool.Poolable {

    private T object;
    private final Rectangle objectBounds = new Rectangle();

    /**
     * Call this method once you get a {@link QuadTreeItem} from {@link QuadTreeRoot}'s obtainItem() method.
     * @param object the object to set
     * @param objectBounds the object's boundaries */
    public void init(T object, Rectangle objectBounds) {
        this.object = object;
        this.objectBounds.set(objectBounds);
    }

    /**
     * Resets this {@link QuadTreeItem} to its default state for later use, for efficient memory usage.
     * This method is automatically called by {@link QuadTreeRoot}'s {@link Pool}, and should not
     * be used under any other circumstance. */
    @Override
    public void reset() {
        objectBounds.set(0, 0, 0, 0);
        object = null;
    }

    /** @return the stored object */
    public T getObject() {
        return object;
    }

    /** @return the object's bounds */
    public Rectangle getObjectBounds() {
        return objectBounds;
    }
}
