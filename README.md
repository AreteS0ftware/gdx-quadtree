# gdx-quadtree

*For a more in-depth explanation, check out <a href="https://aretesoftware.it/libgdx/gdx-quadtree.html">this article</a> - for a live demo, <a href="https://aretesoftware.it/libgdx/gdx-quadtree-example/">click here</a>.*

gdx-quadtree, is a modern, <code><a href="https://libgdx.com/wiki/articles/memory-management">Poolable</a></code> <code>QuadTree</code> implementation that addresses issues with bad memory usage and can work by using a <code>Rectangle</code> in space. 
* This is helpful for games that have many objects that check their position relative to other objects or games that need to find objects by position enough times that the performance would suffer. 
* On the other hand, if said games don't have too many objects, or have lots of objects that don't really interact with each other in any significant way, then it would be best not to bother.

## Usage
```java
Rectangle cameraBounds;
Array<Entity> entities;
QuadTreeRoot<Entity> quadtreeRoot;

@Override
public void create() {
    Rectangle rootBounds = new Rectangle(-1000, -1000, +1000, +1000);
    quadtreeRoot = new QuadTreeRoot<Entity>(rootBounds);  // another constructor is available
    // initialization of entities, camera rectangle, etc.
}

@Override
public void render() {
    // Game logic
    root.Clear();
    for (Entity entity : entities) {
        QuadTreeItem<Entity> item = root.ObtainItem();
        item.init(entity, entity.GetAABBRectangle());
        // Performance would probably much better if all static entities & geometry 
        // that make up a level is inserted into the Quadtree only once!
        root.Insert(item);
    }
    Array<QuadTreeItem<Entity>> list = root.Retrieve(cameraBounds);
    for (QuadTreeItem<Entity> item : list) {
        item.GetObject().Draw(batch);
    }
}
```

## Root Properties
You can easily change the root's properties by calling their relative setters - this by extension affects the entire <code>QuadTree</code>.
* <code>maxLevel</code> is highest level the <code>QuadTree</code> can reach, i.e. how many times it will <code>Split()</code>
* <code>maxItemsPerNode</code> is the amount of <code>QuadTreeItem</code>s that must be present in a <code>QuadTree</code> before it will <code>Split()</code>.

Changing these properties has a **significant effect on performance**! You should tune them according to your game's needs for the maximum amount of gains.

## Roadmap
#### To Do
🔲 Differentiate between static and dynamic objects
