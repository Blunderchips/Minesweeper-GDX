package dot.empire.ms;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;

import static dot.empire.ms.Minesweeper.*;

/**
 * Playing field. Main game screen.
 */
public class ScreenGame extends Scene {

    /**
     * Field size X-axis.
     */
    private int x = 22;
    /**
     * Field size Y-axis.
     */
    private int y = 23;

    private VisTable table;
    private VisTable rootTable;
    private Mine[][] field;

    public ScreenGame() {
        this.rootTable = new VisTable(true);
        this.rootTable.setFillParent(true);
        this.rootTable.align(Align.center);

        this.table = new VisTable(true);
        this.table.setFillParent(false);
        this.table.align(Align.topLeft);
        // this.table.setVisible(false);

        this.field = new Mine[y][x];

        // --
        this.rootTable.add(table);
    }

    @Override
    public void show() {
        super.show();
        getEngine().getStage().addActor(rootTable);
        // this.table.setVisible(true);
//        this.table.setVisible(true);

        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                this.field[x][y] = new Mine(x, y, this);
                this.table.add(field[x][y]);
            }
            this.table.row();
        }

//        this.field[0][0].setMine(true);

        int num = 0;
        while (num != Math.min(NUM_MINES, WIDTH * HEIGHT)) { // cant have more mine than the size of the field
            int x = MathUtils.random(field.length - 1);
            int y = MathUtils.random(field[0].length - 1);

            if (!field[x][y].isMine()) {
                Gdx.app.debug(Minesweeper.TAG, String.format("(%d;%d) is a mine", y, x));
                this.field[x][y].setMine(true);
                num++;
            }
        }
//        this.table.add(new Mine(getEngine()));
    }

    @Override
    public void hide() {
        // this.table.setVisible(false);
        this.rootTable.remove();
    }

    public void getAdjMines(int x, int y) {
        try {
            if (field[x][y].isChecked() || field[x][y].isMine()) {
                return;
            }
        } catch (IndexOutOfBoundsException ignore) {
            return;
        }

        int num = 0;

        num += getAdjMines(x, y, 1, 0);
        num += getAdjMines(x, y, -1, 0);

        num += getAdjMines(x, y, 1, 1);
        num += getAdjMines(x, y, 1, -1);

        num += getAdjMines(x, y, -1, 1);
        num += getAdjMines(x, y, -1, -1);

        num += getAdjMines(x, y, 0, 1);
        num += getAdjMines(x, y, 0, -1);

        this.field[x][y].setAdj(num);

        if (num == 0) {
            getAdjMines(x + 1, y);
            getAdjMines(x, y - 1);

            getAdjMines(x + 1, y + 1);
            getAdjMines(x + 1, y - 1);

            getAdjMines(x - 1, y + 1);
            getAdjMines(x - 1, y - 1);

            getAdjMines(x, y + 1);
            getAdjMines(x, y - 1);
        }
    }

    private int getAdjMines(int x, int y, int offsetX, int offsetY) {
        try {
            if (field[x + offsetX][y + offsetY].isMine()) {
                return 1;
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
        return 0;
    }

    public void clear() {
        Gdx.app.debug(Minesweeper.TAG, "New game");
        getEngine().setScreen(new ScreenGame());
    }

    public void checkWin() {
        int numChecked = 0;
        for (Mine[] mines : field) {
            for (int y = 0; y < field[0].length; y++) {
                numChecked += mines[y].isChecked() ? 1 : 0;
            }
        }
        Gdx.app.debug(TAG, String.format("check = %d, o = %d", numChecked, (x * y) - NUM_MINES));
        if (numChecked >= (x * y) - NUM_MINES) {
            VisDialog dialogue = Dialogs.showOKDialog(getEngine().getStage(), "Winner!", "You Win!");
            dialogue.setModal(true);

            dialogue.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    clear();
                }
            });
        }
    }
}
