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

    public Character() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("char/character.atlas"));
        this.position = new Vector2();
        this.speed = 100f;

        currentState = State.IDLING;
        previousState = State.IDLING;

        charIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
        charWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));

<<<<<<< HEAD
        //Walk Animation
//        for (int i = 1; i < 4; i++) {
//            frame1.add(new TextureRegion(new Texture("char/idle/idle_spritesheet.png"), i*32, 32,32, 32));
//        }

        frame1.add(new Texture("char/walk/char32x32 walk1.png"));
        frame1.add(new Texture("char/walk/char32x32 walk2.png"));
        frame1.add(new Texture("char/walk/char32x32 walk3.png"));
        charWalk = new Animation<Texture>(0.4f, frame1);

        Array<Texture> frame2 = new Array<Texture>();
        //Idle Animation
        frame2.add(new Texture("char/idle/char32x32 idle 1.png"));
        frame2.add(new Texture("char/idle/char32x32 idle 2.png"));
        charIdle = new Animation<Texture>(0.4f, frame2);
=======
        charIdle.setFrameDuration(FRAME_TIME);
        charWalk.setFrameDuration(FRAME_TIME);
>>>>>>> 76d9fb74ec5139dbc4a48e6ae0f3c3cef223a245
    }

    public void update(float delta, float joystickX, float joystickY) {
        // Update the character's position based on input or game logic
        float deltaX = joystickX * speed * delta;
        float deltaY = joystickY * speed * delta;
        position.x += deltaX;
        position.y += deltaY;

        // Increment the stateTime for animation
        stateTime += delta;
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
