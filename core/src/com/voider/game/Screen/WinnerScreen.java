package com.voider.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.voider.game.Character;
import com.voider.game.Voider;

public class WinnerScreen implements Screen {

    private Voider game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private float runtime;
    private int totalKilled;

    private ImageButton returnButton;
    private Label totalKilledLabel;
    private Label runtimeLabel;

    private Music backgroundMusic;

    WinnerScreen(Voider game, Character character) {

        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        stage = new Stage(new ScreenViewport());


        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/12 thanks.mp3")); // Load the background music
        backgroundMusic.setLooping(true); // Set the music to loop
        backgroundMusic.play(); // Start playing the background music


        totalKilled = character.getMobsKilled();
        runtime = character.getTimer();

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

// Background Image
        Texture backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

// Winner LABEL
        Texture gameOverTexture = new Texture(Gdx.files.internal("skin/victory.png"));
        Image gameOverImg = new Image(gameOverTexture);

// Return Home Button
        // Return Home Button
        Texture returnButtonTextureUp = new Texture(Gdx.files.internal("skin/return button.png"));
        Texture returnButtonTextureDown = new Texture(Gdx.files.internal("skin/return button down.png"));

        TextureRegionDrawable returnButtonUp = new TextureRegionDrawable(new TextureRegion(returnButtonTextureUp));
        TextureRegionDrawable returnButtonDown = new TextureRegionDrawable(new TextureRegion(returnButtonTextureDown)); // You can use a different texture for the down state if desired
        ImageButtonStyle returnButtonStyle = new ImageButtonStyle();
        returnButtonStyle.imageUp = returnButtonUp;
        returnButtonStyle.imageDown = returnButtonDown;
        returnButton = new ImageButton(returnButtonStyle);


        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("RHC", "WORKED");
                returnHomeScreen();
            }
        });

// Create the labels using the default skin

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("skin/pixeboy-font/Pixeboy-z8XGD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 50;

        BitmapFont customFont = fontGenerator.generateFont(fontParameter);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.valueOf("#fff893");
        labelStyle.font = customFont;

        totalKilledLabel = new Label("MOBS KILLED: " + totalKilled, labelStyle);
        totalKilledLabel.setFontScale(2f); // Increase the font scale

        runtimeLabel = new Label("TIME: " + runtime + " GAME SECONDS", labelStyle);
        runtimeLabel.setFontScale(2f); // Increase the font scale

        stage.addActor(backgroundImage);

        table.add(gameOverImg); // Adjust the padding value as needed
        table.row();
        table.add(returnButton).width(returnButton.getWidth()).height(returnButton.getHeight()); // Adjust the padding value as needed
        table.row();
        table.add(runtimeLabel);
        table.row();
        table.add(totalKilledLabel);
        table.row();
        stage.addActor(table);
    }

    private void returnHomeScreen() {
        game.setScreen(new MenuScreen(this.game));
    }

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
        backgroundMusic.stop();
        backgroundMusic.dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        backgroundMusic.dispose();
    }
}
