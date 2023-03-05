package it.aretesoftware.quadtree;

import com.badlogic.gdx.utils.Pool;

/**
 * The {@link QuadTreeRoot}'s pool used for handling {@link QuadTreeItem} objects.
 * @author Aret3Dev */
public class PoolQuadTreeItem<T> extends Pool<QuadTreeItem<T>> {

    private final QuadTreeRoot<T> root;

    PoolQuadTreeItem(QuadTreeRoot<T> root) {
        this.root = root;
    }

    @Override
    protected QuadTreeItem<T> newObject() {
        return new QuadTreeItem<>();
    }

}
