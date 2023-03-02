package it.aretesoftware.quadtree.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

import it.aretesoftware.quadtree.QuadTree;
import it.aretesoftware.quadtree.QuadTreeItem;
import it.aretesoftware.quadtree.QuadTreeRoot;

public class QuadtreeExample extends ApplicationAdapter {
	ScreenViewport viewport;
	SpriteBatch batch;
	Texture img;
	Array<SpriteEntity> entities;
	QuadTreeRoot<SpriteEntity> root;
	Rectangle cameraBounds;
	ShapeRenderer shapes;
	
	@Override
	public void create () {
		Gdx.graphics.setVSync(false);
		viewport = new ScreenViewport();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		cameraBounds = new Rectangle();
		root = new QuadTreeRoot<>(new Rectangle(-20000, -20000, 100000, 100000), 6, 4, 32);
		shapes = new ShapeRenderer();
		InitializeEntities(10000);
		RandomizeEntities(new Rectangle(-20000, -20000, 40000, 40000));
	}

	private void InitializeEntities(int size) {
		entities = new Array<>(size);
		for (int i = 0; i < size; i++) {
			entities.add(new SpriteEntity(img));
		}
	}

	private void RandomizeEntities(Rectangle area) {
		Random random = new Random();
		for (SpriteEntity entity : entities) {
			float x = random.nextInt((int)area.getWidth()) + area.getX();
			if (x + img.getWidth() > area.getX() + area.getWidth()) {
				x -= img.getWidth();
			}
			float y = random.nextInt((int)area.getHeight()) + area.getY();
			if (y + img.getHeight() > area.getY() + area.getHeight()) {
				y -= img.getHeight();
			}
			entity.GetBounds().setPosition(x, y);
		}
	}

	//

	@Override
	public void render () {
		HandleInput();
		Draw();
	}

	private void HandleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			for (SpriteEntity entity : entities) {
				entity.GetBounds().x += 1f;
			}
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			for (SpriteEntity entity : entities) {
				entity.GetBounds().x -= 1f;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			for (SpriteEntity entity : entities) {
				entity.GetBounds().y += 1f;
			}
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			for (SpriteEntity entity : entities) {
				entity.GetBounds().y -= 1f;
			}
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			RandomizeEntities(new Rectangle(-20000, -20000, 40000, 40000));
		}

		OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.position.y = camera.position.y + 7.5f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.position.y = camera.position.y - 7.5f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.position.x = camera.position.x + 7.5f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.position.x = camera.position.x - 7.5f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom = camera.zoom + 0.075f;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			camera.zoom = camera.zoom - 0.075f;
		}
		camera.update();

		Frustum frustum = camera.frustum;
		Vector3 min = frustum.planePoints[0];
		Vector3 max = frustum.planePoints[2];
		//cameraBounds.set(min.x, min.y, max.x - min.x, max.y - min.y);
		//cameraBounds.set(camera.position.x - 250, camera.position.y - 250, camera.position.x + 250, camera.position.y + 250);
		float diff = 200 * camera.zoom;
		cameraBounds.set(min.x + diff, min.y + diff, max.x - min.x - (diff * 2f), max.y - min.y - (diff * 2f));
	}

	private void Draw() {
		ScreenUtils.clear(1, 1, 1, 1);

		long start = System.nanoTime();

		Camera camera = viewport.getCamera();
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//DrawNoQuadTreeRetrieval(camera);
		DrawWithQuadTreeRetrieval();
		batch.end();

		long end = System.nanoTime();
		float timeToRender = (end - start) / 100000f;
		System.out.println("Time to render frame: " + timeToRender + "ms, total frames per second: " + Gdx.graphics.getFramesPerSecond() + ", Java heap memory: " + (Gdx.app.getJavaHeap() / 1000000f));

		shapes.setProjectionMatrix(camera.combined);
		shapes.begin(ShapeRenderer.ShapeType.Line);
		root.Render(shapes);
		shapes.setColor(Color.BLUE);
		shapes.flush();
		Gdx.gl.glLineWidth(5f);
		shapes.rect(cameraBounds.x, cameraBounds.y, cameraBounds.width, cameraBounds.height);
		shapes.flush();
		Gdx.gl.glLineWidth(1f);
		shapes.end();
	}

	private void DrawWithQuadTreeRetrieval() {
		root.Clear();
		for (SpriteEntity entity : entities) {
			QuadTreeItem<SpriteEntity> item = root.ObtainItem();
			item.init(entity, entity.GetBounds());
			root.Insert(item);
		}

		long start = System.nanoTime();
		Array<QuadTreeItem<SpriteEntity>> list = root.Retrieve(cameraBounds);
		long end = System.nanoTime();
		float result = (end - start) / 100000f;
		//System.out.println("Time to retrieve: " + result + " ms, Items retrieved: " + list.size);

		for (QuadTreeItem<SpriteEntity> item : list) {
			//if (IsVisible(item.GetObjectBounds())) {
				item.GetObject().Draw(batch);
			//}
		}
	}

	private void DrawNoQuadTreeRetrieval(Camera camera) {
		for (SpriteEntity entity : entities) {
			if (IsVisible(entity.GetBounds())) {
				entity.Draw(batch);
			}
		}
	}

	private boolean IsVisible(Rectangle rect) {
		return rect.overlaps(cameraBounds);
	}

	//
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
}
