package com.voider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Character {
    public Texture texture;
    public Vector2 position;

    public float speed;
    public Character(Texture texture) {
        this.texture = texture;
        this.position = new Vector2();
        this.speed = 100f;
    }
    public void update(float delta, float joystickX, float joystickY) {
        // Update the character's position based on input or game logic
        float deltaX = joystickX * speed * delta;
        float deltaY = joystickY * speed * delta;
        position.x += deltaX;
        position.y += deltaY;
    }

    public void render(SpriteBatch spriteBatch) {
        // Render the character at its current position
        spriteBatch.draw(texture, position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void dispose() {
        texture.dispose();
    }

}
