package com.voider.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.voider.game.Bullet;
import com.voider.game.Character;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.voider.game.Mob;
import com.voider.game.Portal;
import com.voider.game.Scene.HUD;
import com.voider.game.ShootingButton;
import com.voider.game.TileMap;
import com.voider.game.Voider;
import com.voider.game.Weapon;


public class ThirdLevelScreen  implements Screen, Mob.MobDeathListener {
    private static final float DEFAULT_ZOOM = 0.17f; //default zoom
    private final Voider game;
    private float initialCameraX = 1750; // Adjust this value to set the initial x-coordinate of the camera
    private float initialCameraY = 1050; // Adjust this value to set the initial y-coordinate of the camera
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
    private boolean isGameOver;
    private float overlayAlpha;  // Current alpha value for the overlay color
    private final float OVERLAY_FADE_SPEED = 1.5f; // Speed at which the overlay color fades (adjust as needed)
    private Color overlayColor;


    /// keep track of mobs killed this level and to enable gates

    private int mobsKilledThisLevel;


    private boolean gate1BoundaryEnabled = true;
    private boolean gate2BoundaryEnabled = true;
    private boolean gate3BoundaryEnabled = true;

    private boolean gate4BoundaryEnabled = true;
    private boolean gate5BoundaryEnabled = true;
    private boolean gate6BoundaryEnabled = true;
    private boolean gate7BoundaryEnabled = true;

    private Portal portal;
    private boolean portalVisible = false;


    private Sound gameStartSound;

    private Music backgroundMusic;

    private Sound gateSound = Gdx.audio.newSound(Gdx.files.internal("music/gate open 2.mp3"));
    private Sound portalSound = Gdx.audio.newSound(Gdx.files.internal("music/portal.mp3"));

    private float portalEnterTime;



//    public ThirdLevelScreen(Voider game, Character character) {
//        this.game = game;
//        this.character = character;
//        //Get map
//        tiledMap = new TmxMapLoader().load("map/dungeon1/level3.tmx");
//        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
//
//        overlayColor = new Color(0, 0, 0, 0); // Initial color with full transparency
//
//
//        gameStartSound = Gdx.audio.newSound(Gdx.files.internal("music/game-start-6104.mp3"));
//
//        bullets = new Array<>();
//        mobsKilledThisLevel=0;
//        loadPortal();
//        loadCamera();
//        loadCharacter();
//        loadControls();
//        initialiseMobs();
//        loadHUD();
//    }

    public ThirdLevelScreen(Voider game) {
        this.game = game;
        //Get map
        tiledMap = new TmxMapLoader().load("map/dungeon1/level3.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        overlayColor = new Color(0, 0, 0, 0); // Initial color with full transparency

        bullets = new Array<>();
        mobsKilledThisLevel=0;
        gameStartSound = Gdx.audio.newSound(Gdx.files.internal("music/game-start-6104.mp3"));

        loadPortal();
        loadCamera();
        loadCharacter();
        loadControls();
        initialiseMobs();
        loadHUD();
    }

    private void loadPortal() {
        MapLayer portalLayer = tiledMap.getLayers().get("PortalPosition");
        MapObject portalObj = portalLayer.getObjects().get("PortalPosition");
        float x = portalObj.getProperties().get("x", Float.class);
        float y = portalObj.getProperties().get("y", Float.class);
        portal = new Portal();
        portal.setPosition(x, y);
    }

    @Override
    public void show() {
        // Initialize resources or perform any setup

        gameStartSound.play(1f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/10 snow day.mp3"));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.35f);
                backgroundMusic.play();
            }
        }, 1.5f);

        portalEnterTime = 0f;

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
                if (character.getState() == Character.State.DEAD) {
                    return; // Return early if the character is dead
                }

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

                if (touchpad.isTouched()) {
                    // Touchpad is being touched
                    isTouchpadActive = true;

                    rotateGun(knobPercentX, knobPercentY);
                } else if (isTouchpadActive) {
                    // Touchpad was released after being touched
                    isTouchpadActive = false;
                }
            }
        });

        // Create the shooting button
        shootingButton = new ShootingButton(character);

        stage.addActor(touchpad);
        stage.addActor(shootingButton);
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

//    public void loadCharacter() {
//        batch = new SpriteBatch();
//        TileMap tileMap = new TileMap("map/dungeon1/level3.tmx"); // Create the tileMap object
//
//
//
//        character.setTileMap(tileMap);
//        character.setState("IDLE");
//        character.setPosition(initialCameraX - 137, initialCameraY - 10);
//        character.getGun().setPosition(initialCameraX - 137, initialCameraY - 10);
//
//    }

    public void loadCharacter() {
        batch = new SpriteBatch();
        TileMap tileMap = new TileMap("map/dungeon1/level3.tmx"); // Create the tileMap object

        character = new Character(tileMap);
        character.setPosition(initialCameraX - 137, initialCameraY - 10);
        character.setGun(new Weapon(new Texture("weap/gun/gun active.png"), this.character));
        character.getGun().setPosition(initialCameraX - 137, initialCameraY - 10);
    }

    private void initialiseMobs() {
        mobs = new Array<>();

        // Create the tileMap object
        TileMap tileMap = new TileMap("map/dungeon1/level3.tmx");
        MapObjects objects = tiledMap.getLayers().get("BigDemonPosition").getObjects();
        for (MapObject object : objects) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);

            Mob mob = new Mob(tileMap, x, y, "bigDemon", true, 110, 2, character, this);
            mob.setPosition(x, y);
            mobs.add(mob);
        }

        MapObjects wizardObjects = tiledMap.getLayers().get("WizardPosition").getObjects();

        for (MapObject object: wizardObjects) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);

            Mob mob = new Mob(tileMap, x, y, "wizard", false, 40, 2, character, this);
            mob.setPosition(x, y);
            mobs.add(mob);
        }

        MapObjects witchObjects = tiledMap.getLayers().get("WitchPosition").getObjects();

        for (MapObject object: witchObjects) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);

            Mob mob = new Mob(tileMap, x, y, "witch", false, 40, 2, character, this);
            mob.setPosition(x, y);
            mobs.add(mob);
        }
    }
    @Override
    public void onMobDeath() {
        mobsKilledThisLevel++;
        character.setMobsKilled(character.getMobsKilled() + 1);

        MapLayer gateLayer = tiledMap.getLayers().get("ActualGate");
        MapObject gate1Object = gateLayer.getObjects().get("Gate1Object");
        MapObject gate2Object = gateLayer.getObjects().get("Gate2Object");
        MapObject gate3Object = gateLayer.getObjects().get("Gate3Object");
        MapObject gate4Object = gateLayer.getObjects().get("Gate4Object");
        MapObject gate5Object = gateLayer.getObjects().get("Gate5Object");
        MapObject gate6Object = gateLayer.getObjects().get("Gate6Object");
        MapObject gate7Object = gateLayer.getObjects().get("Gate7Object");

        MapLayer gate1 = tiledMap.getLayers().get("Gate1");
        MapLayer gate2 = tiledMap.getLayers().get("Gate2");
        MapLayer gate3 = tiledMap.getLayers().get("Gate3");
        MapLayer gate4 = tiledMap.getLayers().get("Gate4");
        MapLayer gate5 = tiledMap.getLayers().get("Gate5");
        MapLayer gate6 = tiledMap.getLayers().get("Gate6");
        MapLayer gate7 = tiledMap.getLayers().get("Gate7");


        MapLayer gate1Broken = tiledMap.getLayers().get("Gate1Broken");
        MapLayer gate2Broken = tiledMap.getLayers().get("Gate2Broken");
        MapLayer gate3Broken = tiledMap.getLayers().get("Gate3Broken");
        MapLayer gate4Broken = tiledMap.getLayers().get("Gate4Broken");
        MapLayer gate5Broken = tiledMap.getLayers().get("Gate5Broken");
        MapLayer gate6Broken = tiledMap.getLayers().get("Gate6Broken");
        MapLayer gate7Broken = tiledMap.getLayers().get("Gate7Broken");

        if (mobsKilledThisLevel >= 3 && gate1BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate1Object, false);
            gate1BoundaryEnabled = false; // Update the flag to prevent repeated property setting
            // make the gate disappear afterward
            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate1Object, false);
            }

            gate1.setVisible(false);
            gate1Broken.setVisible(true);
            gateSound.play();
        }

        if (mobsKilledThisLevel >= 5 && gate2BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate2Object, false);

            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate2Object, false);
            }

            gate2BoundaryEnabled = false;

            gate2.setVisible(false);
            gate2Broken.setVisible(true);

            gateSound.play();
        }

        if (mobsKilledThisLevel >= 6 && gate3BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate3Object, false);

            gate3BoundaryEnabled = false;

            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate3Object, false);
            }

            gate3.setVisible(false);
            gate3Broken.setVisible(true);

            gateSound.play();
        }

        if (mobsKilledThisLevel >= 9 && gate4BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate4Object, false);

            gate4BoundaryEnabled = false;
            gate4.setVisible(false);
            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate4Object, false);
            }

            gate4Broken.setVisible(true);

            gateSound.play();
        }

        if (mobsKilledThisLevel >= 11 && gate5BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate5Object, false);

            gate5BoundaryEnabled = false;
            gate5.setVisible(false);
            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate5Object, false);
            }

            gate5Broken.setVisible(true);

            gateSound.play();
        }



        if (mobsKilledThisLevel >= 15 && gate7BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate7Object, false);

            gate7BoundaryEnabled = false;
            gate7.setVisible(false);
            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate7Object, false);
            }

            gate7Broken.setVisible(true);

            gateSound.play();
        }

        if (mobsKilledThisLevel >= 16 && gate6BoundaryEnabled) {
            character.getTileMap().updateGateBoundary(gate6Object, false);

            gate6BoundaryEnabled = false;
            gate6.setVisible(false);
            for (Mob mob : mobs) {
                mob.getTileMap().updateGateBoundary(gate6Object, false);
            }

            gate6Broken.setVisible(true);

            gateSound.play();

            portalVisible = true;
        }

    }



    public void rotateGun(float knobPercentX, float knobPercentY) {
        // Calculate the angle of rotation based on the joystick input
        float angleRad = MathUtils.atan2(knobPercentY, knobPercentX);
        float angleDeg = MathUtils.radiansToDegrees * angleRad;

        // Set the rotation angle of the gun in the Weapon class
        character.getGun().setAngle(angleDeg);
    }

    private void showGameOverScreen() {
        overlayAlpha = 0f; // Reset the overlay alpha to 0
        game.setScreen(new GameOverScreen(this.game));
    }

    @Override
    public void render(float delta) {
        // Check for character's death and start game over process
        if (character.getState() == Character.State.DEAD && !isGameOver) {
            isGameOver = true;
        }

        // Render the portal if it's visible
        if (portalVisible) {
            batch.begin();
            portal.update(delta);
            portal.render(batch);
            batch.end();
        }

        if (isGameOver) {
            // Gradually darken the screen by increasing the alpha value of the overlay color
            overlayAlpha = Math.min(overlayAlpha + OVERLAY_FADE_SPEED * delta, 1f);
            overlayColor.a = overlayAlpha;

            // Once the screen is fully darkened, show the GameOverScreen
            if (overlayAlpha >= 1f) {
                showGameOverScreen();
                return;
            }
        }

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

        // Update and render the controls
        stage.act(delta);
        stage.draw();

        // Render the portal if it's visible
        // Render the portal if it's visible
        if (portalVisible) {
            batch.begin();
            portal.update(delta);
            portal.render(batch);
            batch.end();

            // Check if the character enters the portal

            if (character.getCollisionRectangle().overlaps(portal.getCollisionRectangle())) {
                // Increase portalEnterTime by delta
                portalEnterTime += delta;

                // Check if the portal sound should play
                if (portalEnterTime <= 1f) {
                    portalSound.play();
                }

                // Check if the time limit has been reached
                if (portalEnterTime >= 1f) {
                    game.setScreen(new WinnerScreen(game, character));
                    portalSound.stop();
                }
            }

        }


        // Render the mobs
        batch.begin();
        for (Mob mob : mobs) {
            mob.act(delta);
            mob.render(batch);
        }
        batch.end();

        // Render the character
        batch.setProjectionMatrix(gameCam.combined);
        batch.begin();
        character.render(batch);
        batch.end();

        // Update and render the bullets
        batch.begin();
        for (Bullet bullet : character.getBullets()) {
            bullet.render(batch); // Render bullet
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
        if (portal != null) {
            portal.dispose();
        }

        // Stop and dispose of the background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }

        // Stop and dispose of any other sounds or resources used in the screen
        gameStartSound.dispose();
        gateSound.dispose();
        portalSound.dispose();
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

        backgroundMusic.dispose();
        gateSound.dispose();
        gameStartSound.dispose();
    }
}


