package com.voider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Weapon {
    private Texture texture; // The texture representing the weapon
    private Vector2 position; // The position of the weapon
    private float angle; // The rotation angle of the weapon
    private boolean isShoot = false;
    private float shootDuration = 0.5f; // Duration in seconds for which isShoot will be true
    private float shootTimer = 0.0f; // Timer to track the elapsed time

    public Weapon(Texture texture) {
        this.texture = texture;
        position = new Vector2();
        angle = 0.0f;
    }

    public void setState() {
        if (isShoot) {
            // If already shooting, reset the timer
            shootTimer = 0.0f;
        } else {
            // Start shooting by setting isShoot to true
            isShoot = true;
            shootTimer = 0.0f;
        }
    }

    private void updateTexture() {
        if (isShoot) {
            this.texture = new Texture("weap/gun/gun active.png");
        } else {
            this.texture = new Texture("weap/gun/gun inactive.png");
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
    public float getAngle() {
        return this.angle;
    }
    public void render(SpriteBatch spriteBatch) {
        updateTexture();
        boolean flipY = angle < -90 || angle > 90; // Check if angle is outside the range -90 to 90
        if (!flipY) {
            spriteBatch.draw(
                    texture,
                    position.x + 20,
                    position.y + 9,
                    texture.getWidth() / 2,
                    texture.getHeight() / 2,
                    texture.getWidth(),
                    texture.getHeight(),
                    1.0f,
                    flipY ? -1.0f : 1.0f, // Flip the texture vertically if flipY is true
                    angle,
                    0,
                    0,
                    texture.getWidth(),
                    texture.getHeight(),
                    false,
                    false
            );
        } else {
            spriteBatch.draw(
                    texture,
                    position.x + 4,
                    position.y + 9,
                    texture.getWidth() / 2,
                    texture.getHeight() / 2,
                    texture.getWidth(),
                    texture.getHeight(),
                    1.0f,
                    flipY ? -1.0f : 1.0f, // Flip the texture vertically if flipY is true
                    angle,
                    0,
                    0,
                    texture.getWidth(),
                    texture.getHeight(),
                    false,
                    false
            );
        }
    }

    public void update(float deltaTime) {
        if (isShoot) {
            shootTimer += deltaTime;
            if (shootTimer >= shootDuration) {
                // Shooting duration has elapsed, switch isShoot back to false
                isShoot = false;
            }
        }
    }

    public void dispose() {
        texture.dispose();
    }
}
