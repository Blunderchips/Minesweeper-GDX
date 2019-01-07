package dot.empire.ms;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

import javax.swing.*;

/**
 * Base Engine class for Minesweeper-GDX.
 *
 * @author Matthew 'siD' Van der Bijl
 */
public final class Minesweeper extends Game implements Disposable {

    /**
     * Width of the game window.
     */
    public static final int WIDTH = 1024;
    /**
     * Height of the game window.
     */
    public static final int HEIGHT = 960;

    /**
     * Number of mines in game.
     */
    public static final int NUM_MINES = Math.min(Integer.parseInt(JOptionPane.showInputDialog("How many mines are there?")), WIDTH * HEIGHT);

    /**
     * Tag for logging.
     */
    public static final String TAG = "Minesweeper-GDX";

    private AnnotationAssetManager assetMngr;
    /**
     * UI Layer.
     */
    private Stage stage;

    /**
     * Called on start up.
     */
    @Override
    public void create() {
        Gdx.gl.glClearColor(1, 1, 1, 1); // white
        // Gdx.app.setLogLevel(Application.LOG_DEBUG);

        VisUI.load();
        this.assetMngr = new AnnotationAssetManager();
        this.assetMngr.load("gfx/blank.png", Texture.class);
        this.assetMngr.load("gfx/unlit-bomb.png", Texture.class);
        this.assetMngr.load("gfx/golf-flag.png", Texture.class);
        for (int i = 0; i < 10; i++) {
            this.assetMngr.load("gfx/img_num_" + i + ".png", Texture.class);
        }

        Gdx.input.setInputProcessor(new InputMultiplexer());
        this.stage = new Stage();
        getInputMultiplexer().addProcessor(stage);

        this.setScreen(new ScreenLoading());
    }

    @Override
    public void render() {
        this.stage.act();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.draw();
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        super.dispose();
    }

    public void setScreen(final Scene screen) {
        screen.setEngine(this);
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run() {
                Minesweeper.super.setScreen(screen);
            }
        });
    }

    @Override
    @Deprecated
    public void setScreen(Screen screen) {
        super.setScreen(screen);
    }

    public AnnotationAssetManager getAssetManager() {
        return this.assetMngr;
    }

    public InputMultiplexer getInputMultiplexer() {
        return (InputMultiplexer) Gdx.input.getInputProcessor();
    }

    /**
     * @return UI Layer
     */
    public Stage getStage() {
        return this.stage;
    }
}
