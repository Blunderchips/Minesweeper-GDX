package dot.empire.ms.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import dot.empire.ms.Minesweeper;

import static dot.empire.ms.Defines.HEIGHT;
import static dot.empire.ms.Defines.WIDTH;

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

		cfg.resizable = false;
		cfg.samples = 8;

		cfg.width = WIDTH;
		cfg.height = HEIGHT;

		new LwjglApplication(new Minesweeper(), cfg);
	}
}
