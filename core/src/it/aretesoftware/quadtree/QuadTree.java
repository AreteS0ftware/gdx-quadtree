package it.aretesoftware.quadtree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Represents a single node in the tree; will automatically split into four more nodes
 * once the amount of {@link QuadTreeItem}s stored exceeds the maximum amount of items per node.
 * Will not split once the node's level is greater than or equal than the maximum level;
 * any item added after exceeding the level will ignore the maximum amount of items per node
 * and will still be added to the items list.
 * The maximum amount of items per node and maximum level are taken from {@link QuadTreeRoot}.
 * Allows typecasting to any class, thus making the system easy to use in any game.
 * Implements the {@link Pool.Poolable} interface for efficient memory usage.
 * @author Aret3Dev */
public class QuadTree<T> implements Pool.Poolable {

    int level;
    QuadTreeRoot<T> root;
    QuadTree<T> northWest;
    QuadTree<T> northEast;
    QuadTree<T> southWest;
    QuadTree<T> southEast;
    final Array<QuadTreeItem<T>> items;
    final Rectangle bounds;

    QuadTree(Rectangle bounds, int maxItemsPerNode) {
        this.bounds = bounds;
        items = new Array<>(maxItemsPerNode);
    }

    //

    /**
     * Resets this {@link QuadTree} to its default state for later use, for efficient memory usage.
     * This method is automatically called by {@link QuadTreeRoot}'s {@link Pool}, and should not
     * be used under any other circumstance. */
    @Override
    public void reset() {
        clear();
        bounds.set(0, 0, 0, 0);
        root = null;
    }

    /**
     * Clears this {@link QuadTree} by freeing its associated items & nodes.
     * Note that freeing a {@link QuadTree} through {@link QuadTreeRoot}'s freeQuadTree() method
     * automatically calls this method - therefore this method should not be used under any other
     * circumstance, other than {@link QuadTreeRoot}'s own implementation of clear(). */
    protected void clear() {
        root.freeAllItems(items);
        items.clear();

        if (northWest != null) {
            root.freeNode(northWest);
            northWest = null;
        }
        if (northEast != null) {
            root.freeNode(northEast);
            northEast = null;
        }
        if (southWest != null) {
            root.freeNode(southWest);
            southWest = null;
        }
        if (southEast != null) {
            root.freeNode(southEast);
            southEast = null;
        }
    }

    //

    /**
     * Inserts a {@link QuadTreeItem} into this {@link QuadTree}.
     * @param item the item to add into this {@link QuadTree}
     * @return whether the item was added or not */
    protected boolean insert(QuadTreeItem<T> item) {
        Rectangle rect = item.getObjectBounds();
        if (!rect.overlaps(bounds)) {
            return false;
        }

        if (northWest != null) {
            if (northWest.insert(item)
                    || northEast.insert(item)
                    || southWest.insert(item)
                    || southEast.insert(item)) {
                return true;
            }
        }

        items.add(item);
        if (items.size <= root.getMaxItemsPerNode() || level >= root.getMaxLevel()) {
            return true;
        }

        if (northWest == null) {
            split();
        }

        int i = 0;
        while (i < items.size) {
            QuadTreeItem<T> arrayItem = items.get(i);
            if (northWest.insert(arrayItem)
                    || northEast.insert(arrayItem)
                    || southWest.insert(arrayItem)
                    || southEast.insert(arrayItem)) {
                items.removeIndex(i);
            }
            else {
                i++;
            }
        }


        return true;
    }

    /**
     * Splits this {@link QuadTree} into four more nodes, which are obtained from
     * {@link QuadTreeRoot}'s {@link Pool}. */
    private void split() {
        float halfWidth = (bounds.getWidth() * 0.5f);
        float halfHeight = (bounds.getHeight() * 0.5f);
        float x = bounds.getX();
        float y = bounds.getY();
        int newLevel = level + 1;

        northWest = root.obtainNode();
        northWest.bounds.set(x, y + halfHeight, halfWidth, halfHeight);
        northWest.level = newLevel;

        northEast = root.obtainNode();
        northEast.bounds.set(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
        northEast.level = newLevel;

        southWest = root.obtainNode();
        southWest.bounds.set(x, y, halfWidth, halfHeight);
        southWest.level = newLevel;

        southEast = root.obtainNode();
        southEast.bounds.set(x + halfWidth, y, halfWidth, halfHeight);
        southEast.level = newLevel;
    }

    /**
     * Returns all {@link QuadTreeItem}s found on this {@link QuadTree}'s nodes,
     * if they exist and the search {@link Rectangle} area overlaps() the nodes' bounds.
     * Also returns this {@link QuadTree}'s own items.
     * @param list the {@link Array} any items found will be added into
     * @param area the {@link Rectangle} area to search in the tree
     * @return list of {@link QuadTreeItem} found in the defined {@link Rectangle} area */
    protected Array<QuadTreeItem<T>> retrieve(Array<QuadTreeItem<T>> list, Rectangle area) {
        if (northWest != null) {
            if (northWest.bounds.overlaps(area)) northWest.retrieve(list, area);
            if (northEast.bounds.overlaps(area)) northEast.retrieve(list, area);
            if (southWest.bounds.overlaps(area)) southWest.retrieve(list, area);
            if (southEast.bounds.overlaps(area)) southEast.retrieve(list, area);
        }
        list.addAll(items);

        return list;
    }

    //

    /** Render this {@link QuadTree} and its nodes, if they exist. */
    public void render(ShapeRenderer shapeRenderer) {
        if (northWest != null) {
            northWest.render(shapeRenderer);
        }
        if (northEast != null) {
            northEast.render(shapeRenderer);
        }
        if (southWest != null) {
            southWest.render(shapeRenderer);
        }
        if (southEast != null) {
            southEast.render(shapeRenderer);
        }

        switch (level) {
            case 0:
                shapeRenderer.setColor(Color.ORANGE);
                break;
            case 1:
                shapeRenderer.setColor(Color.YELLOW);
                break;
            case 2:
                shapeRenderer.setColor(Color.RED);
                break;
            case 3:
                shapeRenderer.setColor(Color.GREEN);
                break;
            case 4:
                shapeRenderer.setColor(Color.BLUE);
                break;
            case 5:
                shapeRenderer.setColor(Color.MAGENTA);
                break;
            default:
                shapeRenderer.setColor(Color.CYAN);
        }

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
