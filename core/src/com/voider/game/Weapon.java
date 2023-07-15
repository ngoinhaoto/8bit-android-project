package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Weapon {
    private Texture texture; // The texture representing the weapon
    private Vector2 position; // The position of the weapon
    private float angle; // The rotation angle of the weapon
    private boolean isShoot = false;
    private float shootDuration = 0.1f; // Duration in seconds for which isShoot will be true
    private float shootTimer = 0.0f; // Timer to track the elapsed time
    private boolean isLeft = false;
    private Character character;
    private Array<Bullet> bullets;

    private Array<Sound> hitWallSounds;
    private Array<Sound> hitMobSounds;

    public Weapon(Texture texture, Character character) {
        this.texture = texture;
        this.position = new Vector2();
        this.angle = 0;
        this.character = character;
        this.bullets = new Array<>();
        hitWallSounds = new Array<>();
        hitMobSounds = new Array<>();

        hitWallSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitWall2.wav")));
        hitWallSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitWall3.wav")));
        hitWallSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitWall4.wav")));

        hitMobSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitMob.wav")));
        hitMobSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitMob1.wav")));
        hitMobSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitMob2.wav")));
        hitMobSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/hitMob3.wav")));
    }

    public void setState() {
        if (isShoot) {
            // If already shooting, reset the timer
        } else {
            // Start shooting by setting isShoot to true
            isShoot = true;
        }
        shootTimer = 0.0f;
    }

    public void shoot() {
        // Create a new bullet,
        float bulletSpeed = 400;

        float velocityX;
        float velocityY = 0;

        if (!isLeft) {
            velocityX = bulletSpeed;
        } else {
            velocityX = -bulletSpeed;
        }

        Bullet bullet;

        // Set the bullet's position, velocity, angle, damage, and other properties as needed

        if (!isLeft) {
            bullet = new Bullet(this.getPosition().x + 22, this.getPosition().y + 9, velocityX, velocityY, false, getAngle(), 2, "bullet/bullet 5.png", bulletSpeed);
        } else {
            bullet = new Bullet(this.getPosition().x + 2, this.getPosition().y + 9, velocityX, velocityY, true, getAngle(), 2, "bullet/bullet 5.png", bulletSpeed);
        }

        // Add the bullet to the bullets array
        bullets.add(bullet);
    }


    private void updateTexture() {
        if (isShoot) {
            this.texture = new Texture("weap/gun/gun fire.png");
        } else {
            this.texture = new Texture("weap/gun/gun active.png");
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setAngle(float angle) {
        this.angle = angle;
        if (angle < -90 || angle > 90) {
            this.isLeft = true;
            character.updateWeaponIsLeft(true); // Update the isLeft variable in Character
            if(this.character.currentState != Character.State.IDLING) {
                this.character.setState("LEFT");
            }
        } else {
            this.isLeft = false;
            character.updateWeaponIsLeft(false); // Update the isLeft variable in Character
            if(this.character.currentState != Character.State.IDLING) {
                this.character.setState("RIGHT");
            }
        }
    }
    public float getAngle() {
        return this.angle;
    }
    public void render(SpriteBatch spriteBatch) {
        updateTexture();
        if (!isLeft) {
            spriteBatch.draw(
                    texture,
                    position.x + 22,
                    position.y + 9,
                    texture.getWidth() / 2,
                    texture.getHeight() / 2,
                    texture.getWidth(),
                    texture.getHeight(),
                    1.0f,
                    1.0f,
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
                    position.x + 2,
                    position.y + 9,
                    texture.getWidth() / 2,
                    texture.getHeight() / 2,
                    texture.getWidth(),
                    texture.getHeight(),
                    1.0f,
                    1.0f,
                    angle,
                    0,
                    0,
                    texture.getWidth(),
                    texture.getHeight(),
                    false,
                    true
            );
        }
        // Render the bullets
        for (Bullet bullet : bullets) {
            bullet.render(spriteBatch);
        }
    }

    public void update(float deltaTime, Array<Mob> allMobs) {
        if (isShoot) {
            shootTimer += deltaTime;
            if (shootTimer >= shootDuration) {
                // Shooting duration has elapsed, switch isShoot back to false
                isShoot = false;
            }
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);

            // Check for collision with mobs within attacking radius
            boolean bulletHit = false;
            for (Mob mob : this.character.getMobsInRange()) {
                if (mob.getState() != Mob.State.DEAD && bullet.getBoundingRectangle().overlaps(mob.getBoundingRectangle())) {
                    mob.takeDamage(bullet.getDamage());
                    int soundIndex = MathUtils.random(hitMobSounds.size - 1); // Select a random shooting sound
                    Sound selectedSound = hitMobSounds.get(soundIndex);
                    selectedSound.play();

                    bullets.removeIndex(i);
                    bulletHit = true;
//                    Gdx.app.log("HP", "current mob HP: " + mob.getCurrentHP());
                    break; // Exit the inner loop since the bullet can only hit one mob
                }
            }

            // If the bullet didn't hit any living mob within attacking radius, check for collision with all mobs
            if (!bulletHit) {
                for (Mob mob : allMobs) {
                    if (mob.getState() != Mob.State.DEAD && bullet.getBoundingRectangle().overlaps(mob.getBoundingRectangle())) {
                        mob.takeDamage(bullet.getDamage());
                        int soundIndex = MathUtils.random(hitMobSounds.size - 1); // Select a random shooting sound
                        Sound selectedSound = hitMobSounds.get(soundIndex);
                        selectedSound.play();
                        bullets.removeIndex(i);
//                        Gdx.app.log("HP", "current mob HP: " + mob.getCurrentHP());
                        break; // Exit the loop since the bullet can only hit one mob
                    }
                }
            }

            // Check for collision with boundaries or off-screen
            if (isColliding_b(bullet.getPosition().x, bullet.getPosition().y - 4) || isBulletOffScreen(bullet)) {
                if (!bullets.isEmpty() && i < bullets.size) {

                    int soundIndex = MathUtils.random(hitWallSounds.size - 1);
                    Sound selectedSound = hitWallSounds.get(soundIndex);
                    selectedSound.play();
                    bullets.removeIndex(i);
                }
            }
        }
    }

    public boolean isColliding_b(float x, float y) {
        int tileXStart = (int) (x / this.character.getTileMap().getTileWidth());
        int tileXEnd = (int) ((x) / this.character.getTileMap().getTileWidth());
        int tileYStart = (int) (y / this.character.getTileMap().getTileHeight());
        int tileYEnd = (int) ((y+16) / this.character.getTileMap().getTileHeight());

        // Check collisions for each corner of the character
        boolean topLeft = this.character.getTileMap().isBoundary(tileXStart, tileYEnd);
        boolean topRight = this.character.getTileMap().isBoundary(tileXEnd, tileYEnd);
        boolean bottomLeft = this.character.getTileMap().isBoundary(tileXStart, tileYStart);
        boolean bottomRight = this.character.getTileMap().isBoundary(tileXEnd, tileYStart);

        // Return true if any corner collides with a boundary tile
        return topLeft || topRight || bottomLeft || bottomRight;
    }

    private boolean isBulletOffScreen(Bullet bullet) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float bulletWidth = bullet.getBulletTexture().getWidth();
        float bulletHeight = bullet.getBulletTexture().getHeight();

        float bulletX = bullet.getPosition().x;
        float bulletY = bullet.getPosition().y;

        return bulletX < -bulletWidth || bulletX > screenWidth || bulletY < -bulletHeight || bulletY > screenHeight;
    }
    public void updateGunAim(Array<Mob> mobsInRange) {
        if (mobsInRange.isEmpty()) {
            // If there are no mobs in range, reset the gun's aim
            return;
        }

        // Find the nearest mob
        Mob nearestMob = findNearestMob(mobsInRange);

        // Check if a living mob is found
        if (nearestMob == null) {
            // If no living mob is found, reset the gun's aim
            return;
        }

        // Get the position of the weapon and the nearest mob
        Vector2 weaponPosition = getPosition();
        Vector2 mobPosition = nearestMob.getPosition();

        // Calculate the direction vector from weapon to mob
        Vector2 direction = mobPosition.cpy().sub(weaponPosition);

        // Calculate the angle using atan2
        float angle = MathUtils.atan2(direction.y, direction.x);

        // Convert the angle to degrees
        angle = MathUtils.radiansToDegrees * angle;

        // Set the angle for the weapon
        this.setAngle(angle);
    }

    private Mob findNearestMob(Array<Mob> mobsInRange) {
        Mob nearestMob = null;
        float nearestDistance = Float.MAX_VALUE;
        Vector2 weaponPosition = new Vector2(getPosition().x, getPosition().y);

        for (Mob mob : mobsInRange) {
            if (mob.getState() != Mob.State.DEAD) {
                Vector2 mobPosition = new Vector2(mob.getX(), mob.getY());
                float distance = weaponPosition.dst(mobPosition);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestMob = mob;
                }
            }
        }

        return nearestMob;
    }

    public Vector2 getPosition() {
        return position;
    }
    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void dispose() {
        texture.dispose();

        for (Sound sound : hitWallSounds) {
            sound.dispose();
        }
        for (Sound sound : hitMobSounds) {
            sound.dispose();
        }
    }
}
