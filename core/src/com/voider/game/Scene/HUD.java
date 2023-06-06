package com.voider.game.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.voider.game.Voider;

public class HUD {
    public Stage stage;
    public Viewport viewport;

    Label characterLabel;

    public HUD(SpriteBatch sb) {
        viewport =new FillViewport(Voider.V_WIDTH, Voider.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
    }
}
