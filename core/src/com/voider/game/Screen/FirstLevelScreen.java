package com.voider.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.voider.game.Bullet;
import com.voider.game.Character;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.voider.game.Mob;
import com.voider.game.Scene.HUD;
import com.voider.game.ShootingButton;
import com.voider.game.TileMap;
import com.voider.game.Weapon;


public class FirstLevelScreen implements Screen {
    private static final float DEFAULT_ZOOM = 0.17f; //default zoom
    private float initialCameraX = 470; // Adjust this value to set the initial x-coordinate of the camera
    private float initialCameraY = 935; // Adjust this value to set the initial y-coordinate of the camera
    private OrthographicCamera gameCam;
    private SpriteBatch batch;

    private TiledMap tiledMap;
    private HUD hud;

    private OrthogonalTiledMapRenderer mapRenderer;
    private Character character;
    private Touchpad touchpad;
    private Stage stage;

    private ShootingButton shootingButton;

    private Array<Bullet> bullets;

    private Array<Mob> mobs;
    public FirstLevelScreen() {
        //Get map
        tiledMap = new TmxMapLoader().load("map/dungeon1/test-map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        bullets = new Array<>();
        loadCamera();
        loadCharacter();
        loadControls();
        initialiseMobs();
        loadHUD();
    }
    @Override
    public void show() {
        // Initialize resources or perform any setup
    }


    //loadcontrols() = loadjoystick + loadshootingbutton

    // only allowing one stage so we have to load both in one function

    public void loadControls() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        // Create the joystick
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

        touchpad.addListener(new ChangeListener() {
            private boolean isTouchpadActive = false; // Track if the touchpad is active

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (touchpad.isTouched()) {
                    // Touchpad is being touched
                    isTouchpadActive = true;

                    float knobPercentX = touchpad.getKnobPercentX();
                    float knobPercentY = touchpad.getKnobPercentY();
                    rotateGun(knobPercentX, knobPercentY);
                } else if (isTouchpadActive) {
                    // Touchpad was released after being touched
                    isTouchpadActive = false;

                }
            }
        });


        stage.addActor(touchpad);

        // Create the shooting button
        shootingButton = new ShootingButton(character);
        stage.addActor(shootingButton);

        Gdx.input.setInputProcessor(stage);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float knobPercentX = touchpad.getKnobPercentX();
                float knobPercentY = touchpad.getKnobPercentY();

                if (knobPercentY > 0f || knobPercentY < 0f) {
                    if (knobPercentX > 0f) {
                        character.setState("RIGHT");
                    } else if (knobPercentX < -0f) {
                        character.setState("LEFT");
                    } else {
                        character.setState("IDLE");
                    }
                } else {
                    character.setState("IDLE");
                }
            }
        });
    }

    private void loadHUD() {
        hud = new HUD(character);
        hud.setPosition(10, Gdx.graphics.getHeight() - 10);

        stage.addActor(hud);
    }

    public void loadCamera() {
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false);
        gameCam.zoom = DEFAULT_ZOOM;

        // Set the initial camera position to a custom value
        gameCam.position.set(initialCameraX, initialCameraY, 0);

        gameCam.update();
    }

    public void loadCharacter() {
        batch = new SpriteBatch();
        TileMap tileMap = new TileMap("map/dungeon1/test-map.tmx"); // Create the tileMap object
        character = new Character(tileMap);
        character.setPosition(initialCameraX - 137, initialCameraY - 10);
        character.setGun(new Weapon(new Texture("weap/gun/gun inactive.png"), this.character));
        character.getGun().setPosition(initialCameraX - 137, initialCameraY - 10);
    }

    private void initialiseMobs() {
        mobs = new Array<>();

        // Create the tileMap object
        TileMap tileMap = new TileMap("map/dungeon1/test-map.tmx");
        MapObjects objects = tiledMap.getLayers().get("ChortPosition").getObjects();
        for (MapObject object : objects) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);

            Mob mob = new Mob(tileMap, x, y, "chort", 100, character);
            mob.setPosition(x, y);
            mobs.add(mob);
        }
    }

    public void rotateGun(float knobPercentX, float knobPercentY) {
        // Calculate the angle of rotation based on the joystick input
        float angleRad = MathUtils.atan2(knobPercentY, knobPercentX);
        float angleDeg = MathUtils.radiansToDegrees * angleRad;

        // Set the rotation angle of the gun in the Weapon class
        character.getGun().setAngle(angleDeg);
    }

    @Override
    public void render(float delta) {
// Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// Update the character's position and state based on the joystick input
        float joystickX = touchpad.getKnobPercentX();
        float joystickY = touchpad.getKnobPercentY();
        character.update(delta, joystickX, joystickY, mobs);

// Update the camera's position to center on the character
        float cameraX = character.getPosition().x; // Adjust this if necessary
        float cameraY = character.getPosition().y; // Adjust this if necessary
        gameCam.position.set(cameraX, cameraY, 0);
        gameCam.update();

// Render the tile map
        mapRenderer.setView(gameCam);
        mapRenderer.render();


// Render the character
        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();
        character.render(batch);
        batch.end();

// Update and render the controls
        stage.act(delta);
        stage.draw();

// Update and render the bullets
        batch.begin();
        for (Bullet bullet : character.getBullets()) {
            bullet.render(batch); // Render bullet
        }
        batch.end();

// Render the mobs
        batch.begin();
        for (Mob mob : mobs) {
            mob.act(delta);
            mob.render(batch);
        }
        batch.end();


        // Update and render the controls
        stage.act(delta);
        stage.draw();


    }


    @Override
    public void resize(int width, int height) {
        gameCam.viewportWidth = width;
        gameCam.viewportHeight = height;
        gameCam.update();
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


