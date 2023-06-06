package com.voider.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TileMap {
    private static final float DEFAULT_ZOOM = 0.2f; // Adjust this value to change the initial zoom level
    private TiledMap tiledMap;
    private MapRenderer mapRenderer;
    private OrthographicCamera camera;

    public TileMap(String mapFilePath) {
        tiledMap = new TmxMapLoader().load(mapFilePath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        camera = new OrthographicCamera();
        camera.setToOrtho(false); // Set the camera's projection to be aligned with the tile map's coordinate system
        camera.zoom = DEFAULT_ZOOM; // Set the initial zoom level

        // Set the initial camera position to center the map
        float mapWidth = tiledMap.getProperties().get("width", Integer.class) * tiledMap.getProperties().get("tilewidth", Integer.class);
        float mapHeight = tiledMap.getProperties().get("height", Integer.class) * tiledMap.getProperties().get("tileheight", Integer.class);
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();
    }

    public void render() {
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public void dispose() {
this.dispose();    }
}
