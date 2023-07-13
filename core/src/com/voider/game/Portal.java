package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Portal extends Actor {
    private Animation<TextureRegion> animation;
    private static final float FRAME_TIME = 0.18f;
    private float stateTime;

    public Portal() {
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("portal/portal.atlas"));
        animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("portal"));
    }


    public float getWidth() {
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());

        return currentFrame.getRegionWidth();
    }

    public float getHeight() {
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());

        return currentFrame.getRegionHeight();
    }


    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());
        spriteBatch.draw(currentFrame, getX(), getY());
    }


    public Rectangle getCollisionRectangle() {
        float x = getX();
        float y = getY();

        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());
        float width = currentFrame.getRegionWidth();
        float height = currentFrame.getRegionHeight();

        return new Rectangle(x + 30 , y, width - 30, height - 30);

    }


    public void dispose() {

    }

    private TextureRegion getFrame(float deltaTime) {
        return animation.getKeyFrame(stateTime, true);
    }
}

