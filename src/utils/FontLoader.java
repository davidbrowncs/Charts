
package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

public final class FontLoader {

	private final String testString = "test";

	private Font defaultFont;
	private static final float DEFAULT_TITLE_SIZE = 28f;
	private static final float DEFAULT_SUBTITLE_SIZE = 24f;
	private static final float DEFAULT_TEXT_SIZE = 20f;
	private static final float DEFAULT_LABEL_SIZE = 15f;

	private Font titleFont = null;
	private double titleFontPhysicalHeight;

	private Font subTitleFont = null;
	private double subTitlePhysicalHeight;

	private Font textFont = null;
	private double textPhysicalHeight;

	private Font labelFont = null;
	private double labelPhysicalHeight;

	private boolean loaded = false;

	public synchronized Font getTitleFont() {
		checkLoaded();
		return titleFont;
	}
	
	public synchronized void resetFonts() {
		titleFont = defaultFont.deriveFont(DEFAULT_TITLE_SIZE);
		subTitleFont = defaultFont.deriveFont(DEFAULT_SUBTITLE_SIZE);
		textFont = defaultFont.deriveFont(DEFAULT_TEXT_SIZE);
		labelFont = defaultFont.deriveFont(DEFAULT_LABEL_SIZE);
	}

	public synchronized Font getSubTitleFont() {
		checkLoaded();
		return subTitleFont;
	}

	public synchronized Font getTextFont() {
		checkLoaded();
		return textFont;
	}

	public synchronized Font getLabelFont() {
		checkLoaded();
		return labelFont;
	}

	public synchronized void setTitleFontSize(float f) {
		checkLoaded();
		titleFont = titleFont.deriveFont(f);
		titleFontPhysicalHeight = fontHeight(titleFont);
	}

	public synchronized void setSubTitleFontSize(float f) {
		checkLoaded();
		subTitleFont = subTitleFont.deriveFont(f);
		subTitlePhysicalHeight = fontHeight(subTitleFont);
	}

	public synchronized void setTextFontSize(float f) {
		checkLoaded();
		textFont = textFont.deriveFont(f);
		textPhysicalHeight = fontHeight(textFont);
	}

	public synchronized void setLabelFontSize(float f) {
		checkLoaded();
		labelFont = labelFont.deriveFont(f);
		labelPhysicalHeight = fontHeight(labelFont);
	}

	private void checkLoaded() {
		if (!loaded) {
			loadFont();
		}
	}

	public synchronized void init() {
		checkLoaded();
	}

	public synchronized void setFont(Font f) {
		checkLoaded(); // Need to load original font if not already done so to
						// initialise the sizes from the default font
		titleFont = convertFont(f, titleFontPhysicalHeight);
		subTitleFont = convertFont(f, subTitlePhysicalHeight);
		textFont = convertFont(f, textPhysicalHeight);
		labelFont = convertFont(f, labelPhysicalHeight);
	}

	private Font convertFont(Font f, double sz) {
		float size = 1f;
		Font tmp = f.deriveFont(size);
		while (fontHeight(tmp) < sz) {
			size += 0.5f;
			tmp = tmp.deriveFont(size);
		}
		return tmp;
	}

	private double fontHeight(Font f) {
		AffineTransform a = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(a, true, true);
		return f.getStringBounds(testString, frc).getHeight();
	}

	private void loadFont() {
		try {
			InputStream is = FontLoader.class.getResourceAsStream("/resources/Aaargh.ttf");
			Font tmp = Font.createFont(Font.PLAIN, is);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(tmp);

			titleFont = tmp.deriveFont(DEFAULT_TITLE_SIZE);
			titleFontPhysicalHeight = fontHeight(titleFont);

			subTitleFont = tmp.deriveFont(DEFAULT_SUBTITLE_SIZE);
			subTitlePhysicalHeight = fontHeight(subTitleFont);

			textFont = tmp.deriveFont(DEFAULT_TEXT_SIZE);
			textPhysicalHeight = fontHeight(textFont);

			labelFont = tmp.deriveFont(DEFAULT_LABEL_SIZE);
			labelPhysicalHeight = fontHeight(labelFont);

			defaultFont = tmp;

			loaded = true;
		} catch (IOException | FontFormatException e1) {
			e1.printStackTrace();
		}
	}
}
