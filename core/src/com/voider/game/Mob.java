package com.voider.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Mob extends Actor {
    private TextureRegion[] sprites;
    private String mobType;

    public Mob(TextureRegion[] sprites, String mobType) {
        this.sprites = sprites;
        this.mobType = mobType;
        setSize(sprites[0].getRegionWidth(), sprites[0].getRegionHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Update the mob's behavior and attributes
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        TextureRegion sprite = sprites[0];  // Default sprite

        // Retrieve the appropriate sprite based on mobType
        if (mobType.equals("Type1")) {
            sprite = sprites[1];
        } else if (mobType.equals("Type2")) {
            sprite = sprites[2];
        }
        // Add more conditions for other mob types as needed

        batch.setColor(1, 1, 1, parentAlpha);  // Set the batch color with parent alpha
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
        // Render the mob's sprite at its current position
    }

}