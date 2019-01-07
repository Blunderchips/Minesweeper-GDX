package dot.empire.ms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;

import java.awt.*;

/**
 * Potential mine button.
 */
public class Mine extends VisImageButton {

    private Minesweeper engine;
    private ScreenGame parent;

    /**
     * Position of the button on the X-axis.
     */
    private int xPos;
    /**
     * Position of the button on the Y-axis.
     */
    private int yPos;
    private boolean isMine;

    /**
     * @param xPos   X-axis position
     * @param yPos   Y-axis position
     * @param parent parent engine
     */
    public Mine(int xPos, int yPos, ScreenGame parent) {
        // super(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("gfx/blank.png")))));
        super(new SpriteDrawable(new Sprite(parent.getEngine().getAssetManager().get("gfx/blank.png", Texture.class))));
        TableUtils.setSpacingDefaults(this); // Leaking

        this.parent = parent;
        this.engine = parent.getEngine();
        this.isMine = false; // default to not a mine

        this.xPos = xPos;
        this.yPos = yPos;

        super.addListener(new ClickEvent());
    }

    /**
     * BANG!
     */
    private void bang() {
        Toolkit.getDefaultToolkit().beep();
        VisDialog dialogue = Dialogs.showOKDialog(getStage(), "BANG!", "Game Over");
        dialogue.setModal(true);

        dialogue.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.clear();
            }
        });

        Gdx.app.log(Minesweeper.TAG, "GAME OVER!");
        this.changeImage("gfx/unlit-bomb.png");


    }

    public void setAdj(int num) {
        this.changeImage(String.format("gfx/img_num_%d.png", num));
    }

    /**
     * @return whether <code>this Button</code> is a mine or not
     */
    public boolean isMine() {
        return this.isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    // https://badlogicgames.com/forum/viewtopic.php?f=11&t=11245#
    private void changeImage(String img) {
        VisImageButtonStyle style = getStyle();
        style.checked = new SpriteDrawable(new Sprite(engine.getAssetManager().get(img, Texture.class)));
        super.setStyle(style);
        super.setChecked(true);
        super.clearListeners();
        // TableUtils.setSpacingDefaults(this);
    }

    /**
     * What to do when a button is clicked.
     */
    private final class ClickEvent extends ChangeListener {

        /**
         * @param evt   Click event
         * @param actor The event target, which is the actor that emitted the change event
         */
        @Override
        public void changed(ChangeEvent evt, Actor actor) {
            if (isChecked()) {
                return;
            }
            if (isMine) {
                bang();
            } else {
                parent.getAdjMines(xPos, yPos);
                parent.checkWin();
            }
        }
    }
}
