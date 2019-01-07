package dot.empire.ms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Disposable;

/**
 * Renderable game screen.
 */
public abstract class Scene extends ScreenAdapter implements Disposable {

    private Minesweeper engine;

    public Scene() {
    }

    @Override
    public void show() {
        Gdx.app.log(Minesweeper.TAG, "Showing " + getName());
    }

    /**
     * @param dt Delta time
     */
    @Override
    public final void render(float dt) {
        this.update(dt);
        this.render();
    }

    /**
     * @param dt Delta time
     */
    @SuppressWarnings("EmptyMethod")
    public void update(float dt) {
    }

    @SuppressWarnings("EmptyMethod")
    public void render() {
    }

    @Override
    public void hide() {
        this.dispose();
    }

    /**
     * @return parent engine
     */
    public Minesweeper getEngine() {
        return this.engine;
    }

    /**
     * @param engine parent engine
     */
    public void setEngine(Minesweeper engine) {
        this.engine = engine;
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}
