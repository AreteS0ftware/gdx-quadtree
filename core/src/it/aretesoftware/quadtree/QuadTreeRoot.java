package it.aretesoftware.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * The {@link QuadTree}'s root node. All operations on the tree should be done through this object.
 * Defines properties that are applied to the entire tree, such as its maximum level and its maximum amount of items per node.
 * Allows typecasting to any class, thus making the system easy to use in any game.
 * Uses {@link Pool} implementations through {@link PoolQuadTree} and {@link PoolQuadTreeItem} for efficient memory usage.
 * @author Aret3Dev */
public class QuadTreeRoot<T> extends QuadTree<T> {

    private int maxLevel, maxItemsPerNode;
    private final PoolQuadTree<T> quadTreePool;
    private final PoolQuadTreeItem<T> itemsPool;
    private final Array<QuadTreeItem<T>> retrievedItems;

    public QuadTreeRoot(Rectangle bounds) {
        this(bounds, 6, 4, 32);
    }

    public QuadTreeRoot(Rectangle bounds, int maxLevel, int maxItemsPerNode, int poolsSize) {
        super(bounds, maxItemsPerNode);
        super.root = this;
        this.maxLevel = maxLevel;
        this.maxItemsPerNode = maxItemsPerNode;
        quadTreePool = createQuadtreePool(maxItemsPerNode);
        itemsPool = createItemsPool(poolsSize);
        retrievedItems = new Array<>(poolsSize);
    }

    private PoolQuadTree<T> createQuadtreePool(final int poolSize) {
        PoolQuadTree<T> pool = new PoolQuadTree<>(this);
        pool.fill(poolSize);
        return pool;
    }

    private PoolQuadTreeItem<T> createItemsPool(final int poolSize) {
        PoolQuadTreeItem<T> pool = new PoolQuadTreeItem<>(this);
        pool.fill(poolSize);
        return pool;
    }

    //

    /**
     * Overrides {@link QuadTree}'s clear() to also clear {@link QuadTreeRoot}'s
     * internal {@link Array} of {@link QuadTreeItem} used during retrieval. */
    @Override
    public void clear() {
        retrievedItems.clear();
        super.clear();
    }

    /**
     * Overrides {@link QuadTree}'s insert() to free() said {@link QuadTree}
     * in case it couldn't get added for whatever reason.
     * @param item the item to add into this {@link QuadTree}
     * @return whether the item was added or not */
    @Override
    public boolean insert(QuadTreeItem<T> item) {
        boolean inserted = super.insert(item);
        if (!inserted) {
            itemsPool.free(item);
        }
        return inserted;
    }

    /**
     * Overloading of {@link QuadTree}'s retrieve() method, this uses the {@link QuadTreeRoot}'s
     * internal {@link Array} of {@link QuadTree}s for efficient memory usage.
     * Said {@link Array} is cleared before retrieval.
     * @param area the {@link Rectangle} area to search in the tree
     * @return list of {@link QuadTreeItem} found in the defined {@link Rectangle} area */
    public Array<QuadTreeItem<T>> retrieve(Rectangle area) {
        retrievedItems.clear();
        return retrieve(retrievedItems, area);
    }

    //

    /**
     * Obtains a {@link QuadTreeItem} from {@link PoolQuadTreeItem}, for efficient memory usage.
     * This should be called from within your application.
     * @return a newly freed {@link QuadTreeItem} */
    public QuadTreeItem<T> obtainItem() {
        return itemsPool.obtain();
    }

    /**
     * Frees all {@link QuadTreeItem}s through {@link PoolQuadTreeItem}, for efficient memory usage.
     * Used when the {@link QuadTree} clears itself, should not be called under any other circumstance.
     * @param items the {@link Array} of {@link QuadTreeItem}s to free */
    void freeAllItems(Array<QuadTreeItem<T>> items) {
        itemsPool.freeAll(items);
    }

    /**
     * Obtains a {@link QuadTree} from {@link PoolQuadTree}, for efficient memory usage.
     * Used when the {@link QuadTree} splits, should not be called under any other circumstance.
     * @return a newly freed {@link QuadTreeItem} */
    QuadTree<T> obtainNode() {
        return quadTreePool.obtain();
    }

    /**
     * Frees a {@link QuadTree} through {@link PoolQuadTree}, for efficient memory usage.
     * Used when the {@link QuadTree} splits itself, should not be called under any other circumstance.
     * @param node the {@link QuadTree} node to free */
    void freeNode(QuadTree<T> node) {
        quadTreePool.free(node);
    }

    //

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxItemsPerNode() {
        return maxItemsPerNode;
    }

    public void setMaxItemsPerNode(int maxItemsPerSector) {
        this.maxItemsPerNode = maxItemsPerSector;
    }

}
