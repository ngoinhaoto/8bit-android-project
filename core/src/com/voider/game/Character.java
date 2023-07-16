package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Character extends Sprite {
    //Stat
    private final int maxHP = 5;
    private final int maxARM = 3;
    private int currentHP;
    private int currentARM;
    private static final float FRAME_TIME = 0.18f;
    public enum State { IDLING, WALKING, DEAD }
    private float attackTimer;
    private static final float ATTACK_DURATION = 0.2f;
    public State currentState;
    public State previousState;
    public TextureAtlas textureAtlas;
    public Vector2 position;
    private Animation<TextureRegion> charIdle;
    private Animation<TextureRegion> charWalk;
    private Animation<TextureRegion> charDie;
    private float stateTime;
    public float speed;
    private boolean isLeft = false;
    private boolean isBeingAttacked;
    private Weapon gun;

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    private TileMap tileMap;
    private Array<Bullet> bullets;

    private Array<Mob> mobsInRange; // List to store mobs within attacking radius

    private float armorRegenTimer; // Time in seconds for each armor regeneration tick
    private static final float ARMOR_REGEN_TICK = 1f; // Time in seconds for each armor regeneration tick
    private float lastDamageTime;
    private static final float DAMAGE_COOLDOWN = 5f; // Time in seconds to wait before armor regeneration
    private float timer;
    private static final int TIMER_INCREMENT = 1; // Timer increment in seconds
    public int mobsKilled;

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
    }


    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    private Array<Sound> shootingSounds;

    private Sound takingDamageSound;

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
        charDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));

        charIdle.setFrameDuration(FRAME_TIME);
        charWalk.setFrameDuration(FRAME_TIME);
        charDie.setFrameDuration(FRAME_TIME);

        this.tileMap = tileMap;


        shootingSounds = new Array<>();
        shootingSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/shootsound.wav")));
        shootingSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/laserShoot.wav")));
        shootingSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/laserShoot1.wav")));
        shootingSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/laserShoot2.wav")));


        takingDamageSound =  Gdx.audio.newSound(Gdx.files.internal("music/oof sound.mp3"));


        bullets = new Array<>();
        mobsInRange = new Array<>();
        armorRegenTimer = ARMOR_REGEN_TICK; // Initialize the armor regeneration timer
        timer = 0;
        mobsKilled = 0;
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
        timer += delta;

        // Check if the character is dead
        if (currentState == State.DEAD) {
            // Character is dead, do not update movement or shooting
            return;
        }

        timer += delta;

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
        this.gun.updateGunAim(mobsInRange);
        // Update the gun
        this.gun.update(delta, allMobs);

        // Increment the stateTime for animation
        stateTime += delta;

        // Regenerate armor points if enough time has passed since the last damage
        if (currentARM < maxARM && currentHP > 0) {
            lastDamageTime += delta;

            if (lastDamageTime >= DAMAGE_COOLDOWN) {
                armorRegenTimer -= delta;

                if (armorRegenTimer <= 0) {
                    regenerateArmor();
                    armorRegenTimer = ARMOR_REGEN_TICK;
                }
            }
        }

        // Check if any mobs have died
        for (Mob mob : allMobs) {
            if (mob.getState() == Mob.State.DEAD) {
                // Handle any additional logic or effects when a mob is killed
            }
        }
    }

    public int getMobsKilled() {
        return mobsKilled;
    }
    private void regenerateArmor() {
        if (currentARM < maxARM) {
            currentARM+=1;
        }
    }

    public void shoot() {
        //Change state of Gun

        if (currentState == State.DEAD) {
            // Character is dead, cannot shoot
            return;
        }

        this.gun.setState();
        this.gun.shoot();
        int soundIndex = MathUtils.random(shootingSounds.size - 1); // Select a random shooting sound
        Sound selectedSound = shootingSounds.get(soundIndex);
        selectedSound.play();
    }

    public TileMap getTileMap() {
        return tileMap;
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
            case DEAD:
                region = charDie.getKeyFrames()[0]; // Use the first (and only)
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
            case "DIE":
                this.previousState = currentState;
                // Code to set the character to dead state
                currentState = State.DEAD;
                break;
        }
    }


    public void render(SpriteBatch spriteBatch) {
        if (currentState != State.DEAD) {
            // Check if the Mob is being attacked and modify the color accordingly
            if (isBeingAttacked) {
                // Set the color to red
                spriteBatch.setColor(Color.RED);
            }
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
            // Update the attack timer and check if it's expired
            if (isBeingAttacked) {
                attackTimer -= Gdx.graphics.getDeltaTime();
                if (attackTimer <= 0) {
                    isBeingAttacked = false;
                }
            }
            // Render the gun
            gun.setPosition(position.x, position.y); // Set the gun position same as the character's position
            gun.render(spriteBatch);
            spriteBatch.setColor(Color.WHITE);

        } else {
            // Character is dead, do not reset the state
            TextureRegion currentFrame = charDie.getKeyFrames()[0];
            float textureWidth = 32; // Set the desired texture width
            float textureHeight = 32; // Set the desired texture height
            if (isLeft) {
                spriteBatch.draw(currentFrame, position.x + textureWidth, position.y,
                        -textureWidth, textureHeight);
            } else {
                spriteBatch.draw(currentFrame, position.x, position.y, textureWidth, textureHeight);
            }
        }
    }

    public void takeDamage(int damage) {


        isBeingAttacked = true;

        takingDamageSound.play();
        attackTimer = ATTACK_DURATION;
        if (this.currentARM > 0) {
            this.currentARM -= damage;
            if (currentARM < 0) {
                currentARM = 0;
            }
        } else {
            // If there is no armor, apply the damage directly to HP
            this.currentHP -= damage;
        }

        if (this.currentHP <= 0) {
            this.currentHP = 0; // Ensure HP is never negative
            this.setState("DIE"); // Set the state to "DEAD" if HP reaches 0
        }

        // Update the lastDamageTime of the character
        this.setLastDamageTime(0);
    }


    public Rectangle getBoundingRectangle() {
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        return new Rectangle(x, y, width + 25, height + 25);
    }

    public Rectangle getBeingAttackedRectangle() {
        float x = getPosition().x;
        float y = getPosition().y;
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());

        float width = currentFrame.getRegionWidth();
        float height = currentFrame.getRegionHeight();

        return new Rectangle(x, y, width - 20, height - 20 );
    }

    public Rectangle getCollisionRectangle() {
        float x = getPosition().x;
        float y = getPosition().y;

        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());

        float width = currentFrame.getRegionWidth();
        float height = currentFrame.getRegionHeight();

        return new Rectangle(x , y, width - 10 , height - 10);
    }
    // Set isLeft based the weapon
    public void updateWeaponIsLeft(boolean isLeft) {
        this.isLeft = isLeft;
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
    public void setLastDamageTime(float time) {
        this.lastDamageTime = time;
    }
    public float getLastDamageTime() {
        return lastDamageTime;
    }

    public Array<Mob> getMobsInRange() {
        return this.mobsInRange;
    }

    public void dispose() {
        // Dispose of any resources here if needed
        gun.dispose();
        for (Sound sound : shootingSounds) {
            sound.dispose();
        }
        takingDamageSound.dispose();
    }
}

