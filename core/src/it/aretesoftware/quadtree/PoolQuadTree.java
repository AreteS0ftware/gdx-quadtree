package it.aretesoftware.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * The {@link QuadTreeRoot}'s pool used for handling {@link QuadTree} objects.
 * @author Aret3Dev */
public class PoolQuadTree<T> extends Pool<QuadTree<T>> {

    private final QuadTreeRoot<T> root;

    PoolQuadTree(QuadTreeRoot<T> root) {
        this.root = root;
    }

    @Override
    protected QuadTree<T> newObject() {
        Rectangle rectangle = new Rectangle();
        QuadTree<T> quadTree = new QuadTree<>(rectangle, root.getMaxItemsPerNode());
        quadTree.root = root;
        return quadTree;
    }
    @Override
    public QuadTree<T> obtain() {
        QuadTree<T> quadTree = super.obtain();
        quadTree.root = root;
        return quadTree;
    }

}
