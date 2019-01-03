package dot.empire.ms;


import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Playing field.
 */
public class GameScreen extends Screen {

    private Stage stage;

    public GameScreen() {
        this.stage = new Stage();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
