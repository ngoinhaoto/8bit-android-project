package com.voider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {

    final Voider game;

    OrthographicCamera camera;
    public MenuScreen(final Voider game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 600);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1); //color of screen

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Voider's game", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin", 100, 100);
        game.batch.end();

        // if player touch the screen, go on to next screen and dispose current screen(which is mainmenuscreen)

        if (Gdx.input.isTouched()) {
            game.setScreen(new FirstLevelScreen(game));
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        dispose();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
