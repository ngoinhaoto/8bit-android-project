package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.voider.game.TileMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class FirstLevelScreen implements Screen {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private static final float DEFAULT_ZOOM = 0.17f; //default zoom
    private OrthographicCamera camera;

    private SpriteBatch batch;

    private Character character;
    private Stage stage;
    private Touchpad touchpad;



    public FirstLevelScreen() {
        tiledMap = new TmxMapLoader().load("map/dungeon1/test-map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        camera = new OrthographicCamera();

        camera.setToOrtho(false);
        camera.zoom = DEFAULT_ZOOM;
        // Set the initial camera position to a custom value
        float initialCameraX = 470; // Adjust this value to set the initial x-coordinate of the camera
        float initialCameraY = 935; // Adjust this value to set the initial y-coordinate of the camera
        camera.position.set(initialCameraX, initialCameraY, 0);
        camera.update();

        batch = new SpriteBatch();
        Texture characterTexture = new Texture("char/idle/char32x32 idle 1.png");
        character = new Character(characterTexture);
        character.setPosition(initialCameraX - 137, initialCameraY - 10);

        stage = new Stage();
        Texture touchpadBackground = new Texture("joystick/joystickbackground.png");
        Texture touchpadKnob = new Texture("joystick/joystickknob.png");

        Skin touchpadSkin = new Skin();

        touchpadSkin.add("touchpadBackground", touchpadBackground);
        touchpadSkin.add("touchpadKnob", touchpadKnob);

        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchpadBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchpadKnob");

        touchpad = new Touchpad(10, touchpadStyle);

        touchpad.setBounds(120, 120, 250, 250);

        stage.addActor(touchpad);

        Gdx.input.setInputProcessor(stage);

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
        mapRenderer.setView(camera);
        mapRenderer.render();
        // Other rendering logic

        // Rendering character
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        character.render(batch);
        batch.end();

        float joystickX = touchpad.getKnobPercentX();
        float joystickY = touchpad.getKnobPercentY();
        character.update(delta, joystickX, joystickY);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize the tile map viewport
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        stage.getViewport().update(width, height, true);

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
        tiledMap.dispose();
        mapRenderer.dispose();
        batch.dispose();
        character.dispose();
        touchpad.clearListeners();
        touchpad.remove();
        stage.dispose();

    }
}

