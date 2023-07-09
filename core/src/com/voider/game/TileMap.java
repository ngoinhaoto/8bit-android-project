package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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

    private boolean[][] gateTiles; // Array to store gate tiles' positions

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

        MapLayer objectLayer = tiledMap.getLayers().get("ActualWall");

        // Get the tile size from the tile layer
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(2);
        tileWidth = tileLayer.getTileWidth();
        tileHeight = tileLayer.getTileHeight();

        boundaryTiles = new boolean[tileLayer.getWidth()][tileLayer.getHeight()];

        for (MapObject object : objectLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangleObject = (RectangleMapObject) object;
                float x = rectangleObject.getRectangle().getX();
                float y = rectangleObject.getRectangle().getY();
                float width = rectangleObject.getRectangle().getWidth();
                float height = rectangleObject.getRectangle().getHeight();

                // Check if the object has the "boundary" property set to true
                boolean isBoundary = false;
                if (rectangleObject.getProperties().containsKey("boundary")) {
                    isBoundary = rectangleObject.getProperties().get("boundary", Boolean.class);
                }

                if (isBoundary) {
                    // Convert object coordinates to tile coordinates
                    int startX = (int) (x / tileWidth);
                    int startY = (int) (y / tileHeight);
                    int endX = (int) ((x + width) / tileWidth);
                    int endY = (int) ((y + height) / tileHeight);

                    // Mark the corresponding tiles as boundary tiles
                    for (int tileX = startX; tileX <= endX; tileX++) {
                        for (int tileY = startY; tileY <= endY; tileY++) {
                            boundaryTiles[tileX][tileY] = true;
                        }
                    }
                }
            }
        }

        // working with gate layer
        MapLayer gateLayer = tiledMap.getLayers().get("ActualGate");

        if (gateLayer != null) {
            gateTiles = new boolean[tileLayer.getWidth()][tileLayer.getHeight()];

            for (MapObject object : gateLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    float x = rectangleObject.getRectangle().getX();
                    float y = rectangleObject.getRectangle().getY();
                    float width = rectangleObject.getRectangle().getWidth();
                    float height = rectangleObject.getRectangle().getHeight();

                    // Check if the object has the "boundary" property set to true
                    boolean isBoundary = false;
                    if (rectangleObject.getProperties().containsKey("boundary")) {
                        isBoundary = rectangleObject.getProperties().get("boundary", Boolean.class);
                    }

                    // Convert object coordinates to tile coordinates
                    int startX = (int) (x / tileWidth);
                    int startY = (int) (y / tileHeight);
                    int endX = (int) ((x + width) / tileWidth);
                    int endY = (int) ((y + height) / tileHeight);

                    // Mark the corresponding tiles as boundary tiles
                    for (int tileX = startX; tileX <= endX; tileX++) {
                        for (int tileY = startY; tileY <= endY; tileY++) {
                            gateTiles[tileX][tileY] = isBoundary;
                        }
                    }
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
        return boundaryTiles[x][y] || gateTiles[x][y];
    }

    public void updateGateBoundary(MapObject gateObject,boolean gateBoundary) {
        MapLayer gateLayer = tiledMap.getLayers().get("ActualGate");

        if (gateObject instanceof RectangleMapObject) {
            gateObject.getProperties().put("boundary", gateBoundary);
        }

        updateGateTiles(gateLayer);
    }



    private void updateGateTiles(MapLayer gateLayer) {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(2);

        gateTiles = new boolean[tileLayer.getWidth()][tileLayer.getHeight()];

        for (MapObject object : gateLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangleObject = (RectangleMapObject) object;
                float x = rectangleObject.getRectangle().getX();
                float y = rectangleObject.getRectangle().getY();
                float width = rectangleObject.getRectangle().getWidth();
                float height = rectangleObject.getRectangle().getHeight();

                boolean isBoundary = false;
                if (rectangleObject.getProperties().containsKey("boundary")) {
                    isBoundary = rectangleObject.getProperties().get("boundary", Boolean.class);
                }

                int startX = (int) (x / tileWidth);
                int startY = (int) (y / tileHeight);
                int endX = (int) ((x + width) / tileWidth);
                int endY = (int) ((y + height) / tileHeight);

                for (int tileX = startX; tileX <= endX; tileX++) {
                    for (int tileY = startY; tileY <= endY; tileY++) {
                        gateTiles[tileX][tileY] = isBoundary;
                        Gdx.app.log("TileMap", "Tile Position: (" + tileX + ", " + tileY + "), Gate Tile Value: " + gateTiles[tileX][tileY]);
                    }
                }
            }
        }
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
