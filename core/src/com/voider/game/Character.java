package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character extends Sprite {
    //Stat
    private final int maxHP = 5;
    private final int maxARM = 3;
    private int currentHP;
    private int currentARM;
    private static final float FRAME_TIME = 0.18f;
    private enum State { IDLING, WALKING_RIGHT, WALKING_LEFT }
    public State currentState;

    public State previousState;
    public TextureAtlas textureAtlas;
    public Vector2 position;
    private Animation<TextureRegion> charIdle;
    private Animation<TextureRegion> charWalk;
    private float stateTime;
    public float speed;
    private boolean isLeft = false;
    private Weapon gun;


    private TileMap tileMap;
    private Array<Bullet> bullets;
    public Character(TileMap tileMap) {
        setHP(getMaxHP());
        setARM(getMaxARM());
        textureAtlas = new TextureAtlas(Gdx.files.internal("char/char.atlas"));
        this.position = new Vector2();
        this.speed = 100f;

        currentState = State.IDLING;
        previousState = State.IDLING;

        charIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
        charWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));

        charIdle.setFrameDuration(FRAME_TIME);
        charWalk.setFrameDuration(FRAME_TIME);

        this.tileMap = tileMap;

        bullets = new Array<>();
    }

    public void update(float delta, float joystickX, float joystickY) {
        // Update the character's position based on input or game logic
        float deltaX = joystickX * speed * delta;
        float deltaY = joystickY * speed * delta;
        float desiredX = position.x + deltaX - getWidth() / 2;
        float desiredY = position.y + deltaY - getHeight() / 2;

        // Check if the desired position collides with any boundary tile
        if (!isColliding(desiredX, position.y)) {
            position.x = desiredX; // Horizontal movement
        }

        if (!isColliding(position.x, desiredY)) {
            position.y = desiredY; // Vertical movement
        }

        // Increment the stateTime for animation
        stateTime += delta;
        // Update the gun
        this.gun.update(delta);

        // Update the bullets' positions and check for collision with boundaries
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);

            if (isColliding_b(bullet.getPosition().x, bullet.getPosition().y-4)) {
                // Handle bullet collision with boundaries or other objects
                bullets.removeIndex(i);
            } else if (isBulletOffScreen(bullet)) {
                bullets.removeIndex(i);
            }
        }
    }

    private boolean isBulletOffScreen(Bullet bullet) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float bulletWidth = bullet.getBulletTexture().getRegionWidth();
        float bulletHeight = bullet.getBulletTexture().getRegionHeight();

        float bulletX = bullet.getPosition().x;
        float bulletY = bullet.getPosition().y;

        return bulletX < -bulletWidth || bulletX > screenWidth || bulletY < -bulletHeight || bulletY > screenHeight;
    }

    public void shoot() {
        //Change state of Gun
        this.gun.setState();

        // Create a new bullet,
        float bulletSpeed = 500;

        float velocityX = 0;
        float velocityY = 0;

        if (!isLeft) {
            velocityX = bulletSpeed;
        } else {
            velocityX = -bulletSpeed;
        }

        Bullet bullet;
        // getPosition().y + 10 because the bullet needs to be fired from the arm of the character.
        if (!isLeft) {
            bullet = new Bullet(getPosition().x + 24, getPosition().y + 10, velocityX, velocityY, false);
        } else {
            bullet = new Bullet(getPosition().x - 4, getPosition().y + 10, velocityX, velocityY, true);
        }
        bullets.add(bullet);
    }

    public boolean isColliding_b(float x, float y) {
        int tileXStart = (int) (x / tileMap.getTileWidth());
        int tileXEnd = (int) ((x) / tileMap.getTileWidth());
        int tileYStart = (int) (y / tileMap.getTileHeight());
        int tileYEnd = (int) ((y) / tileMap.getTileHeight());

        // Check collisions for each corner of the character
        boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
        boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
        boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
        boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

        // Return true if any corner collides with a boundary tile
        return topLeft || topRight || bottomLeft || bottomRight;
    }

    public boolean isColliding(float x, float y) {
        int tileXStart = (int) (x / tileMap.getTileWidth());
        int tileXEnd = (int) ((x+16) / tileMap.getTileWidth());
        int tileYStart = (int) (y / tileMap.getTileHeight());
        int tileYEnd = (int) ((y+16) / tileMap.getTileHeight());

        // Check collisions for each corner of the character
        boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
        boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
        boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
        boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

        // Return true if any corner collides with a boundary tile
        return topLeft || topRight || bottomLeft || bottomRight;
    }

    private TextureRegion getFrame(float deltaTime) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case IDLING:
                region = charIdle.getKeyFrame(stateTime, true);
                break;
            case WALKING_LEFT:
                region = charWalk.getKeyFrame(stateTime, true);
                this.isLeft = true;
                break;
            case WALKING_RIGHT:
                region = charWalk.getKeyFrame(stateTime, true);
                this.isLeft = false;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentState);
        }
        return region;
    }

    public State getState() {
        return this.currentState;
    }

    // Set State of Character
    public void setState(String stt) {
        // Handle state change
        switch (stt) {
            case "IDLE":
                // Code to set the character to idle state
                this.previousState = currentState;
                currentState = State.IDLING;
                break;
            case "LEFT":
                // Code to set the character to move left state
                this.previousState = currentState;
                currentState = State.WALKING_LEFT;
                break;
            case "RIGHT":
                // Code to set the character to move right state
                this.previousState = currentState;
                currentState = State.WALKING_RIGHT;
                break;
            default:
                currentState = State.IDLING;
                break;
        }
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());
        // Render the character at its current position
        float textureWidth = 32; // Set the desired texture width
        float textureHeight = 32; // Set the desired texture height
        if (isLeft) {
            spriteBatch.draw(currentFrame, position.x + textureWidth, position.y,
                    -textureWidth, textureHeight);
        } else {
            spriteBatch.draw(currentFrame, position.x, position.y, textureWidth, textureHeight);
        }
        // Render the gun
        gun.setPosition(position.x, position.y); // Set the gun position same as the character's position
        gun.render(spriteBatch);

        // Render the bullets
        for (Bullet bullet : bullets) {
            bullet.render(spriteBatch, gun.getAngle());
        }
    }
    public void setGun(Weapon gun) {
        this.gun = gun;
    }
    public Weapon getGun() {
        return this.gun;
    }
    public Array<Bullet> getBullets() {
        return bullets;
    }
    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(float x, float y) {
        position.set(x, y);
    }
    public void setHP(int newHP) {
        this.currentHP = newHP;
    }
    public int getMaxHP() {
        return this.maxHP;
    }
    public int getCurrentHP() {
        return this.currentHP;
    }
    public void setARM(int newArmor) {
        this.currentARM = newArmor;
    }
    public int getMaxARM() {
        return this.maxARM;
    }
    public int getCurrentARM() {
        return this.currentARM;
    }
    public void dispose() {
        // Dispose of any resources here if needed
        gun.dispose();
    }
}

