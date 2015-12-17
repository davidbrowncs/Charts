
package graphs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utils.FontLoader;
import utils.GraphicsAuxiliary;
import graphs.DefaultLabel.FontType;

/**
 * Displays the legend data, i.e. a legend title, the name given to different
 * data series, and the colours they are being drawn with.
 */
class Legend extends JPanel {
	private static final long serialVersionUID = 5725435102513897890L;

	/**
	 * Title for the legend
	 */
	private JLabel legendTitle = null;

	/**
	 * Reference to the series of data from the graph which is using this
	 * instance of a Legend
	 */
	private List<Series> series = null;

	/**
	 * References to the names of each series, used to remove the labels when
	 * then names of one or more data series changes.
	 */
	private ArrayList<JLabel> labels = new ArrayList<>();

	/**
	 * Similar to {@code labels}, keeps references to the Color box panels so
	 * they can be removed upon updating the colours for display data series'.
	 */
	private ArrayList<ColorBox> colorBoxes = new ArrayList<>();

	/**
	 * Used to calculate the maximum allowable with for a series name. The
	 * length is arbitrary, it is just what seems a reasonable length of text to
	 * reach before starting to wrap.
	 */
	private final static String MAX_STRING_SAMPLE = "Long text sampleaaaaa";

	/**
	 * "Pixel" value for the maximum allowable text width. Dependent upon the
	 * font and the text used to calculate it ({@code MAX_STRING_SAMPLE}). Pixel
	 * value calculated once initially with the default font and stored.
	 */
	private int maxLabelSize;

	/**
	 * Alpha value for the background colour. Initialise to 0 so the background
	 * is completely transparent.
	 */
	private int alpha = 0;

	/**
	 * Default background color, grey with an alpha component dependent on
	 * {@code alpha}.
	 */
	private Color backgroundColor = new Color(150, 150, 150, alpha);

	/**
	 * Reference to the font loader from the legend's graph parent. Used to set
	 * the fonts for the title and for the series names.
	 */
	private FontLoader fontLoader;

	/**
	 * Flag to indicate whether the user has specified series data or not. If
	 * the user specifies series information, such as a series name or colour,
	 * this will be set to true, if the user has not it is false. If they add
	 * some information and then reset the graph back to using default or
	 * automated names and colours, this is set back to false, to indicate not
	 * to add labels to the legend upon updating.
	 */
	private boolean userSpecifiedSeriesInformation = false;

	/**
	 * Non public constructor since this is only a auxiliar class for a graph.
	 * 
	 * @param loader
	 *            The loader from graph containing this legend.
	 */
	Legend(FontLoader loader) {
		/* Window builder code */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		/* End of window builder code */

		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(backgroundColor);
		this.fontLoader = loader;

		// Initialise and store the maximum allowable text width.
		AffineTransform aff = new AffineTransform();
		FontRenderContext fontRenderContext = new FontRenderContext(aff, true, true);
		maxLabelSize = (int) fontLoader.getTextFont().getStringBounds(MAX_STRING_SAMPLE, fontRenderContext).getWidth();
	}

	/**
	 * Not used too often. Since a graph can be initialised without a dataset,
	 * we do not know when the series will be initialised by the graph. This is
	 * called upon a user setting the dataset, and the list of series objects
	 * being generated.
	 * 
	 * @param series
	 *            The series to assign to this legend.
	 */
	void setSeries(ArrayList<Series> series) {
		this.series = series;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1.create();
		super.paintComponent(g1);

		// Ensure the anti-aliasing properties are set properly
		GraphicsAuxiliary.setupAA(g);

		// Rounded corners rectangle
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 25, 25);
		Color oldColor = g.getColor();
		g.setColor(backgroundColor);
		g.fill(rect);
		g.setColor(oldColor);
		g.dispose();
	}

	/**
	 * Sets the background colour of the legend and causes it to be repainted
	 * with the new colour. The new colour is derived from the given colour,
	 * with its alpha component set to that of {@code this.alpha}.
	 * 
	 * @param c
	 *            The new colour for the legend's background
	 */

	void setBackgroundColor(Color c) {
		backgroundColor = utils.ColorGenerator.convertToAlpha(c, alpha);
		this.repaint();
	}

	/**
	 * Sets the title of the legend. First removes all components to ensure that
	 * if series labels were set before specifying a title, the legend will be
	 * added in the correct cell, then readds the components.
	 * 
	 * @param s
	 *            Name to give to the title.
	 */
	void setTitle(String s) {
		if (legendTitle != null) {
			remove(legendTitle);
		}
		removeAll();
		legendTitle = new DefaultLabel(s, FontType.SUB_TITLE, fontLoader);

		/* Window builder code */
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(legendTitle, gbc_lblNewLabel);
		/* End window builder code */

		/*
		 * Only add series details if the series has been initialised and user
		 * has set series data
		 */
		if (series != null && userSpecifiedSeriesInformation) {
			updateSeriesNames();
			updateSeriesColors();
		}
	}

	/**
	 * Try to update the series labels if the series list has been updated.
	 * First checks if the names in the list of data-series are the same as they
	 * are currently, if they are, return false to indicate nothing changed,
	 * otherwise removeall the labels and add updated ones.
	 * 
	 * @return True if the labels were updated, false if not (the ones in the
	 *         series are the same as the labels currently added to the legend,
	 *         or the user has not specified series names or colours.)
	 */
	boolean updateSeriesNames() {
		for (JLabel l : labels) {
			remove(l);
		}
		if (!userSpecifiedSeriesInformation) {
			return false;
		}
		labels.clear();
		int counter = legendTitle == null ? 0 : 1;

		for (int i = 0; i < series.size(); i++, counter++) {
			String name = series.get(i).getName();
			if (name == null) {
				// If the name is null, indicates that it needs to be removed,
				// so a colour box should not be added
				continue;
			}
			JLabel l = new DefaultLabel(name, FontType.TEXT, fontLoader);
			Font font = l.getFont();
			// Add html for wrapping
			l.setText("<html><body style=\fontFamily: " + font.getFamily() + "\" size =\"" + font.getSize() + "\"></font>"
					+ l.getText() + "</html");
			// Force it to wrap upon exceeding the maximum size, don't really
			// care what it's maximum height is.
			l.setMaximumSize(new Dimension(maxLabelSize, Integer.MAX_VALUE));
			labels.add(l);

			/* Window builder code */
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_1.gridx = 1;
			gbc_lblNewLabel_1.gridy = counter;
			add(l, gbc_lblNewLabel_1);
			/* End window builder code */
		}
		updateSeriesColors();
		this.revalidate();
		this.repaint();
		return true;
	}

	/**
	 * Set the flag indicating whether the user has specified series
	 * meta-information or not.
	 * 
	 * @param b
	 *            The flag value to set {@code userSpecifiedSeriesInformation}
	 *            to.
	 */
	void setUserSpecifiedInformation(boolean b) {
		userSpecifiedSeriesInformation = b;
	}

	boolean updateSeriesColors() {
		for (ColorBox c : colorBoxes) {
			remove(c);
		}
		if (!userSpecifiedSeriesInformation) {
			return false;
		}
		colorBoxes.clear();
		int counter = legendTitle == null ? 0 : 1;
		for (int i = 0; i < series.size(); i++, counter++) {
			if (series.get(i).getName() == null) {
				continue;
			}
			ColorBox c = new ColorBox(series.get(i).getColor());
			colorBoxes.add(c);

			/* Window builder code */
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = counter;
			add(c, gbc_panel);
			/* End window builder code */
		}
		this.revalidate();
		this.repaint();
		return true;
	}

	/**
	 * Sets the alpha component for the background colour, and also sets all the
	 * background colours of children components to use this new alpha value.
	 * This value needs to be between 0 and 1, as it will be multiplied by 255
	 * to obtain the 8-bit value. Also causes the legend to be repainted.
	 * 
	 * @param a
	 *            The "factor" by which to multiply 255 by to obtain a new alpha
	 *            value for the background colour.
	 */
	void setAlphaComponent(double a) {
		int nv = utils.ColorGenerator.convertTo8Bit(a);
		if (nv != alpha) {
			backgroundColor = utils.ColorGenerator.convertToAlpha(backgroundColor, nv);
			alpha = nv;
			changeAlpha(this, alpha);
		}
		repaint();
	}

	private void changeAlpha(Component component, int alpha) {
		if (component != this) {
			component.setBackground(utils.ColorGenerator.convertToAlpha(component.getBackground(), alpha));
		}
		if (component instanceof Legend.ColorBox) {
			((ColorBox) component).color = utils.ColorGenerator.convertToAlpha(((ColorBox) component).color, alpha);
		} else if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeAlpha(child, alpha);
			}
		}
	}

	/**
	 * Auxiliary class for drawing coloured boxes, to indicate which series is
	 * being drawn with which colour.
	 */
	private class ColorBox extends JPanel {
		private static final long serialVersionUID = 5732330488332905113L;

		/**
		 * Width and height of the box to be drawn.
		 */
		private final int width = 15;
		private final int height = 15;
		private Color color;

		private ColorBox(Color c) {
			this.color = c;
			this.setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			int hc = this.getWidth() / 2;
			int vc = this.getHeight() / 2;

			g.setColor(color);
			int x = hc - (width / 2);
			int y = vc - (height / 2);
			g.fillRect(x, y, width, height);
		}
	}
}
