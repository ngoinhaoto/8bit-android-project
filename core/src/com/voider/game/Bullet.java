package com.voider.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

    private Vector2 position;
    private Vector2 velocity;

    private TextureRegion bulletTexture;

    private float bulletSpeed = 300; // Declare bullet speed
    // constructor
// constructor
    public Bullet(float x, float y, float velocityX, float velocityY) {
        position = new Vector2(x, y);
        velocity = new Vector2(velocityX, velocityY).nor().scl(bulletSpeed);
        bulletTexture = new TextureRegion(new Texture("bullet/bullet 5.png"));
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
        batch.draw(bulletTexture, position.x, position.y);
    }

    public TextureRegion getBulletTexture() {
        return bulletTexture;
    }

}
