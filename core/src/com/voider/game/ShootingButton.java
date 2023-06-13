package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ShootingButton extends ImageButton {
    private Character character;
    public ShootingButton(final Character character) {
        super(createButtonStyle());
        setPosition(Gdx.graphics.getWidth() - getWidth() - 150, 150); // Adjust the position as needed

        this.character = character;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                character.shoot();
            }
        });
    }

    private static ImageButtonStyle createButtonStyle() {
        Texture buttonUp = new Texture("shootingbutton/shoot-button-up.png");
        Texture buttonDown = new Texture("shootingbutton/shoot-button-down.png");

        ImageButtonStyle buttonStyle = new ImageButtonStyle();
        buttonStyle.up = buttonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(buttonUp));
        buttonStyle.down = buttonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(buttonDown));

        return buttonStyle;
    }
}
