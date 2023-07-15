package com.voider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Bullet {

    private Vector2 position;
    private Vector2 velocity;

    public int damage;
    private boolean isLeft;
    private Texture bulletTexture;

    private float angle;
    private float bulletSpeed; // Declare bullet speed

    //added damage to constructor

    public Bullet(float x, float y, float velocityX, float velocityY, boolean left, float angle, int damage, String bulletPath, float bulletSpeed) {
        this.isLeft = left;
        position = new Vector2(x, y);
        this.angle = angle;
        this.damage = damage;
        this.bulletSpeed = bulletSpeed;
        float angleInRadians = (float) Math.toRadians(angle);
        velocity = new Vector2((float) Math.cos(angleInRadians), (float) Math.sin(angleInRadians)).nor().scl(bulletSpeed);
        bulletTexture = new Texture(bulletPath);
    }

    // Update the bullet's position
    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    // Getters
    public Vector2 getPosition() {
        return position;
    }

    public void render(SpriteBatch batch) {
        if (!isLeft) {
            batch.draw(
                    bulletTexture,
                    position.x,
                    position.y,
                    bulletTexture.getWidth() / 2,
                    bulletTexture.getHeight() / 2,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight(),
                    1.0f,
                    1.0f,
                    angle,
                    0,
                    0,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight(),
                    false,
                    false
            );
        } else {
            batch.draw(
                    bulletTexture,
                    position.x,
                    position.y+2,
                    bulletTexture.getWidth() / 2,
                    bulletTexture.getHeight() / 2,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight(),
                    1.0f,
                    1.0f,
                    angle,
                    0,
                    0,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight(),
                    false,
                    true
            );
        }
    }

    public void setAngle(float angle) {
        this.angle = angle;
        float angleInRadians = (float) Math.toRadians(angle);
        velocity.set((float) Math.cos(angleInRadians), (float) Math.sin(angleInRadians)).nor().scl(bulletSpeed);
    }

    public float getAngle() {
        return this.angle;
    }

    public Texture getBulletTexture() {
        return bulletTexture;
    }

    public int getDamage() {
        return damage;
    }

    public float getWidth() {
        return bulletTexture.getWidth();
    }

    public float getHeight() {
        return bulletTexture.getHeight();
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, getWidth() , getHeight());
    }
}
