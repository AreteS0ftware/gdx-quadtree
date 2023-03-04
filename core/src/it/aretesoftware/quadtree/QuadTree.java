package it.aretesoftware.quadtree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class QuadTree<T> implements Pool.Poolable {

    int level;
    QuadTreeRoot<T> root;
    QuadTree<T> northWest;
    QuadTree<T> northEast;
    QuadTree<T> southWest;
    QuadTree<T> southEast;
    final Array<QuadTreeItem<T>> objects;
    final Rectangle bounds;

    QuadTree(Rectangle bounds, int maxItemsPerSector) {
        this.bounds = bounds;
        objects = new Array<>(maxItemsPerSector);
    }

    //

    @Override
    public void reset() {
        Clear();
        bounds.set(0, 0, 0, 0);
        root = null;
    }

    protected void Clear() {
        root.FreeAllItems(objects);
        objects.clear();

        if (northWest != null) {
            root.FreeQuadTree(northWest);
            northWest = null;
        }
        if (northEast != null) {
            root.FreeQuadTree(northEast);
            northEast = null;
        }
        if (southWest != null) {
            root.FreeQuadTree(southWest);
            southWest = null;
        }
        if (southEast != null) {
            root.FreeQuadTree(southEast);
            southEast = null;
        }
    }

    //

    protected boolean Insert(QuadTreeItem<T> item) {
        Rectangle rect = item.GetObjectBounds();
        if (!rect.overlaps(bounds)) {
            return false;
        }

        if (northWest != null) {
            if (northWest.Insert(item)
                    || northEast.Insert(item)
                    || southWest.Insert(item)
                    || southEast.Insert(item)) {
                return true;
            }
        }

        objects.add(item);
        if (objects.size <= root.GetMaxItemsPerNode() || level >= root.GetMaxLevel()) {
            return true;
        }

        if (northWest == null) {
            Split();
        }

        int i = 0;
        while (i < objects.size) {
            QuadTreeItem<T> arrayItem = objects.get(i);
            if (northWest.Insert(arrayItem)
                    || northEast.Insert(arrayItem)
                    || southWest.Insert(arrayItem)
                    || southEast.Insert(arrayItem)) {
                objects.removeIndex(i);
            }
            else {
                i++;
            }
        }


        return true;
    }

    private void Split() {
        float halfWidth = (bounds.getWidth() * 0.5f);
        float halfHeight = (bounds.getHeight() * 0.5f);
        float x = bounds.getX();
        float y = bounds.getY();
        int newLevel = level + 1;

        northWest = root.ObtainQuadTree();
        northWest.bounds.set(x, y + halfHeight, halfWidth, halfHeight);
        northWest.level = newLevel;

        northEast = root.ObtainQuadTree();
        northEast.bounds.set(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
        northEast.level = newLevel;

        southWest = root.ObtainQuadTree();
        southWest.bounds.set(x, y, halfWidth, halfHeight);
        southWest.level = newLevel;

        southEast = root.ObtainQuadTree();
        southEast.bounds.set(x + halfWidth, y, halfWidth, halfHeight);
        southEast.level = newLevel;
    }

    //

    protected Array<QuadTreeItem<T>> Retrieve(Array<QuadTreeItem<T>> list, Rectangle area) {
        if (northWest != null) {
            if (northWest.bounds.overlaps(area)) northWest.Retrieve(list, area);
            if (northEast.bounds.overlaps(area)) northEast.Retrieve(list, area);
            if (southWest.bounds.overlaps(area)) southWest.Retrieve(list, area);
            if (southEast.bounds.overlaps(area)) southEast.Retrieve(list, area);
        }
        list.addAll(objects);

        return list;
    }

    //

    public void Render(ShapeRenderer shapeRenderer) {
        if (northWest != null) {
            northWest.Render(shapeRenderer);
        }
        if (northEast != null) {
            northEast.Render(shapeRenderer);
        }
        if (southWest != null) {
            southWest.Render(shapeRenderer);
        }
        if (southEast != null) {
            southEast.Render(shapeRenderer);
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
