package dot.empire.ms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Load in all needed assets.
 */
public final class ScreenLoading extends Scene {

    private VisLabel lbl;
    private VisTable root;

    public ScreenLoading() {
        this.root = new VisTable(true);
        this.root.setFillParent(true);
        this.root.align(Align.center);

        this.lbl = new VisLabel();
        this.lbl.setColor(Color.BLACK);
    }

    @Override
    public void show() {
        super.show();
        this.root.add(lbl);
        getEngine().getStage().addActor(root);
    }

    @Override
    public void update(float dt) {
        final AssetManager mngr = getEngine().getAssetManager();
        if (mngr.update()) {
            getEngine().setScreen(new ScreenGame());
        }
        String progress = String.format("Loading: %.2f", mngr.getProgress() * 100) + "%";
        this.lbl.setText(progress);
        Gdx.app.log(Minesweeper.TAG, progress);
    }

    @Override
    public void hide() {
        this.lbl.remove();
        super.hide();
    }
}
