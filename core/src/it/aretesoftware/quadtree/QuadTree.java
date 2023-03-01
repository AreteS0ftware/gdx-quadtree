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
        if (!(rect.x < bounds.x + bounds.width && rect.x + rect.width > bounds.x && rect.y < bounds.y + bounds.height && rect.y + rect.height > bounds.y)) {
            return false;
        }

        if (objects.size < root.GetMaxItemsPerSector() && northWest == null || level >= root.GetMaxLevel()) {
            objects.add(item);
            return true;
        }

        if (northWest == null) {
            Split();
        }

        if (northWest.Insert(item)) return true;
        if (northEast.Insert(item)) return true;
        if (southWest.Insert(item)) return true;
        if (southEast.Insert(item)) return true;

        return false;
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

    Array<QuadTreeItem<T>> Retrieve(Array<QuadTreeItem<T>> list, Rectangle area) {
        // I could use Rectangle.overlaps(), but a half-assed benchmark shows that checking
        // if the rectangles overlap manually like I'm doing here is roughly 30% faster.
        if (!((area.x < bounds.x + bounds.width && area.x + area.width > bounds.x && area.y < bounds.y + bounds.height && area.y + area.height > bounds.y))) {
            return list; // empty list
        }

        for (QuadTreeItem<T> item : objects) {
            Rectangle rect = item.GetObjectBounds();
            if (rect.x < area.x + area.width && rect.x + rect.width > area.x && rect.y < area.y + area.height && rect.y + rect.height > area.y) {
                list.add(item);
            }
        }

        if (northWest == null) {
            return list;
        }

        northWest.Retrieve(list, area);
        northEast.Retrieve(list, area);
        southWest.Retrieve(list, area);
        southEast.Retrieve(list, area);

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
