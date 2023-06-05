package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.voider.game.TileMap;

public class FirstLevelScreen implements Screen {
    private TileMap tileMap;

    public FirstLevelScreen() {
        tileMap = new TileMap("map/dungeon1/test-map.tmx");
    }

    @Override
    public void show() {
        // Initialize resources or perform any setup
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and render the tile map
        tileMap.render();

        // Other rendering logic
    }

    @Override
    public void resize(int width, int height) {
        // Resize the tile map viewport
        tileMap.resize(width, height);
    }

    @Override
    public void pause() {
        // Pause the screen (if needed)
    }

    @Override
    public void resume() {
        // Resume the screen (if needed)
    }

    @Override
    public void hide() {
        // Dispose of resources or perform any cleanup
    }

    @Override
    public void dispose() {
        // Dispose of resources
        tileMap.dispose();
    }
}

