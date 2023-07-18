package com.voider.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.voider.game.Voider;

public class GameOverScreen implements Screen {
    private Voider game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;


    public GameOverScreen(Voider game) {
        this.game = game;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        // GAME OVER LABEL
        Texture gameOverTexture = new Texture(Gdx.files.internal("skin/game_over.png"));
        Image gameOverImg = new Image(gameOverTexture);

        // Play Again Button
        BitmapFont font = new BitmapFont();
        float fontSize = 48f; // Set the desired font size
        font.getData().setScale(fontSize / font.getLineHeight()); // Scale the font size
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.font.getData().setScale(2f); // Increase the font scale

        TextButton returnHomeButton = new TextButton("RETURN HOME", buttonStyle);
        TextButton playAgainButton = new TextButton("PLAY AGAIN", buttonStyle);
        returnHomeButton.getLabel().setFontScale(3f);
        playAgainButton.getLabel().setFontScale(3f); // Increase the font scale

        returnHomeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("RHC", "WORKED");
                returnHomeScreen();
            }
        });

        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("GOC", "WORKED");
                playAgain();
            }
        });

        table.add(gameOverImg);
        table.row();
        table.add(returnHomeButton).width(200).height(60);
        table.row();
        table.add().height(40); // Add space between the buttons
        table.row();
        table.add(playAgainButton).width(200).height(60);
        table.setFillParent(true);

        stage.addActor(table);
    }

    private void returnHomeScreen() {
        game.setScreen(new MenuScreen(this.game));
    }

    private void playAgain() {
        game.setScreen(new FirstLevelScreen(this.game));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Draw any background or game over graphics here if needed
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}
