package com.voider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character extends Sprite {
    public enum State { IDLING, WALKING_RIGHT, WALKING_LEFT};
    public State currentState;
    public State previousState;
    public Texture characterTexture;
    public Vector2 position;
    private Animation<Texture> charIdle;
    private Animation<Texture> charWalk;
    private float stateTimer;
    public float speed;
    public Character() {
        this.characterTexture = new Texture("char/idle/char32x32 idle 1.png");
        this.position = new Vector2();
        this.speed = 100f;

        currentState = State.IDLING;
        previousState = State.IDLING;

        Array<Texture> frame1 = new Array<Texture>();

        //Walk Animation
//        for (int i = 1; i < 4; i++) {
//            frame1.add(new TextureRegion(new Texture("char/idle/idle_spritesheet.png"), i*32, 32,32, 32));
//        }
        frame1.add(new Texture("char/walk/char32x32 walk1.png"));
        frame1.add(new Texture("char/walk/char32x32 walk3.png"));
        frame1.add(new Texture("char/walk/char32x32 walk2.png"));
        charWalk = new Animation<Texture>(0.1f, frame1);

        Array<Texture> frame2 = new Array<Texture>();
        //Idle Animation
        frame2.add(new Texture("char/idle/char32x32 idle 1.png"));
        frame2.add(new Texture("char/idle/char32x32 idle 2.png"));
        charIdle = new Animation<Texture>(0.1f, frame2);
    }
    public void update(float delta, float joystickX, float joystickY) {
        // Update the character's position based on input or game logic
        float deltaX = joystickX * speed * delta;
        float deltaY = joystickY * speed * delta;
        position.x += deltaX;
        position.y += deltaY;

        this.characterTexture = getFrame(delta);
    }

    public Texture getFrame(float deltaTime) {
        currentState = getState();
        Texture region;

        switch (currentState) {
            case IDLING:
                region = charIdle.getKeyFrame(stateTimer);
            case WALKING_LEFT:
                region = charWalk.getKeyFrame(stateTimer, true);
                break;
            case WALKING_RIGHT:
                region = charWalk.getKeyFrame(stateTimer, true);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentState);
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        return this.currentState;
    }

    //Set State of Character
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

    public void render(SpriteBatch spriteBatch, boolean flip) {
        // Render the character at its current position
        if (flip) {
            spriteBatch.draw(this.characterTexture, position.x + this.characterTexture.getWidth(), position.y,
                    -this.characterTexture.getWidth(), this.characterTexture.getHeight());
        } else {
            spriteBatch.draw(this.characterTexture, position.x, position.y);
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
