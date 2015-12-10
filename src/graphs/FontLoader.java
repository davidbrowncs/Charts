
package graphs;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class FontLoader {

	private static Font titleFont = null;
	private static Font subTitleFont = null;
	private static Font textFont = null;
	private static Font tinyFont = null;
	private static boolean loaded = false;

	private FontLoader() {}

	public synchronized static Font getTitleFont() {
		checkLoaded();
		return titleFont;
	}

	public synchronized static Font getSubTitleFont() {
		checkLoaded();
		return subTitleFont;
	}

	public synchronized static Font getTextFont() {
		checkLoaded();
		return textFont;
	}

	public synchronized static Font getTinyFont() {
		checkLoaded();
		return tinyFont;
	}

	private static void checkLoaded() {
		if (!loaded) {
			loadFont();
		}
		loaded = true;
	}

	public synchronized static void init() {
		new Thread(() -> {
			loadFont();
		}).start();
	}

	private static void loadFont() {
		try {
			InputStream is = FontLoader.class.getResourceAsStream("/resources/Aaargh.ttf");
			Font tmp = Font.createFont(Font.PLAIN, is);
			titleFont = tmp.deriveFont(28f);
			textFont = tmp.deriveFont(20f);
			subTitleFont = tmp.deriveFont(24f);
			tinyFont = tmp.deriveFont(15f);
		} catch (IOException | FontFormatException e1) {
			e1.printStackTrace();
		}
	}
}
