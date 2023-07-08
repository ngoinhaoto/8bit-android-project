package com.voider.game.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.voider.game.Character;

public class HUD extends Actor {
    private Character character;
    private Sprite hpBar;
    private Sprite armorBar;

    public HUD(Character character) {
        this.character = character;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update the bar based on the character's current HP and Armor values
        TextureAtlas atlasHP = new TextureAtlas("hud/HP Bar/HP_Bar.atlas");
        TextureAtlas atlasARM = new TextureAtlas("hud/Armor Bar/Arm_Bar.atlas");

        Gdx.app.log("HP", loadCurrentHP());
        Gdx.app.log("ARM", loadCurrentARM());

        hpBar = new Sprite(atlasHP.findRegion("hpBar-"+loadCurrentHP()));
        armorBar = new Sprite(atlasARM.findRegion("armBar-"+loadCurrentARM()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        TextureAtlas atlasHP = new TextureAtlas("hud/HP Bar/HP_Bar.atlas");
        TextureAtlas atlasARM = new TextureAtlas("hud/Armor Bar/Arm_Bar.atlas");


        hpBar.setPosition(40, Gdx.graphics.getHeight() - 30 - hpBar.getHeight()); // Top left corner
        armorBar.setPosition(40, Gdx.graphics.getHeight() - 80 - armorBar.getHeight()); // Below the HP bar

        // Draw the bars
        hpBar.draw(batch);
        armorBar.draw(batch);
    }

    private String loadCurrentHP() {
        return  String.valueOf((int)((double)this.character.getCurrentHP() / this.character.getMaxHP() *100));
    }
    private String loadCurrentARM() {
        return  String.valueOf((int)(Math.round((float)this.character.getCurrentARM() / this.character.getMaxARM() *100)));
    }


}
