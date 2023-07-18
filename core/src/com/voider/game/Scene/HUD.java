package com.voider.game.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.voider.game.Character;

public class HUD extends Actor {
    private Character character;
    private Sprite hpBar;
    private Sprite armorBar;

    private BitmapFont font;

    private BitmapFont timerFont;
    private TextureAtlas atlasARM = new TextureAtlas("hud/Armor Bar/Arm_Bar.atlas");
    private TextureAtlas atlasHP = new TextureAtlas("hud/HP Bar/HP_Bar.atlas");

    public HUD(Character character) {
        this.character = character;
        // load the font

        font = new BitmapFont();

        Color color = new Color(0, 255, 175, 1);

        font.setColor(color);
        font.getData().setScale(3f);

        timerFont = new BitmapFont();


        Color timerColor = new Color(255, 143, 0, 1);

        timerFont.setColor(timerColor);
        timerFont.getData().setScale(3f);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Dispose old sprites to release resources
        if (hpBar != null) {
            hpBar.getTexture().dispose();
        }
        if (armorBar != null) {
            armorBar.getTexture().dispose();
        }

        // Update the bar based on the character's current HP and Armor values
        hpBar = new Sprite(atlasHP.findRegion("hpBar-"+loadCurrentHP()));
        armorBar = new Sprite(atlasARM.findRegion("armBar-"+loadCurrentARM()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        hpBar.setPosition(40, Gdx.graphics.getHeight() - 30 - hpBar.getHeight()); // Top left corner
        armorBar.setPosition(40, Gdx.graphics.getHeight() - 80 - armorBar.getHeight()); // Below the HP bar

        // Draw the bars
        hpBar.draw(batch);
        armorBar.draw(batch);

        // draw number of mobs killed
        String mobsKilledText = "Mobs Killed: " + character.getMobsKilled();
        font.draw(batch, mobsKilledText, Gdx.graphics.getWidth() - 700, Gdx.graphics.getHeight() - 50);


        String timerText = "Time: " + String.format("%.2f", character.getTimer());

        timerFont.draw(batch, timerText, Gdx.graphics.getWidth() - 320, Gdx.graphics.getHeight() - 50);
    }

    private String loadCurrentHP() {
        return  String.valueOf((int)((double)this.character.getCurrentHP() / this.character.getMaxHP() *100));
    }
    private String loadCurrentARM() {
        return  String.valueOf((int)(Math.round((float)this.character.getCurrentARM() / this.character.getMaxARM() *100)));
    }

}

