package dot.empire.ms;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;

/**
 * Base Engine class.
 *
 * @author Matthew 'siD' Van der Bijl
 */
public final class Minesweeper extends ApplicationAdapter implements Disposable {

    /**
     * Tag for logging.
     */
    public static final String TAG = "Minesweeper-GDX";

    private Stage stage;

    @Override
    public void create() {
        Gdx.gl.glClearColor(1, 1, 1, 1);

        VisUI.load();
        this.stage = new Stage();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final float dt = Gdx.graphics.getDeltaTime();
        this.stage.act(dt);

        this.stage.draw();
    }
}
