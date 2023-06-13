package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character extends Sprite {
    private static final float FRAME_TIME = 0.2f;
    public enum State { IDLING, WALKING_RIGHT, WALKING_LEFT }
    public State currentState;
    public State previousState;
    public TextureAtlas textureAtlas;
    public Vector2 position;
    private Animation<TextureRegion> charIdle;
    private Animation<TextureRegion> charWalk;
    private float stateTime;
    public float speed;
    private boolean isLeft = false;

    private TileMap tileMap;

    public Character(TileMap tileMap) {
        textureAtlas = new TextureAtlas(Gdx.files.internal("char/character.atlas"));
        this.position = new Vector2();
        this.speed = 100f;


        currentState = State.IDLING;
        previousState = State.IDLING;

        charIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
        charWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));

        charIdle.setFrameDuration(FRAME_TIME);
        charWalk.setFrameDuration(FRAME_TIME);

        this.tileMap = tileMap;
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
    }


    public boolean isColliding(float x, float y) {
        int tileXStart = (int) (x / tileMap.getTileWidth());
        int tileXEnd = (int) ((x + 16) / tileMap.getTileWidth());
        int tileYStart = (int) (y / tileMap.getTileHeight());
        int tileYEnd = (int) ((y + 16) / tileMap.getTileHeight());

        // Check collisions for each corner of the character
        boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
        boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
        boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
        boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

        // Return true if any corner collides with a boundary tile
        return topLeft || topRight || bottomLeft || bottomRight;
    }



    public TextureRegion getFrame(float deltaTime) {
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
            Gdx.app.log("Txt", String.valueOf(textureWidth));
        } else {
            spriteBatch.draw(currentFrame, position.x, position.y, textureWidth, textureHeight);
            Gdx.app.log("Txt", String.valueOf(textureWidth));
        }
    }



    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void dispose() {

    }
}

