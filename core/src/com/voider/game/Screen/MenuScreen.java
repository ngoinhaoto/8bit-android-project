package com.voider.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.voider.game.Character;
import com.voider.game.Voider;

import javax.swing.plaf.TextUI;

public class MenuScreen implements Screen {

    private Voider game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private ImageButton playButton;

    private Label introductionLabel;
    private Label descriptionLabel;
    private Music backgroundMusic;

    private Sound playButtonSound;
    public MenuScreen(final Voider game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        stage = new Stage(new ScreenViewport());

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Menu/02 fat cat.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        playButtonSound = Gdx.audio.newSound(Gdx.files.internal("music/playbuttonsound.wav"));

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);

        //background image

        Texture backgroundTexture = new Texture(Gdx.files.internal("menubackground.jpg"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);


        //play button

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("skin/pixeboy-font/Pixeboy-z8XGD.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 50;

        BitmapFont customFont = fontGenerator.generateFont(fontParameter);

        Texture playButtonTextureUp = new Texture(Gdx.files.internal("skin/playbutton.png"));
        Texture playButtonTextureDown = new Texture(Gdx.files.internal("skin/playbuttondown.png"));

        TextureRegionDrawable playButtonUp = new TextureRegionDrawable(new TextureRegion(playButtonTextureUp));
        final TextureRegionDrawable playButtonDown = new TextureRegionDrawable(new TextureRegion(playButtonTextureDown));

        ImageButtonStyle playButtonStyle = new ImageButtonStyle();
        playButtonStyle.imageUp = playButtonUp;
        playButtonStyle.imageDown = playButtonDown;

        playButton = new ImageButton(playButtonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playButtonSound.play();
                game.setScreen(new FirstLevelScreen(game));
            }
        });


        Label.LabelStyle introductionLabelStyle = new Label.LabelStyle();
        introductionLabelStyle.font = customFont;
        introductionLabelStyle.fontColor = Color.valueOf("#C8E2AA");
        Label.LabelStyle descriptionLabelStyle = new Label.LabelStyle();
        descriptionLabelStyle.font = customFont;
        descriptionLabelStyle.fontColor = Color.valueOf("#bae100");

        introductionLabel = new Label("Welcome to Voider game", introductionLabelStyle);
        introductionLabel.setFontScale(3.5f);

        descriptionLabel = new Label("Your task is killing all the mobs to get the the portal to escape the dungeons", descriptionLabelStyle);
        descriptionLabel.setFontScale(1.5f);

        stage.addActor(backgroundImage);

        table.row();
        table.add(playButton).width(playButton.getWidth()).height(playButton.getHeight());
        table.row();
        table.add(introductionLabel);
        table.row();
        table.add().height(50);
        table.row();
        table.add(descriptionLabel);
        stage.addActor(table);
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
        stage.getViewport().update(width,height);
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
        backgroundMusic.stop();
        backgroundMusic.dispose();
        playButtonSound.stop();
        playButtonSound.dispose();

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        backgroundMusic.dispose();
        playButtonSound.dispose();
    }
}
