package com.voider.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.voider.game.Voider;

public class GameOverScreen implements Screen {
    private Voider game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private ImageButton replayButton;
    private ImageButton homeButton;


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

        Texture homeButtonTextureUp = new Texture(Gdx.files.internal("skin/homebuttonup.png"));
        Texture homeButtonTextureDown = new Texture(Gdx.files.internal("skin/homebuttondown.png"));

        TextureRegionDrawable homeButtonUp = new TextureRegionDrawable(new TextureRegion(homeButtonTextureUp));
        TextureRegionDrawable homeButtonDown = new TextureRegionDrawable(new TextureRegion(homeButtonTextureDown));

        ImageButtonStyle homeButtonStyle = new ImageButtonStyle();
        homeButtonStyle.imageUp = homeButtonUp;
        homeButtonStyle.imageDown = homeButtonDown;

        homeButton = new ImageButton(homeButtonStyle);


        Texture replayButtonTextureUp = new Texture(Gdx.files.internal("skin/replaybuttonup.png"));
        Texture replayButtonTextureDown = new Texture(Gdx.files.internal("skin/replaybuttondown.png"));

        TextureRegionDrawable replayButtonUp = new TextureRegionDrawable(new TextureRegion(replayButtonTextureUp));
        TextureRegionDrawable replayButtonDown = new TextureRegionDrawable(new TextureRegion(replayButtonTextureDown));

        ImageButtonStyle replayButtonStyle = new ImageButtonStyle();
        replayButtonStyle.imageUp = replayButtonUp;
        replayButtonStyle.imageDown = replayButtonDown;


        replayButton = new ImageButton(replayButtonStyle);

        table.add(gameOverImg);
        table.row();
        table.add(homeButton).width(homeButton.getWidth()).height(homeButton.getHeight());
        table.row();
        table.add().height(40); // Add space between the buttons
        table.row();
        table.add(replayButton).width(replayButton.getWidth()).height(replayButton.getHeight());
        table.setFillParent(true);

        stage.addActor(table);

        replayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playAgain();
            }
        });
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnHomeScreen();
            }
        });

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
