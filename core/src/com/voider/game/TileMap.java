package com.voider.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TileMap {
    private static final float DEFAULT_ZOOM = 0.17f; // Adjust this value to change the initial zoom level
    private TiledMap tiledMap;
    private MapRenderer mapRenderer;
    private OrthographicCamera camera;

    private boolean[][] boundaryTiles; // Array to store boundary tiles' positions
    private float tileWidth;
    private float tileHeight;

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


        // Initialise boundary array

        // Assuming the boundary tiles are in the first layer

        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(4);
        int mapWidthInTiles = tileLayer.getWidth();
        int mapHeightInTiles = tileLayer.getHeight();

        tileWidth = tileLayer.getTileWidth();
        tileHeight = tileLayer.getTileHeight();

        boundaryTiles = new boolean[mapWidthInTiles][mapHeightInTiles];

        for (int y = 0; y < mapHeightInTiles; y++) {
            for (int x = 0; x < mapWidthInTiles; x++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);

                if (cell != null && cell.getTile() != null) {
                    // Assuming the boundary tiles have a property named "boundary" set to true
                    boolean isBoundary = cell.getTile().getProperties().get("boundary", true, Boolean.class);
                    boundaryTiles[x][y] = isBoundary;
                }
            }
        }
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

        tiledMap.dispose();
    }

    public boolean isBoundary(int x, int y) {
        if (x < 0 || x >= boundaryTiles.length || y < 0 || y >= boundaryTiles[0].length) {
            // The position is outside the boundary of the tilemap
            return true;
        }
        return boundaryTiles[x][y];
    }

    public int getWidth() {
        return boundaryTiles.length;
    }
    public int getHeight() {
        return boundaryTiles[0].length;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }
}
