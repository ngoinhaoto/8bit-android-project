package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Character extends Sprite {
    //Stat
    private final int maxHP = 6;
    private final int maxARM = 4;
    private int currentHP;
    private int currentARM;
    private static final float FRAME_TIME = 0.18f;
    enum State { IDLING, WALKING, DEAD }
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

    private Array<Mob> mobsInRange; // List to store mobs within attacking radius

    public Character(TileMap tileMap) {
        setHP(getMaxHP());
        setARM(getMaxARM());
        textureAtlas = new TextureAtlas(Gdx.files.internal("char/char.atlas"));
        this.position = new Vector2();
        this.speed = 110f;

        currentState = State.IDLING;
        previousState = State.IDLING;

        charIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
        charWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));

        charIdle.setFrameDuration(FRAME_TIME);
        charWalk.setFrameDuration(FRAME_TIME);

        this.tileMap = tileMap;

        bullets = new Array<>();
        mobsInRange = new Array<>();
    }

    public void updateGunAim() {
        if (mobsInRange.isEmpty()) {
            // If there are no mobs in range, reset the gun's aim
            return;
        }

        // Find the nearest mob
        Mob nearestMob = findNearestMob();

        // Get the position of the character and the nearest mob
        Vector2 characterPosition = getPosition();
        Vector2 mobPosition = nearestMob.getPosition();

        // Calculate the direction vector from character to mob
        Vector2 direction = mobPosition.cpy().sub(characterPosition);

        // Calculate the angle using atan2
        float angle = MathUtils.atan2(direction.y, direction.x);

        // Convert the angle to degrees
        angle = MathUtils.radiansToDegrees * angle;

        // Set the angle for the gun
        this.gun.setAngle(angle);
    }



    private Mob findNearestMob() {
        Mob nearestMob = null;
        float nearestDistance = Float.MAX_VALUE;
        Vector2 characterPosition = new Vector2(getX(), getY());

        for (Mob mob : mobsInRange) {
            Vector2 mobPosition = new Vector2(mob.getX(), mob.getY());
            float distance = characterPosition.dst(mobPosition);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestMob = mob;
            }
        }

        return nearestMob;
    }


    // Add this method to update the mobsInRange list based on the character's attacking radius
    public void updateMobsInRange(float attackingRadius, Array<Mob> allMobs) {
        mobsInRange.clear(); // Clear the current list of mobs in range

        for (Mob mob : allMobs) {
            Vector2 mobPosition = new Vector2(mob.getPosition().x, mob.getPosition().y);
            Vector2 characterPosition = new Vector2(getPosition().x, getPosition().y);
            float distance = characterPosition.dst(mobPosition);

            if (distance <= attackingRadius) {
                mobsInRange.add(mob);
            }
        }

    }


    public void update(float delta, float joystickX, float joystickY, Array<Mob> allMobs) {


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


        // Update the mobs in range
        float attackingRadius = 170;
        updateMobsInRange(attackingRadius, allMobs);
        // Update the gun aim
        updateGunAim();
        // Update the gun
        this.gun.update(delta);


        // Update the bullets' positions and check for collision with mobs
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);

            // Check for collision with mobs within attacking radius
            boolean bulletHit = false;
            for (Mob mob : mobsInRange) {
                if (mob.getState() != Mob.State.DEAD && bullet.getBoundingRectangle().overlaps(mob.getBoundingRectangle())) {
                    mob.takeDamage(bullet.getDamage());
                    bullets.removeIndex(i);
                    bulletHit = true;
                    Gdx.app.log("HP", "current mob HP: " + mob.getCurrentHP());

                    break; // Exit the inner loop since the bullet can only hit one mob
                }
            }

            // If the bullet didn't hit any living mob within attacking radius, check for collision with all mobs
            if (!bulletHit) {
                for (Mob mob : allMobs) {
                    if (mob.getState() != Mob.State.DEAD && bullet.getBoundingRectangle().overlaps(mob.getBoundingRectangle())) {
                        mob.takeDamage(bullet.getDamage());
                        bullets.removeIndex(i);
                        Gdx.app.log("HP", "current mob HP: " + mob.getCurrentHP());
                        break; // Exit the loop since the bullet can only hit one mob
                    }
                }
            }

            // Check for collision with boundaries or off-screen
            if (isColliding_b(bullet.getPosition().x, bullet.getPosition().y - 4) || isBulletOffScreen(bullet)) {
                bullets.removeIndex(i);
            }
        }


        // Increment the stateTime for animation
        stateTime += delta;
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
        float bulletSpeed = 400;

        float velocityX = 0;
        float velocityY = 0;

        if (!isLeft) {
            velocityX = bulletSpeed;
        } else {
            velocityX = -bulletSpeed;
        }

        Bullet bullet;

        // getPosition().y + 10 because the bullet needs to be fired from the arm of the character.

        // t set damage thành 2
        if (!isLeft) {
            bullet = new Bullet(getPosition().x + 24, getPosition().y + 10, velocityX, velocityY, false, gun.getAngle(), 3);
        } else {
            bullet = new Bullet(getPosition().x - 4, getPosition().y + 10, velocityX, velocityY, true, gun.getAngle(), 3);
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
            case WALKING:
                region = charWalk.getKeyFrame(stateTime, true);
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
                currentState = State.WALKING;
                this.isLeft = true;
                break;
            case "RIGHT":
                // Code to set the character to move right state
                this.previousState = currentState;
                currentState = State.WALKING;
                this.isLeft = false;
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
            bullet.render(spriteBatch);
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

