
package graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;

import fileHandling.FontLoader;

public class DefaultLabel extends JLabel {
	private static final long serialVersionUID = 2377995574299523154L;

	private FontType type;
	private FontLoader fontLoader;

	private static int idCounter = 0;
	private int id = idCounter++;
	
	public DefaultLabel(String s, int i, FontType type, FontLoader loader) {
		super(s, i);
		this.fontLoader = loader;
		this.type = type;
		setup();
	}

	public DefaultLabel(String s, FontType type, FontLoader loader) {
		super(s);
		this.type = type;
		this.fontLoader = loader;
		setup();
	}

	public DefaultLabel(FontType type, FontLoader loader) {
		super();
		this.type = type;
		this.fontLoader = loader;
		setup();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Font f = g.getFont();
		if (f != null) {
			if (!getLoadedFont().equals(f)) {
				setFont(getLoadedFont());
			}
		}
	}

	private Font getLoadedFont() {
		switch (type) {
			case TITLE:
				return fontLoader.getTitleFont();
			case SUB_TITLE:
				return fontLoader.getSubTitleFont();
			case TEXT:
				return fontLoader.getTextFont();
			case TINY:
				return fontLoader.getLabelFont();
		}
		return null;
	}

	private void setup() {
		if (utils.Debug.isDebug()) {
			this.setOpaque(true);
			this.setBackground(new Color(0, 255, 204, 100));
		}

		switch (type) {
			case TITLE:
				setFont(fontLoader.getTitleFont());
			break;
			case SUB_TITLE:
				setFont(fontLoader.getSubTitleFont());
			break;
			case TEXT:
				setFont(fontLoader.getTextFont());
			break;
			case TINY:
				setFont(fontLoader.getLabelFont());
			break;
		}
	}

	public enum FontType {
		TITLE,
		SUB_TITLE,
		TEXT,
		TINY;
	}
}
