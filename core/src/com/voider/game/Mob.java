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


    private float distanceToMove;
    private boolean movingRight;

    public Mob(TextureRegion[] sprites, String mobType, float radius, Character player) {
        this.sprites = sprites;
        this.mobType = mobType;
        this.radius = radius;
        this.player = player;
        setSize(sprites[0].getRegionWidth(), sprites[0].getRegionHeight());

        movementSpeed = 30;
        originalX = getX();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Update the mob's behavior and attributes
        update(delta);
    }

    public void update(float delta) {
        // Check the distance between the mob and the player
        float distanceToPlayerX = player.getPosition().x - getX();
        float distanceToPlayerY = player.getPosition().y - getY();
        float totalDistanceToPlayer = (float) Math.sqrt(distanceToPlayerX * distanceToPlayerX + distanceToPlayerY * distanceToPlayerY);

        // Check if the player is within the mob's radius
        if (totalDistanceToPlayer <= radius) {
            // Move towards the player
            float directionX = distanceToPlayerX / totalDistanceToPlayer;
            float directionY = distanceToPlayerY / totalDistanceToPlayer;

            float distanceMovedX = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerX));
            float distanceMovedY = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerY));

            setX(getX() + distanceMovedX * directionX);
            setY(getY() + distanceMovedY * directionY);
        } else {
            // Perform random left and right movement
            randomMovement(delta);
        }
    }




    public void randomMovement(float delta) {
        // Check if the mob is currently moving right
        if (movingRight) {
            // Move right
            float distanceMoved = Math.min(movementSpeed * delta, distanceToMove);
            setX(getX() + distanceMoved);
            distanceToMove -= distanceMoved;

            // Check if the distance has been covered
            if (distanceToMove <= 0) {
                // Change direction to left
                movingRight = false;
                distanceToMove = MathUtils.random(50, 200);
            }
        } else {
            // Move left
            float distanceMoved = Math.min(movementSpeed * delta, distanceToMove);
            setX(getX() - distanceMoved);
            distanceToMove -= distanceMoved;

            // Check if the distance has been covered
            if (distanceToMove <= 0) {
                // Change direction to right
                movingRight = true;
                distanceToMove = MathUtils.random(50, 200);
            }
        }
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