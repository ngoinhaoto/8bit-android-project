package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Portal extends Actor {
    private Animation<TextureRegion> animation;
    private static final float FRAME_TIME = 0.18f;
    private float stateTime;

    public Portal() {
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("portal/portal.atlas"));
        animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("portal"));
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());
        spriteBatch.draw(currentFrame, getX(), getY());
    }

    public void dispose() {

    }

    private TextureRegion getFrame(float deltaTime) {
        return animation.getKeyFrame(stateTime, true);
    }
}

