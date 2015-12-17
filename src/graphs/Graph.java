
package graphs;

import graphs.DefaultLabel.FontType;
import interfaces.DataModel;
import interfaces.SwingObserver;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import utils.ColorGenerator;
import utils.FontLoader;

/**
 * Class specifying a lot of the meta-behaviour of a graph (colours, series
 * names etc.) and the basic functionality of a graph.
 * 
 * @param <I>
 *            The type of independent variable a graph inheriting from
 *            {@code Graph} requires.
 */
public abstract class Graph<I> extends JPanel implements SwingObserver<I, Double> {
	private static final long serialVersionUID = 9103378308225496616L;

	/**
	 * The number which if two double variables have a difference less than this
	 * number, are considered to be equal.
	 */
	protected static final double ZERO_FACTOR = Double.MIN_VALUE * Math.pow(10, 4);

	/**
	 * The maximum power of ten allowable before a value being displayed on a
	 * graph is truncated.
	 */
	protected static final int MAX_POW_10 = (int) Math.pow(10, 4);

	/**
	 * Flag to indicate whether debug mode is on or not.
	 */
	protected boolean debug = utils.Debug.isDebug();

	/**
	 * Keeps track of the number of graphs created.
	 */
	protected static int graphCounter = 0;

	/**
	 * ID of this graph. Used to distinguish between graphs via the
	 * {@code equals} method. Assigned to the current value of
	 * {@code graphCounter}, and then increments {@code graphCounter}.
	 */
	protected int id = graphCounter++;

	/**
	 * The datamodel this graph uses for drawing.
	 */
	protected DataModel<I, Double> dataModel;

	/**
	 * Collection of {@code Series}, where a series contains the meta
	 * information for a series of dependent data, and also the plotting points
	 * for that dependent data set.
	 */
	protected ArrayList<Series> series = new ArrayList<>();

	/**
	 * Instance of a font loader to get allowable fonts from.
	 */
	protected FontLoader fontLoader = new FontLoader();

	/**
	 * The panel to use for drawing to.
	 */
	protected JPanel drawingPanel = new JPanel() {
		private static final long serialVersionUID = -8697262090210209523L;

		@Override
		public void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D) g1;
			if (debug) {
				g.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
				g.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
			}
			draw(g);
		}
	};

	/************************ Drawing vars ******************************/

	/**
	 * Percent of blank space to allocate around the graph
	 */
	protected double prcntMargin = 0.10d;

	// Is multiplied by prcnt margin, so the amount extra for gridlines is (max
	// or mind data set val) + or - prcntMargin * axisPadding
	protected double axisPadding = 0.3d;

	protected static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
	protected static final Stroke DEFAULT_AXIS_STROKE = new BasicStroke(1.0f);
	protected static final Stroke DEFAULT_GRID_LINE_STROKE = new BasicStroke(1.0f);

	protected Stroke generalStroke = DEFAULT_STROKE;
	protected Stroke axisStroke = DEFAULT_AXIS_STROKE;
	protected Stroke gridLineStroke = DEFAULT_GRID_LINE_STROKE;

	/*********** Colours **********/

	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_AXIS_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
	protected static final Color DEFAULT_GRIDLINE_COLOR = new Color(176, 176, 176, 200);

	protected Color generalStrokeColor = DEFAULT_STROKE_COLOR;
	protected Color axisStrokeColor = DEFAULT_AXIS_STROKE_COLOR;
	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
	protected Color gridLineColor = DEFAULT_GRIDLINE_COLOR;

	protected int alpha = 255;

	/************************* Swing vars *******************************/
	protected JLabel title = null;

	protected String xLabel = null;
	protected String yLabel = null;

	// "Gap" between the axis lines and the axis labels
	protected int axisLabelOffset = 10;

	protected Legend legend = null;

	protected JPanel legendContainer = new JPanel();
	protected boolean legendVisible = false;

	protected MetaTracker tracker = new MetaTracker();

	protected int numSeries = 0;
	protected boolean autoColors = true;

	protected abstract void drawXLabel(Graphics2D g);

	protected abstract void drawYLabel(Graphics2D g);

	protected abstract void drawGraph(Graphics2D g);

	protected abstract void draw(Graphics2D g);

	protected abstract void convertPoints();

	public Graph() {
		fontLoader.init();
		this.setLayout(new BorderLayout());
		legend = new Legend(fontLoader);
		legend.setVisible(legendVisible);
		legendContainer.add(legend);
		add(legendContainer, BorderLayout.EAST);
		add(drawingPanel, BorderLayout.CENTER);
		this.setBackground(backgroundColor);
	}

	public void setDataModel(DataModel<I, Double> dataSet) {
		if (!dataSet.equals(this.dataModel)) {
			this.dataModel = dataSet;
			dataSet.addObserver(this);
			updated();
			legend.setSeries(series);
		}
	}

	public void setPrcntMargin(double d) {
		prcntMargin = d;
		updated();
	}

	/******************** Font changing methods **************/

	public void setTitleFontSize(float f) {
		fontLoader.setTitleFontSize(f);
		revalidate();
		repaint();
	}

	public void setSubTitleFontSize(float f) {
		fontLoader.setSubTitleFontSize(f);
		revalidate();
		repaint();
	}

	public void setTextFontSize(float f) {
		fontLoader.setTextFontSize(f);
		revalidate();
		repaint();
	}

	public void setLabelFontSize(float f) {
		fontLoader.setLabelFontSize(f);
		revalidate();
		repaint();
	}

	public void setFont(Font f) {
		if (fontLoader != null) {
			fontLoader.setFont(f);
		}
		super.setFont(f);
		changeFont(this, f);
	}

	protected void changeFont(Component component, Font font) {
		if (component != this) {
			component.setFont(font);
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeFont(child, font);
			}
		}
	}

	/*********************** Legend changing methods ****************************/

	public void legendLeftSide() {
		remove(legendContainer);
		add(legendContainer, BorderLayout.WEST);
		setLegendVisible(true);
		revalidate();
		repaint();
	}

	public void legendRightSide() {
		remove(legendContainer);
		add(legendContainer, BorderLayout.EAST);
		setLegendVisible(true);
		revalidate();
		repaint();
	}

	public void setLegendVisible(boolean b) {
		legend.setVisible(legendVisible = b);
	}

	public void setLegendTitle(String title) {
		legend.setTitle(title);
		setLegendVisible(true);
		revalidate();
		repaint();
	}

	protected void updateLegendNames() {
		if (tracker.namesUpdated(series)) {
			legend.updateSeriesNames();
		}
	}

	protected void updateLegendColors() {
		if (tracker.colorsUpdated(series)) {
			legend.updateSeriesColors();
			repaint();
		}
	}

	/*********************************************************/

	public void setLegendTransparency(double d) {
		legend.setAlphaComponent(assertInRange(d, 0, 1));
	}

	public DataModel<I, Double> getDataModel() {
		return dataModel;
	}

	public void updateDisplay() {
		repaint();
	}

	public void removeDataModel() {
		this.dataModel = null;
		updated();
		repaint();
	}

	public void setTransparency(double d) {
		alpha = utils.ColorGenerator.convertTo8Bit(assertInRange(0, 1, d));
		repaint();
	}

	protected double assertInRange(double a, double b, double d) {
		double max = Math.max(a, b);
		double min = Math.min(a, b);
		return d > max ? max : d < min ? min : d;
	}

	public void setSeriesNames(ArrayList<String> s) {
		Objects.requireNonNull(s);
		legend.setUserSpecifiedInformation(true);
		matchSeriesNames(s);
		setLegendVisible(true);
	}

	protected void matchSeriesNames(ArrayList<String> names) {
		if (series.size() <= names.size()) {
			for (int i = 0; i < series.size(); i++) {
				series.get(i).setName(names.get(i));
			}
		} else if (series.size() > names.size()) {
			for (int i = 0; i < names.size(); i++) {
				series.get(i).setName(names.get(i));
			}
			for (int i = names.size(); i < series.size(); i++) {
				series.get(i).setName("Series - " + series.size() + 1);
			}
		}
		int count = 0;
		for (int i = 0; i < series.size(); i++) {
			String s = series.get(i).getName();
			if (s == null) {
				count++;
			}
		}
		legend.setUserSpecifiedInformation(count == series.size());
		updateLegendNames();
	}

	protected void matchSeriesColors(ArrayList<Color> colors) {
		if (series.size() <= colors.size()) {
			for (int i = 0; i < series.size(); i++) {
				if (series.get(i).getColor() != colors.get(i)) {
					series.get(i).setColor(colors.get(i));
				}
			}
		} else if (colors.size() < series.size()) {
			for (int i = 0; i < colors.size(); i++) {
				if (series.get(i).getColor() != colors.get(i)) {
					series.get(i).setColor(colors.get(i));
				}
			}
			for (int i = colors.size(); i < series.size(); i++) {
				series.get(i).setColor(ColorGenerator.getColor(alpha));
			}
		}
		updateLegendColors();
	}

	public void setSeriesColors(ArrayList<Color> colors) {
		Objects.requireNonNull(dataModel);
		updated();
		legend.setUserSpecifiedInformation(true);
		autoColors = false;
		matchSeriesColors(colors);
	}

	public void setSeriesColor(int seriesNum, Color c) {
		Objects.requireNonNull(dataModel);
		updated();
		if (seriesNum < 0 || seriesNum >= series.size()) {
			throw new IndexOutOfBoundsException();
		}
		legend.setUserSpecifiedInformation(true);
		series.get(seriesNum).setColor(c);
		autoColors = false;
		updateLegendColors();
	}

	public void setSeriesName(int seriesNum, String text) {
		updated();
		if (seriesNum < 0 || seriesNum >= series.size()) {
			throw new IndexOutOfBoundsException();
		}
		legend.setUserSpecifiedInformation(true);
		setLegendVisible(true);
		series.get(seriesNum).setName(text);
		updateLegendNames();
	}

	/**
	 * If colours have been assigned to the series in the graph, this method
	 * will clear all the current colours assigned to data-series, and will
	 * assign new ones from the ColorGenerator class.
	 */
	public void autoColors() {
		if (autoColors) {
			return;
		}
		// Give it an empty list so it'll get colours from generator
		matchSeriesColors(new ArrayList<Color>());
		autoColors = true;
		repaint();
	}

	/**
	 * Sets the background colour of the legend. The new colour will be derived
	 * from the given colour with the current legend's alpha component. For
	 * example, if the color {@code new Color(255, 255, 255, 255)} is given, and
	 * the legend's alpha value is 150, the new colour of the legend will be
	 * {@code new Color(255, 255, 255, 150)}.
	 * 
	 * @param c
	 *            The colour to assign to the legend.
	 */
	public void setLegendBackgroundColor(Color c) {
		legend.setBackgroundColor(c);
	}

	protected boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < ZERO_FACTOR;
	}

	/**
	 * Not necessary to ever call this. Used by the {@code DataSet} class to
	 * update a graph once the dataset has been changed. If this method is
	 * called, it will re-calculate the points to draw based on the graph's
	 * dataset. It will also ensure that there is an appropriate number of
	 * colours to draw series with, and will update the legend with these
	 * colours too, if the legend is visible.
	 */
	public void updated() {
		if (dataModel != null) {
			convertPoints();
		}
		updateLegendColors();
		updateLegendNames();
	}

	/**
	 * Adds a title to the graph.
	 * 
	 * @param title
	 *            Value of the new title.
	 */
	public void setTitle(String title) {
		if (this.title != null) {
			remove(this.title);
		}
		this.title = new DefaultLabel(title, SwingConstants.CENTER, FontType.TITLE, fontLoader);
		add(this.title, BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	/**
	 * Set a label to draw on the graph, labelling the y axis. drawn on under
	 * the x-axis of the graph image. To remove a label once set, call
	 * "setXLabel(null)" and it will be removed. Labels are off by default. Take
	 * care that the label is not too long, as there is no text wrapping
	 * implemented.
	 * 
	 * @param lbl
	 *            The string To assign to be the x label
	 */
	public void setXLabel(String lbl) {
		xLabel = lbl;
		repaint();
	}

	/**
	 * Set a label to draw on the graph, labelling the y axis. This will be
	 * rotated 270 or -90 degrees and drawn on the left hand side of the graph
	 * image. To remove a label once set, call "setYLabel(null)" and it will be
	 * removed. Labels are off by default. Take care that the label is not too
	 * long, as there is no text wrapping implemented.
	 * 
	 * @param lbl
	 *            The string To assign to be the y label
	 */
	public void setYLabel(String lbl) {
		yLabel = lbl;
		repaint();
	}

	/**
	 * Used to set the percentage margin around the edge of the graph. Limited
	 * to between 0.03 (3%) and 0.5(50%). Any given values outside this range
	 * will be reset to within the range.
	 * 
	 * @param d
	 *            The new value of the margin around the graph as a percentage
	 */
	public void setPercentMargin(double d) {
		d = assertInRange(0.03, 0.5, d);
		prcntMargin = d;
		updated();
		repaint();
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
		if (!(obj instanceof Graph))
			return false;
		@SuppressWarnings("rawtypes")
		Graph other = (Graph) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
