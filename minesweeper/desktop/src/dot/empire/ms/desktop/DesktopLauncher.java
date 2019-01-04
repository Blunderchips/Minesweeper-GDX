package dot.empire.ms.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ezware.dialog.task.TaskDialogs;
import dot.empire.ms.Minesweeper;

import java.awt.*;

import static dot.empire.ms.Minesweeper.HEIGHT;
import static dot.empire.ms.Minesweeper.WIDTH;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    /**
     * @param args Arguments from the command line
     */
    public static void main(String[] args) {
        final LwjglApplicationConfiguration cfg
                = new LwjglApplicationConfiguration();

        cfg.title = Minesweeper.TAG;

        cfg.resizable = true;
        cfg.samples = 8;

        cfg.width = WIDTH;
        cfg.height = HEIGHT;

        try {
            new LwjglApplication(new Minesweeper(), cfg);
        } catch (Throwable t) {
            Toolkit.getDefaultToolkit().beep();
            t.printStackTrace(System.err);
            TaskDialogs.showException(t);
        }
    }
}
