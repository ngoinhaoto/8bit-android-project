package com.voider.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Mob extends Actor {
    private TextureRegion[] sprites;
    private String mobType;
    private float radius; // if player enters their radius, they will follow player
    private Character player;
    private float movementSpeed;
    private float targetX;
    private float targetY;
    private float originalX;

    public Mob(TextureRegion[] sprites, String mobType, float radius, Character player) {
        this.sprites = sprites;
        this.mobType = mobType;
        this.radius = radius;
        this.player = player;
        setSize(sprites[0].getRegionWidth(), sprites[0].getRegionHeight());

        movementSpeed = 350;
        originalX = getX();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Update the mob's behavior and attributes
        update(delta);
    }

    public void update(float delta) {
        float playerX = player.getX();
        float playerY = player.getY();
        float distance = Math.abs(getX() - playerX);

        // mob would move toward player if they are in the radius
        if (distance <= radius) {
            targetX = playerX - getWidth() / 2;
            targetY = playerY - getHeight() / 2;
        } else {
            // Random movement if player is outside the radius
            float threshold = 1f; // Adjust this threshold as needed
            if (MathUtils.isEqual(getX(), originalX, threshold) || MathUtils.isEqual(getX(), targetX, threshold)) {
                // Generate a random target position within a range
                float minX = originalX - 100; // Adjust the range as needed
                float maxX = originalX + 100;
                targetX = MathUtils.random(minX, maxX);

                // Generate a random target position along the y-axis within a range
                float minY = getY() - 100; // Adjust the range as needed
                float maxY = getY() + 100;
                targetY = MathUtils.random(minY, maxY);
            }
        }

        // Calculate the difference between the current position and the target position
        float deltaX = targetX - getX();
        float deltaY = targetY - getY();

        // Calculate the angle between the current position and the target position
        float angle = MathUtils.atan2(deltaY, deltaX);

        // Calculate the movement speed along the x-axis and y-axis
        float speedX = movementSpeed * MathUtils.cos(angle) * delta;
        float speedY = movementSpeed * MathUtils.sin(angle) * delta ;

        // Move towards the target position
        setX(getX() + speedX * delta);
        setY(getY() + speedY * delta);
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        TextureRegion sprite = sprites[0];  // Default sprite

        // Retrieve the appropriate sprite based on mobType
        if (mobType.equals("Type1")) {
            sprite = sprites[1];
        } else if (mobType.equals("Type2")) {
            sprite = sprites[2];
        }

        batch.setColor(1, 1, 1, parentAlpha);  // Set the batch color with parent alpha
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }
}