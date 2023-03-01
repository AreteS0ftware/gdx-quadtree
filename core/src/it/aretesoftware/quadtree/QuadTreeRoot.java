package it.aretesoftware.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class QuadTreeRoot<T> extends QuadTree<T> {

    private int maxLevel, maxItemsPerSector;
    private final Pool<QuadTree<T>> quadTreePool;
    private final Pool<QuadTreeItem<T>> itemsPool;
    private final Array<QuadTreeItem<T>> retrievedItems;

    public QuadTreeRoot(Rectangle bounds) {
        this(bounds, 6, 4, 32);
    }

    public QuadTreeRoot(Rectangle bounds, int maxLevel, int maxItemsPerSector, int poolSize) {
        super(bounds, maxItemsPerSector);
        super.root = this;
        //
        this.maxLevel = maxLevel;
        this.maxItemsPerSector = maxItemsPerSector;
        quadTreePool = CreateQuadtreePool(maxItemsPerSector);
        itemsPool = CreateItemsPool(poolSize);
        retrievedItems = new Array<>(poolSize);
    }

    private Pool<QuadTree<T>> CreateQuadtreePool(final int poolSize) {
        Pool<QuadTree<T>> pool = new Pool<QuadTree<T>>() {
            @Override
            protected QuadTree<T> newObject() {
                Rectangle rectangle = new Rectangle();
                QuadTree<T> quadTree = new QuadTree<>(rectangle, maxItemsPerSector);
                quadTree.root = QuadTreeRoot.this;
                return quadTree;
            }
            @Override
            public QuadTree<T> obtain() {
                QuadTree<T> quadTree = super.obtain();
                quadTree.root = QuadTreeRoot.this;
                return quadTree;
            }
        };
        pool.fill(poolSize);
        return pool;
    }

    private Pool<QuadTreeItem<T>> CreateItemsPool(final int poolSize) {
        Pool<QuadTreeItem<T>> pool = new Pool<QuadTreeItem<T>>(poolSize) {
            @Override
            protected QuadTreeItem<T> newObject() {
                return new QuadTreeItem<T>();
            }
        };
        pool.fill(poolSize);
        return pool;
    }

    //

    @Override
    public void Clear() {
        retrievedItems.clear();
        super.Clear();
    }

    @Override
    public boolean Insert(QuadTreeItem<T> item) {
        boolean inserted = super.Insert(item);
        if (!inserted) {
            itemsPool.free(item);
        }
        return inserted;
    }

    public Array<QuadTreeItem<T>> Retrieve(Rectangle area) {
        retrievedItems.clear();
        return Retrieve(retrievedItems, area);
    }

    //

    public int GetMaxLevel() {
        return maxLevel;
    }

    public void SetMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int GetMaxItemsPerSector() {
        return maxItemsPerSector;
    }

    public void SetMaxItemsPerSector(int maxItemsPerSector) {
        this.maxItemsPerSector = maxItemsPerSector;
    }

    //

    public QuadTreeItem<T> ObtainItem() {
        return itemsPool.obtain();
    }

    void FreeAllItems(Array<QuadTreeItem<T>> items) {
        itemsPool.freeAll(items);
    }

    QuadTree<T> ObtainQuadTree() {
        return quadTreePool.obtain();
    }

    void FreeQuadTree(QuadTree<T> quadTree) {
        quadTreePool.free(quadTree);
    }

}
