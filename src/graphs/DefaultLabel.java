
package graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;

import utils.FontLoader;

/**
 * Class used when adding a JLabel to a graph. Enables auto-setting of the fonts
 * and simplifies setting fonts across all components in a {@code Graph}
 */
public class DefaultLabel extends JLabel {
	private static final long serialVersionUID = 2377995574299523154L;

	/**
	 * Type of font to use for this label.
	 */
	private FontType type;

	/**
	 * Reference to the font loader from the graph using this instance of a a
	 * {@code DefaultLabel}.
	 */
	private FontLoader fontLoader;

	/**
	 * Counts the number of labels created.
	 */
	private static int idCounter = 0;

	/**
	 * ID for distinguishing between labels in the {@code equals} method. Is set
	 * to the current value of the {@code idCounter}, and then increments the
	 * counter.
	 */
	private int id = idCounter++;

	/**
	 * "Hide" the JLabel's constructor specifying the text to create this label
	 * with, and its horizontal alignment, as well as the type of font to use
	 * and the reference to the {@code FontLoader} in the graph making use of
	 * this label.
	 * 
	 * @param s
	 *            Text to initialise label with.
	 * @param i
	 *            Horizontal alignment of the label.
	 * @param type
	 *            Type of font to use for this label.
	 * @param loader
	 *            Reference to the font loader from the graph using this label.
	 */
	public DefaultLabel(String s, int i, FontType type, FontLoader loader) {
		super(s, i);
		this.fontLoader = loader;
		this.type = type;
		setup();
	}

	/**
	 * Constructor to initialise the text of this label, as well as the type of
	 * font to use and the reference to the font loader from the graph instance.
	 * 
	 * @param s
	 *            Text to initialise this label with.
	 * @param type
	 *            Type of font for this label to use.
	 * @param loader
	 *            The reference to the font loader in the graph instance.
	 */
	public DefaultLabel(String s, FontType type, FontLoader loader) {
		super(s);
		this.type = type;
		this.fontLoader = loader;
		setup();
	}

	/**
	 * Initialise a label with no text, and specifies the font type for this
	 * label to use as well as the reference to the fontloader from the instance
	 * of a graph using the label.
	 * 
	 * @param type
	 *            Type of font to use for this label.
	 * @param loader
	 *            The reference to the font loader from the instance of a graph
	 *            using this label.
	 */
	public DefaultLabel(FontType type, FontLoader loader) {
		super();
		this.type = type;
		this.fontLoader = loader;
		setup();
	}

	/**
	 * Ensures the font being used is the correct one as specified by the
	 * {@code fontLoader}.
	 */
	public void paintComponent(Graphics g) {
		Font f = g.getFont();
		if (f != null) {
			if (!getLoadedFont().equals(f)) {
				setFont(getLoadedFont());
			}
		}
		super.paintComponent(g);
	}

	/**
	 * Returns a font based on the enum {@code type}.
	 * 
	 * @return The font based on which font this label is supposed to be using.
	 */
	private Font getLoadedFont() {
		switch (type) {
			case TITLE:
				return fontLoader.getTitleFont();
			case SUB_TITLE:
				return fontLoader.getSubTitleFont();
			case TEXT:
				return fontLoader.getTextFont();
			case LABEL:
				return fontLoader.getLabelFont();
		}
		return null;
	}

	/**
	 * Initialises the label with the font that was specified upon creation of
	 * this {@code DefaultLabel}.
	 */
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
			case LABEL:
				setFont(fontLoader.getLabelFont());
			break;
		}
	}

	/**
	 * Enumerates the different types of font allowable in a default label.
	 */
	public enum FontType {
		TITLE,
		SUB_TITLE,
		TEXT,
		LABEL;
	}

	/**
	 * Returns the id of this label.
	 * 
	 * @return Returns the id of this label.
	 */
	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefaultLabel))
			return false;
		DefaultLabel other = (DefaultLabel) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
